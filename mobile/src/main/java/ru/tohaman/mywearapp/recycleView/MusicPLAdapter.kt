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
import ru.tohaman.mywearapp.databinding.MusicItemBinding
import java.text.SimpleDateFormat
import java.util.*

class MusicPLAdapter (private val onClickListener: OnClickListener) : PagedListAdapter<MusicItem, MusicPLAdapter.MusicViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bindTo(currentItem, onClickListener)
    }


    class MusicViewHolder private constructor(private val binding: MusicItemBinding): RecyclerView.ViewHolder(binding.root) {
        var menuItem : MusicItem? = null

        fun bindTo(menuItem: MusicItem?, onClickListener: OnClickListener) {
            this.menuItem = menuItem
            binding.viewMenuItem = menuItem
            binding.clickListener = onClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : MusicViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = MusicItemBinding.inflate(inflater, parent, false)
                return MusicViewHolder(binding)
            }
        }
    }

    class OnClickListener(val clickListener: (MusicItem) -> Unit) {
        fun onClick(menuItem: MusicItem) = clickListener(menuItem)
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