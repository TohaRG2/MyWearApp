package ru.tohaman.mywearapp.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.tohaman.mywearapp.data.MusicDB
import androidx.paging.Config
import androidx.paging.toLiveData
import ru.tohaman.mywearapp.data.MusicItem
import ru.tohaman.mywearapp.ioThread

class MusicViewModel(app: Application) : AndroidViewModel(app){
    private val dao = MusicDB.get(app).musicItemDao()

    val allMusic = dao.getAll().toLiveData(Config (pageSize = 30, enablePlaceholders = true, maxSize = 200))

    val curArtist : MutableLiveData<String> by lazy { MutableLiveData<String>("lArt") }

    private val currentItem : MutableLiveData<MusicItem> by lazy {
        MutableLiveData<MusicItem>(MusicItem(0, "SomeLazyArtist", "SomeLazyTitle"))
    }

    fun getCurrentItem () : MusicItem {
        Log.d("MWA", "Artist - ${currentItem.value}")
        return currentItem.value ?: MusicItem(0, "nullArtist","nullTitle")
    }

    fun musicInfoActivity(musicItem: MusicItem) {
        currentItem.value = MusicItem(0, "Art", "Tit")

        Log.d("MWA", "Artist - ${currentItem.value?.artist}")
    }

    fun insert(item: MusicItem) = ioThread {
        dao.insert(item)
    }

    fun remove(item: MusicItem) = ioThread {
        dao.delete(item)
    }

}