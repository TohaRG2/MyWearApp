package ru.tohaman.mywearapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_list.*
import ru.tohaman.mywearapp.recycleView.MusicPLAdapter

class ListActivity : AppCompatActivity() {

    private val viewModel by viewModels<MusicViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // Create adapter for the RecyclerView
        val adapter = MusicPLAdapter()
        rcView.adapter = adapter
        rcView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)

        viewModel.allMusic.observe(this, Observer (adapter::submitList))

    }
}
