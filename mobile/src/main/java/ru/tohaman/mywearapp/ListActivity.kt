package ru.tohaman.mywearapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tohaman.mywearapp.recycleView.MusicAdapter

class ListActivity : AppCompatActivity() {

    private val viewModel by viewModels<MusicViewModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val mAdapter = MusicAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)


        viewManager = LinearLayoutManager(this)

        //mAdapter.setData(data = list)

        recyclerView = findViewById<RecyclerView>(R.id.rcView).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = mAdapter

        }

    }
}
