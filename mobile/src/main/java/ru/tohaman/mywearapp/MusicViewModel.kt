package ru.tohaman.mywearapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.tohaman.mywearapp.data.MusicDB
import androidx.paging.Config
import androidx.paging.toLiveData
import ru.tohaman.mywearapp.data.MusicItem

class MusicViewModel(app: Application) : AndroidViewModel(app){
    val dao = MusicDB.get(app).musicItemDao()

    val allMusic = dao.getAll().toLiveData(Config (pageSize = 30, enablePlaceholders = true, maxSize = 200))

    fun insert(item: MusicItem) = ioThread {
        dao.insert(item)
    }

    fun remove(item: MusicItem) = ioThread {
        dao.delete(item)
    }

}