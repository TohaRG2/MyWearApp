package ru.tohaman.mywearapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.graphics.Typeface


private const val START_REC_KEY = "ru.tohaman.mywearapp.startRec"
private const val START_REC = "/startRecognize"
private const val SEND_DATA_KEY = "ru.tohaman.mywearapp.sendData"
private const val SEND_DATA = "/sendArtist"
private const val COUNT_KEY = "com.example.key.count"
private const val AMBIENT_UPDATE_ACTION = "com.your.package.action.AMBIENT_UPDATE"

const val TAG = "MWA_Wear"

class MainActivity : WearableActivity(),
                        DataClient.OnDataChangedListener {

    private var count = 0
    private lateinit var topText: TextView
    private lateinit var mainText: TextView
    private lateinit var bottomText: TextView
    private lateinit var monthText: TextView
    private var autoShazam = false

    // Milliseconds between waking processor/screen for updates (преобразуем секунды в милисекунды)
    // Pause between autoShazam requests in ambient mode
    private val AMBIENT_INTERVAL_MS: Long = TimeUnit.SECONDS.toMillis(30)

    private lateinit var ambientUpdateAlarmManager: AlarmManager
    private lateinit var ambientUpdatePendingIntent: PendingIntent
    private lateinit var ambientUpdateBroadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setAmbientEnabled()

        ambientUpdateAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        ambientUpdatePendingIntent = Intent(AMBIENT_UPDATE_ACTION).let { ambientUpdateIntent ->
            PendingIntent.getBroadcast(this, 0, ambientUpdateIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        ambientUpdateBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                refreshDisplayAndSetNextUpdate()
            }
        }



        topText = findViewById(R.id.wear_top_text)
        topText.setTextColor(Color.DKGRAY)
        topText.text = ""

        /**Символы форматирования строки
        A - AM или PM
        d - день месяца (1-31)
        D - день в году (1-366)
        H - часы в формате AM/PM (1-12)
        K - часы в формате суток (1-24)
        M - минуты (0-59)
        S - секунды (0-59)
        W - неделя в году (1-53)
        y - год
        z - часовой пояс */
        mainText = findViewById(R.id.wear_start_button)

        val customFont = Typeface.createFromAsset(assets, "fonts/dsdigib.ttf")

        mainText.typeface = customFont

        val sdf = SimpleDateFormat("kk:mm", Locale.US)
        mainText.text  = sdf.format(Date())
        mainText.setOnClickListener {
            sendRequest2Phone()
        }

        monthText = findViewById(R.id.month_text)
        val sdf2 = SimpleDateFormat("d MMM", Locale.getDefault())
        monthText.text = sdf2.format(Date())

        bottomText = findViewById(R.id.wear_bottom_text)
        bottomText.setOnClickListener {
            if (autoShazam) {
                bottomText.text = ""
                topText.text = ""
            } else {
                bottomText.text = "!"

            }
            autoShazam = !autoShazam
        }
    }


    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
        IntentFilter(AMBIENT_UPDATE_ACTION).also { filter ->
            registerReceiver(ambientUpdateBroadcastReceiver, filter)
        }
        sendRequest2Phone()
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
        unregisterReceiver(ambientUpdateBroadcastReceiver)
        ambientUpdateAlarmManager.cancel(ambientUpdatePendingIntent)
    }

    override fun onDestroy() {
        ambientUpdateAlarmManager.cancel(ambientUpdatePendingIntent)
        super.onDestroy()
    }

    private fun sendRequest2Phone () {
        /** https://developer.android.com/training/wearables/data-layer/data-items#ListenEvents
         *  https://itnan.ru/post.php?c=1&p=353748
         * */

        val dataClient = Wearable.getDataClient(this)

        val putDataReq: PutDataRequest = PutDataMapRequest.create(START_REC).run {
            dataMap.putString(START_REC_KEY, "Данные с часов ${count++}")
//            dataMap.putInt(COUNT_KEY, count++)
            asPutDataRequest()
        // Добавим, что данные надо передать срочно, а не отложенно
        }.setUrgent()

        val putDataTask: Task<DataItem> = dataClient.putDataItem(putDataReq)

        //Показываем, что запрос послан
        bottomText.text = ">"


        /** Calls to the Data Layer API, for example, a call using the putDataItem method of the DataClient class, sometimes
         * return a Task<ResultType> object. As soon as the Task object is created, the operation is queued in the background.
         * If you do nothing more after this, the operation eventually completes silently. However, you'll usually want to do
         * something with the result after the operation completes, so the Task object lets you wait for the result status,
         * either synchronously or asynchronously.
         * https://developer.android.com/training/wearables/data-layer/events
         */

        // Например, можно узнать результат передачи
//        putDataTask.addOnSuccessListener(
//            OnSuccessListener<DataItem> { dataItem -> LOGD(TAG, "Sending image was successful: $dataItem") })

    }

    private fun refreshDisplayAndSetNextUpdate() {
        val sdf = SimpleDateFormat("kk:mm", Locale.UK)
        mainText.text  = sdf.format(Date())
        if (autoShazam) { sendRequest2Phone() }

//        if (isAmbient) {
//            // Implement data retrieval and update the screen for ambient mode
//        } else {
//            // Implement data retrieval and update the screen for interactive mode
//        }

        val timeMs: Long = System.currentTimeMillis()
        // Schedule a new alarm
        if (isAmbient) {
            // Calculate the next trigger time
            val delayMs: Long = AMBIENT_INTERVAL_MS - timeMs % AMBIENT_INTERVAL_MS
            val triggerTimeMs: Long = timeMs + delayMs
            ambientUpdateAlarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMs,
                ambientUpdatePendingIntent)
        } else {
            // Calculate the next trigger time for interactive mode
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, "onDataChanged: $dataEvents")
        }
        dataEvents.forEach { event ->
            // DataItem changed
            if (event.type == DataEvent.TYPE_CHANGED) {
                event.dataItem.also { item ->
                    if (item.uri.path?.compareTo(SEND_DATA) == 0) {
                        DataMapItem.fromDataItem(item).dataMap.apply {
                            if (getString(SEND_DATA_KEY) != "?") {
                                //получен ответ от телефона (не подтверждение "?")
                                val artist = getString(SEND_DATA_KEY).substringAfter(":")
                                val time = getString(SEND_DATA_KEY).substringBefore(":")
                                topText.text = artist
                                bottomText.text = if (autoShazam) time else ""
                            } else {
                                //получено подтверждение на запрос
                                bottomText.text = "?"
                            }
                        }
                    }
                }
            } else if (event.type == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)

        topText.setTextColor(Color.DKGRAY)

        topText.paint.isAntiAlias = false
        mainText.paint.isAntiAlias = false
        bottomText.paint.isAntiAlias = false

        refreshDisplayAndSetNextUpdate()
    }

    override fun onExitAmbient() {
        super.onExitAmbient()

        if (autoShazam) topText.setTextColor(Color.BLACK)

        topText.paint.isAntiAlias = true
        mainText.paint.isAntiAlias = true
        bottomText.paint.isAntiAlias = true

        ambientUpdateAlarmManager.cancel(ambientUpdatePendingIntent)
    }

    override fun onUpdateAmbient() {
        super.onUpdateAmbient()

        refreshDisplayAndSetNextUpdate()
    }

}
