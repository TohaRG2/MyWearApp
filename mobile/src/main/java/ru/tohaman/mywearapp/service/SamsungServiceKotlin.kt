package ru.tohaman.mywearapp.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import com.acrcloud.rec.sdk.ACRCloudClient
import com.acrcloud.rec.sdk.ACRCloudConfig
import com.acrcloud.rec.sdk.IACRCloudListener
import com.samsung.android.sdk.SsdkUnsupportedException
import com.samsung.android.sdk.accessory.*
import org.json.JSONException
import org.json.JSONObject
import ru.tohaman.mywearapp.DeveloperKey
import ru.tohaman.mywearapp.data.MusicItem
import ru.tohaman.mywearapp.data.musicDatabase
import ru.tohaman.mywearapp.ioThread
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SamsungServiceKotlin(private val mContext: Context) :
    SAAgentV2(TAG, mContext),
    IACRCloudListener {

    private var startTime: Long = 0
    private var stopTime: Long = 0

    private var mMessage: SAMessage? = null
    private var mToast: Toast? = null
    private var watchAgent: SAPeerAgent? = null

    private var mClient: ACRCloudClient? = null
    private lateinit var mConfig: ACRCloudConfig

    private var mProcessing: Processing = Processing.Waiting
    private var initState = false

    override fun onFindPeerAgentsResponse(peerAgents: Array<SAPeerAgent>?, result: Int) {
        Timber.d("$TAG onFindPeerAgentResponse : result =$result")
        watchAgent = peerAgents?.get(0)
    }

    override fun onAuthenticationResponse(
        peerAgent: SAPeerAgent,
        authToken: SAAuthenticationToken,
        error: Int
    ) {
        /*
         * The authenticatePeerAgent(peerAgent) API may not be working properly depending on the firmware
         * version of accessory device. Please refer to another sample application for Security.
         */
    }

    override fun onError(peerAgent: SAPeerAgent, errorMessage: String, errorCode: Int) {
        super.onError(peerAgent, errorMessage, errorCode)
    }

    fun sendData(peerAgent: SAPeerAgent?, message: String): Int {
        val tid: Int
        return if (mMessage != null) {
            try {
                tid = mMessage?.send(peerAgent, message.toByteArray()) ?: -1
                tid
            } catch (e: IOException) {
                e.printStackTrace()
                displayToast(e.message, Toast.LENGTH_SHORT)
                -1
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                displayToast(e.message, Toast.LENGTH_SHORT)
                -1
            }
        } else -1
    }

    private fun processUnsupportedException(e: SsdkUnsupportedException): Boolean {
        e.printStackTrace()
        val errType = e.type
        if (errType == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED
            || errType == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED
        ) {
            /*
             * Your application can not use Samsung Accessory SDK. You application should work smoothly
             * without using this SDK, or you may want to notify user and close your app gracefully (release
             * resources, stop Service threads, close UI thread, etc.)
             */
        } else if (errType == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
            Timber.e("You need to install Samsung Accessory SDK to use this application.")
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED) {
            Timber.e("You need to update Samsung Accessory SDK to use this application.")
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_RECOMMENDED) {
            Timber.e("We recommend that you update your Samsung Accessory SDK before using this application.")
            return false
        }
        return true
    }

    private fun displayToast(str: String?, duration: Int) {
        if (mToast != null) {
            mToast!!.cancel()
        }
        mToast = Toast.makeText(applicationContext, str, duration)
        mToast?.show()
    }

    companion object {
        private const val TAG = "MyWearAppSAP"
    }

    init {
        val mAccessory = SA()
        try {
            mAccessory.initialize(mContext)
        } catch (e: SsdkUnsupportedException) {
            // try to handle SsdkUnsupportedException
            if (processUnsupportedException(e)) {
//                return
            }
        } catch (e1: Exception) {
            e1.printStackTrace()
            /*
             * Your application can not use Samsung Accessory SDK. Your application should work smoothly
             * without using this SDK, or you may want to notify user and close your application gracefully
             * (release resources, stop Service threads, close UI thread, etc.)
             */
        }
        mMessage = object : SAMessage(this) {
            override fun onSent(peerAgent: SAPeerAgent, id: Int) {
                Timber.d("$TAG onSent(), id: $id ToAgent: ${peerAgent.peerId}")
            }

            override fun onError(peerAgent: SAPeerAgent, id: Int, errorCode: Int) {
                Timber.d("$TAG onError(), id: $id ToAgent: ${peerAgent.peerId} errorCode: $errorCode")
            }

            override fun onReceive(peerAgent: SAPeerAgent, message: ByteArray) {
                Timber.d("$TAG onReceive(), FromAgent : ${peerAgent.peerId} Message : ${String(message)}")
                watchAgent = peerAgent
//                displayToast("MESSAGE RECEIVED", Toast.LENGTH_SHORT)  //для сервиса не имеет смысла
                val strToUpdateUI = String(message)
                //Если получили startRecognize - распознаем песню, иначе отправляем то, что получили, прибавив к этому текущее время
                if (strToUpdateUI == "startRecognize") {
                    startRecognize()
                    sendMessage2Wear("?")
                } else {
                    val calendar: Calendar = GregorianCalendar()
                    val dateFormat = SimpleDateFormat("yyyy.MM.dd aa hh:mm:ss.SSS", Locale.getDefault())
                    val timeStr = " " + dateFormat.format(calendar.time)
                    val str = "$strToUpdateUI $timeStr by MyWearKotlinService"
                    sendMessage2Wear(str)
                }
            }
        }
        arcCloudInit(mContext)
    }

    private fun arcCloudInit(ctx: Context) {
        Timber.d("arcCloudInit")
        mConfig = ACRCloudConfig()
        mConfig.acrcloudListener = this

        mConfig.context = ctx
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

    private fun startRecognize() {
        Timber.d("startRecoginze")
        if (!initState) {
            sendMessage2Wear("Start init error")
            return
        }

        if (mProcessing == Processing.Waiting) {
            mProcessing = Processing.Recognize
            if (mClient == null || !mClient!!.startRecognize()) {
                mProcessing = Processing.Waiting
                sendMessage2Wear("Start error")
            }
            startTime = System.currentTimeMillis()
        }
    }

    override fun onResult(result: String?) {
        Timber.d("onResult SamsungService")
        if (mClient != null) {
            mClient!!.cancel()
            mProcessing = Processing.Waiting
        }
        stopTime = (System.currentTimeMillis() - startTime) / 1000

        result?.let{
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
                        for (i in 0 until 1) {      //берем только одну запись из всех найденных (если найдены конечно)
                            val tt = musics.get(i) as JSONObject
                            outTitle = tt.getString("title")
                            val artistt = tt.getJSONArray("artists")
                            val art = artistt.get(0) as JSONObject
                            outArtist = art.getString("name")
                            tres = tres + (i + 1) + ".  Title: " + outTitle + "    Artist: " + outArtist + "\n"
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

    @Suppress("DEPRECATION")
    private fun oneShotVibration() {
        Timber.d("oneShortVibration SamsungService")
        val vibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500)
        }

    }

    override fun onVolumeChanged(p0: Double) {
//        stopTime = (System.currentTimeMillis() - startTime) / 1000
    }

    private fun sendMessage2Wear(str: String) {
        watchAgent?.let {
            Thread { sendData(it, str) }.start()
        }
    }

    enum class Processing {
        Waiting, Recognize
    }
}