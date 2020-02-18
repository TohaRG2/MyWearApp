package ru.tohaman.mywearapp.dataSource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import ru.tohaman.mywearapp.data.MusicDB
import ru.tohaman.mywearapp.data.MusicItem
import ru.tohaman.mywearapp.data.musicDatabase
import ru.tohaman.mywearapp.ioThread

val repository by lazy { RepDataSource() }

class RepDataSource (private val database: MusicDB = musicDatabase) : MusicDataSource {
    //private val mDatabase: MusicDB
    //private val mObservableProducts: MediatorLiveData<List<MusicItem>>
    /**
     * Get the list of products from the database and get notified when the data changes.
     */

    override fun observeAllMusicItems(): LiveData<Result<List<MusicItem>>> {
        val res = database.musicDao.observeAllMusic()
        return res.map {
            Result.Success(it)
        }
    }
    override fun observeMusicItem(musicItemId: Int): LiveData<Result<MusicItem>> =
        database.musicDao.observeItemById(musicItemId).map {
            Result.Success(it)
        }

    override suspend fun getAllMusic(): Result<List<MusicItem>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadAllMusicItems(): LiveData<PagedList<MusicItem>> =
        database.musicDao.getAll().toLiveData(Config (pageSize = 30, enablePlaceholders = true, maxSize = 200))

    override suspend fun getMusicItemById(musicItemId: Int): Result<MusicItem> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insertMusicItem(musicItem: MusicItem) {
        ioThread { database.musicDao.insert(musicItem) }
    }

    override suspend fun updateMusicItem(musicItem: MusicItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteMusicItemById(musicItemId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteMusicItem(musicItem: MusicItem) {
        ioThread { database.musicDao.delete(musicItem) }
    }

}
