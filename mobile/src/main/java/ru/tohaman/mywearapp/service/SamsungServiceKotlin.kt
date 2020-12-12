package ru.tohaman.mywearapp.service

import android.content.Context
import android.widget.Toast
import com.acrcloud.rec.sdk.ACRCloudClient
import com.acrcloud.rec.sdk.ACRCloudConfig
import com.acrcloud.rec.sdk.IACRCloudListener
import com.samsung.android.sdk.SsdkUnsupportedException
import com.samsung.android.sdk.accessory.*
import ru.tohaman.mywearapp.DeveloperKey
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SamsungServiceKotlin(private val mContext: Context) :
    SAAgentV2(TAG, mContext),
    IACRCloudListener {

    private var startTime: Long = 0

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
        private const val TAG = "HelloMessage(P)"
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
                Timber.d("TAG$ onSent(), id: $id ToAgent: ${peerAgent.peerId}")
            }

            override fun onError(peerAgent: SAPeerAgent, id: Int, errorCode: Int) {
                Timber.d("$TAG onError(), id: $id ToAgent: ${peerAgent.peerId} errorCode: $errorCode")
            }

            override fun onReceive(peerAgent: SAPeerAgent, message: ByteArray) {
                Timber.d("$TAG onReceive(), FromAgent : ${peerAgent.peerId} Message : ${String(message)}")
                watchAgent = peerAgent
                displayToast("MESSAGE RECEIVED", Toast.LENGTH_SHORT)
                val calendar: Calendar = GregorianCalendar()
                val dateFormat = SimpleDateFormat("yyyy.MM.dd aa hh:mm:ss.SSS", Locale.getDefault())
                val timeStr = " " + dateFormat.format(calendar.time)
                val strToUpdateUI = String(message)
                val str = "$strToUpdateUI $timeStr by MyWearKotlinService"

                sendMessage2Wear(str)
            }
        }
        arcCloudInit(mContext)
    }

    override fun onResult(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun onVolumeChanged(p0: Double) {
        TODO("Not yet implemented")
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

    private fun sendMessage2Wear(str: String) {
        watchAgent?.let {
            Thread { sendData(it, str) }.start()
        }
    }

    enum class Processing {
        Waiting, Recognize
    }
}