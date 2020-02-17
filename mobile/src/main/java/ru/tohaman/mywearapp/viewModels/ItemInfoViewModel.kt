package ru.tohaman.mywearapp.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tohaman.mywearapp.data.MusicDB
import androidx.paging.Config
import androidx.paging.toLiveData
import ru.tohaman.mywearapp.data.MusicItem
import ru.tohaman.mywearapp.ioThread

class ItemInfoViewModel(app: Application) : AndroidViewModel(app){
    //private val dao = MusicDB.get(app).musicItemDao()

    var curId : Long = 0

    private lateinit var currentItem : LiveData<MusicItem>

//    init {
//        currentItem =
//    }
//
//    fun setMusicItemById (id: Long) {
//         currentItem.value = dao.getById(id).value
//    }

    fun getMusicItem() = currentItem

}