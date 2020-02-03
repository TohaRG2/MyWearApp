package ru.tohaman.mywearapp.recycleView

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.tohaman.mywearapp.R
import ru.tohaman.mywearapp.data.MusicItem

class MusicPLAdapter : PagedListAdapter<MusicItem, MusicPLAdapter.MusicViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.music_item, parent, false))
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bindTo(currentItem)
    }


    class MusicViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val titleText = itemView.findViewById<TextView>(R.id.titleText)
        private val artistText = itemView.findViewById<TextView>(R.id.artistText)
        var musicItem: MusicItem? = null
        fun bindTo(menuItem: MusicItem?) {
            this.musicItem = menuItem
            titleText.text = menuItem?.title
            artistText.text = menuItem?.artist
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<MusicItem>() {
            override fun areItemsTheSame(oldItem: MusicItem, newItem: MusicItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MusicItem, newItem: MusicItem): Boolean =
                oldItem == newItem
        }
    }

}