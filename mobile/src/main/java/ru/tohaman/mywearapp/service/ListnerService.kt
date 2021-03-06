package ru.tohaman.mywearapp.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import com.acrcloud.rec.sdk.ACRCloudClient
import com.acrcloud.rec.sdk.ACRCloudConfig
import com.acrcloud.rec.sdk.IACRCloudListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import org.json.JSONException
import org.json.JSONObject
import ru.tohaman.mywearapp.DeveloperKey
import ru.tohaman.mywearapp.DeveloperKey.SEND_DATA
import ru.tohaman.mywearapp.DeveloperKey.SEND_DATA_KEY
import ru.tohaman.mywearapp.DeveloperKey.TAG
import ru.tohaman.mywearapp.data.MusicItem
import ru.tohaman.mywearapp.data.musicDatabase
import ru.tohaman.mywearapp.ioThread
import timber.log.Timber
import java.io.IOException
import java.util.*


class ListenerService : WearableListenerService(), IACRCloudListener {

    private var startTime: Long = 0
    private var stopTime: Long = 0

    private var mClient: ACRCloudClient? = null
    private lateinit var mConfig: ACRCloudConfig

    private var mProcessing = false
    private var initState = false

    //private var dao: MusicItemDao? = null


    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate ListnerService")
        mConfig = ACRCloudConfig()
        mConfig.acrcloudListener = this

        // If you implement IACRCloudResultWithAudioListener and override "onResult(ACRCloudResult result)", you can get the Audio data.
        //this.mConfig.acrcloudResultWithAudioListener = this;

        mConfig.context = this
        mConfig.host = DeveloperKey.DK_ACRCloudHostKey
        mConfig.accessKey =
            DeveloperKey.DK_ACRCloudAccessKey
        mConfig.accessSecret =
            DeveloperKey.DK_ACRCloudAccessSecret
        mConfig.protocol = ACRCloudConfig.ACRCloudNetworkProtocol.PROTOCOL_HTTPS // PROTOCOL_HTTP
        mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE

        mClient = ACRCloudClient()
        initState = mClient!!.initWithConfig(mConfig)
    }

    private fun sendMessage2Wear(message : String, key: String = SEND_DATA_KEY, path: String = SEND_DATA) {
        Timber.d("key = $key , message = $message")

        val dataClient = Wearable.getDataClient(this)

        val putDataReq: PutDataRequest = PutDataMapRequest.create(path).run {
            dataMap.putString(key, message)
            asPutDataRequest()
            // Добавим, что данные надо передать срочно, а не отложенно
        }.setUrgent()

        val putDataTask: Task<DataItem> = dataClient.putDataItem(putDataReq)
        //тут еще по идее можно обработать результат таска
        //mGoogleApiClient.disconnect(); mGoogleApiClient.connect();
    }


    private fun startRecognize() {
        Timber.d("startRecoginze")
        if (!initState) {
            sendMessage2Wear("Start init error")
            return
        }

        if (!mProcessing) {
            mProcessing = true
            if (mClient == null || !mClient!!.startRecognize()) {
                mProcessing = false
                sendMessage2Wear("Start error")
            }
            startTime = System.currentTimeMillis()
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        if (Log.isLoggable(TAG, Log.INFO)) {
            Timber.i( "onDataChanged: $dataEvents")
        }
        // Поскольку это сервис, то он уже отсеивает Event по фильтру в манифесте
        // поэтому немного отличается от того, что в приложении для часов

        dataEvents.map { it.dataItem.uri }
            .forEach { uri ->
                Timber.d( "onDataChanged.Item: ${uri.path}")
                // Get the node id from the host value of the URI
                //val nodeId: String? = uri.host
                // Set the data of the message to be the bytes of the URI
                val payload: ByteArray = uri.toString().toByteArray()

                sendMessage2Wear("?")
                startRecognize()
                // Send the RPC
                //Wearable.getMessageClient(this)
                //    .sendMessage(nodeId, DATA_ITEM_RECEIVED_PATH, payload)
            }
    }

    override fun onResult(result: String?) {
        Timber.d("onResult ListnerService")
        if (mClient != null) {
            mClient!!.cancel()
            mProcessing = false
        }

        if (result != null) {
            var tres = "\n"

            var outTitle = ""
            var outArtist = ""

            try {
                val j = JSONObject(result)
                val j1 = j.getJSONObject("status")
                val j2 = j1.getInt("code")
                if (j2 == 0) {
                    val metadata = j.getJSONObject("metadata")
                    //
                    if (metadata.has("music")) {
                        val musics = metadata.getJSONArray("music")
//                    for (i in 0 until musics.length()) {
                        for (i in 0 until 1) {      //берем только одну запись из всех найденных (если найдены конечно)
                            val tt = musics.get(i) as JSONObject
                            outTitle = tt.getString("title")
                            val artistt = tt.getJSONArray("artists")
                            val art = artistt.get(0) as JSONObject
                            outArtist = art.getString("name")
                            tres =
                                tres + (i + 1) + ".  Title: " + outTitle + "    Artist: " + outArtist + "\n"
                        }
                    }
                    tres = tres + "\n\n" + result
                } else {
                    tres = result ?: "\n"
                }
            } catch (e: JSONException) {
                tres = result ?: ""
                e.printStackTrace()
            }
            sendMessage2Wear("$stopTime : $outArtist")
            ioThread {
                try {
                    val dao = musicDatabase.musicDao
                    dao.insert(MusicItem(0, outArtist, outTitle, stopTime, Date(), result))
                } catch (e: IOException) {
                    Timber.d("room.dao.exception")
                }
            }
            if ((outArtist != "") or (outArtist != "?")) oneShotVibration()
        }
    }

    private fun oneShotVibration() {
        Timber.d("oneShortVibration ListnerService")
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500)
        }

    }




    override fun onVolumeChanged(volume: Double) {
        stopTime = (System.currentTimeMillis() - startTime) / 1000
    }

}