package ru.tohaman.mywearapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.acrcloud.rec.sdk.ACRCloudClient
import com.acrcloud.rec.sdk.ACRCloudConfig
import com.acrcloud.rec.sdk.IACRCloudListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk15.coroutines.onClick
import org.json.JSONException
import org.json.JSONObject
import ru.tohaman.mywearapp.DeveloperKey.DK_ACRCloudAccessKey
import ru.tohaman.mywearapp.DeveloperKey.DK_ACRCloudAccessSecret
import ru.tohaman.mywearapp.DeveloperKey.DK_ACRCloudHostKey
import java.io.File
import ru.tohaman.mywearapp.DeveloperKey.TAG
import ru.tohaman.mywearapp.DeveloperKey.SEND_DATA
import ru.tohaman.mywearapp.DeveloperKey.SEND_DATA_KEY
import ru.tohaman.mywearapp.DeveloperKey.START_REC

class MainActivity : AppCompatActivity(), IACRCloudListener {

    private lateinit var volumeTextView: TextView
    private lateinit var resultTextView: TextView
    private lateinit var timeTextView: TextView

    private var mClient: ACRCloudClient? = null
    private lateinit var mConfig: ACRCloudConfig

    private var mProcessing = false
    private var initState = false

    private var path = ""

    private var startTime: Long = 0
    private var stopTime: Long = 0

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission()

        path = "${Environment.getExternalStorageDirectory()}/AudioTag"

        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }

        verticalLayout {
            padding = dip(30)

            button ("Play") {
                textSize = 26f }.onClick { playStart() }

            button ("PlayStop") {
                textSize = 26f }.onClick { playStop() }

            button ("Cancel") {
                textSize = 26f }.onClick { playCancel() }

            button (resources.getString(R.string.send)) {
                textSize = 26f }.onClick { sendMessage2Wear("Данные с телефона ${count++}") }

            volumeTextView = textView ("Громкость:")

            timeTextView = textView ("0")

            resultTextView = textView("тут результат запроса")

        }

        mConfig = ACRCloudConfig()
        mConfig.acrcloudListener = this@MainActivity

        // If you implement IACRCloudResultWithAudioListener and override "onResult(ACRCloudResult result)", you can get the Audio data.
        //this.mConfig.acrcloudResultWithAudioListener = this;

        mConfig.context = this
        mConfig.host = DK_ACRCloudHostKey
        mConfig.dbPath = path // offline db path, you can change it with other path which this app can access.
        mConfig.accessKey = DK_ACRCloudAccessKey
        mConfig.accessSecret = DK_ACRCloudAccessSecret
        mConfig.protocol = ACRCloudConfig.ACRCloudNetworkProtocol.PROTOCOL_HTTPS // PROTOCOL_HTTP
        mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE
        //mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_LOCAL
        //mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_BOTH

        mClient = ACRCloudClient()
        // If reqMode is REC_MODE_LOCAL or REC_MODE_BOTH,
        // the function initWithConfig is used to load offline db, and it may cost long time.
        initState = mClient!!.initWithConfig(mConfig)
        if (initState) {
            //            this.mClient.startPreRecord(3000); //start prerecord, you can call "this.mClient.stopPreRecord()" to stop prerecord.
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this@MainActivity, permissions, 0)
        }
    }


    private fun playStart() {
        if (!initState) {
            Toast.makeText(this, "init error", Toast.LENGTH_SHORT).show()
            return
        }

        if (!mProcessing) {
            mProcessing = true
            volumeTextView.text = ""
            resultTextView.text = ""
            if (mClient == null || !mClient!!.startRecognize()) {
                mProcessing = false
                resultTextView.text = "start error!"
            }
            startTime = System.currentTimeMillis()
        }
    }

    private fun playStop() {
        if (mProcessing && mClient != null) {
            mClient!!.stopRecordToRecognize()
        }
        mProcessing = false

        stopTime = System.currentTimeMillis()
    }

    private fun playCancel() {
        if (mProcessing) {
            mProcessing = false
            mClient?.cancel()
            timeTextView.text = ""
            resultTextView.text = ""
        }
    }

    /** По умолчанию передаем сообщение с путем SEND_DATA
     *  и пакуем в Map с ключом SEND_DATA_KEY, если надо отправить не одно значение, то можно
     *  добавить сточки dataMap.putString(key, message) с другими ключами
     */
    private fun sendMessage2Wear(message : String, key: String = SEND_DATA_KEY, path: String = SEND_DATA) {
        val dataClient = Wearable.getDataClient(this)

        val putDataReq: PutDataRequest = PutDataMapRequest.create(path).run {
            dataMap.putString(key, message)
            asPutDataRequest()
            // Добавим, что данные надо передать срочно, а не отложенно
        }.setUrgent()

        val putDataTask: Task<DataItem> = dataClient.putDataItem(putDataReq)
        //тут еще по идее можно обработать результат таска
    }


    override fun onResult(result: String?) {
        if (mClient != null) {
            mClient!!.cancel()
            mProcessing = false
        }

        var tres = "\n"

        var outTitle: String
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

        resultTextView.text = tres
        sendMessage2Wear(outArtist)
    }

    override fun onVolumeChanged(volume: Double) {
        val time = (System.currentTimeMillis() - startTime) / 1000
        volumeTextView.text = "${resources.getString(R.string.volume)} $volume \n\nRecord Time: $time"
    }

    override fun onResume() {
        super.onResume()
//        Wearable.getDataClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
//        Wearable.getDataClient(this).removeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("MainActivity", "release")
        if (mClient != null) {
            mClient!!.release()
            initState = false
            mClient = null
        }

    }

    /** As simple wrapper around Log.i  */
    private fun LOGI(tag: String, message: String) {
        if (Log.isLoggable(tag, Log.INFO)) {
            Log.i(tag, message)
        }
    }

}
