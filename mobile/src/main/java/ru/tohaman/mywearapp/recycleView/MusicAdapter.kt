package ru.tohaman.mywearapp.recycleView

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tohaman.mywearapp.R
import ru.tohaman.mywearapp.data.MusicItem

class MusicAdapter : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {
    private val mDataList: MutableList<MusicItem> = ArrayList()

    fun setData(data: List<MusicItem>) {
        mDataList.clear()
        mDataList.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.music_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val currentItem = mDataList[position]
        holder.bindTo(currentItem)
    }


    class MusicViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val titleText = itemView.findViewById<TextView>(R.id.titleText)
        private val artistText = itemView.findViewById<TextView>(R.id.artistText)
        fun bindTo(menuItem: MusicItem) {
            titleText.text = menuItem.title
            artistText.text = menuItem.artist
        }
    }

    /**
     *  class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val titleView = itemView.findViewById<TextView>(R.id.main_menu_title)
    private val commentView = itemView.findViewById<TextView>(R.id.main_menu_comment)
    private val imageView = itemView.findViewById<ImageView>(R.id.main_menu_image)

    fun bindTo(menuItem: MainDBItem) {
    titleView.text = menuItem.title
    commentView.text = menuItem.comment
    val icon = menuItem.icon
    imageView.setImageResource(icon)
    }
    }
     */

}