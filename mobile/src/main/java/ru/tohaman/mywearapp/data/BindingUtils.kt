package ru.tohaman.mywearapp.data

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("dateToString")
fun dateToString(textView: TextView, date: Date?) {
    date?.let {
        textView.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(it)
    }
}

@BindingAdapter("showTime")
fun showTime(textView: TextView, time: Long?) {
    time?.let {
        textView.text = "$it сек"
    }
}