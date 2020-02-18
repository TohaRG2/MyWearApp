package ru.tohaman.mywearapp.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.tohaman.mywearapp.data.MusicItem
import ru.tohaman.mywearapp.dataSource.repository
import ru.tohaman.mywearapp.ioThread
import timber.log.Timber

class MusicViewModel(app: Application) : AndroidViewModel(app){

    val allMusic = repository.loadAllMusicItems()

        //dao.getAll().toLiveData(Config (pageSize = 30, enablePlaceholders = true, maxSize = 200))

    val curArtist : MutableLiveData<String> by lazy { MutableLiveData<String>("lArt") }

    private val currentItem : MutableLiveData<MusicItem> by lazy {
        MutableLiveData<MusicItem>(MusicItem(0, "SomeLazyArtist", "SomeLazyTitle"))
    }

    fun getCurrentItem () : MusicItem {
        Timber.d( "Artist - ${currentItem.value}")
        return currentItem.value ?: MusicItem(0, "nullArtist","nullTitle")
    }

    fun musicInfoActivity(musicItem: MusicItem) {
        currentItem.value = MusicItem(0, "Art", "Tit")

        Timber.d( "Artist - ${currentItem.value?.artist}")
    }

    fun insert(item: MusicItem) {
        repository.insertMusicItem(item)
    }

    fun remove(item: MusicItem) = ioThread {
        repository.deleteMusicItem(item)
    }

}