package ru.tohaman.mywearapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_list.*
import ru.tohaman.mywearapp.data.MusicItem
import ru.tohaman.mywearapp.recycleView.MusicPLAdapter

class ListActivity : AppCompatActivity() {

    private val viewModel by viewModels<MusicViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // Create adapter for the RecyclerView
        val adapter = MusicPLAdapter(MusicPLAdapter.OnClickListener{onMenuItemClick(it)})
        rcView.adapter = adapter
        rcView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)

        viewModel.allMusic.observe(this, Observer (adapter::submitList))
        initSwipeToDelete()
    }

    private fun initSwipeToDelete() {
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            // enable the items to swipe to the left or right
            override fun getMovementFlags(recyclerView: RecyclerView,
                                          viewHolder: RecyclerView.ViewHolder): Int =
                makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean = false

            // When an item is swiped, remove the item via the view model. The list item will be
            // automatically removed in response, because the adapter is observing the live list.
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                (viewHolder as MusicPLAdapter.MusicViewHolder).menuItem.let {
                    if (it != null) {
                        viewModel.remove(it)
                    }
                }
            }
        }).attachToRecyclerView(rcView)
    }

    private fun onMenuItemClick(musicItem: MusicItem) {
        Log.d("MWA", "ListActivity.onMenuItemClick, ${musicItem.id}")
    }
}
