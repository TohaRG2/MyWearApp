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

val repository by lazy { RepDataSource() }

class RepDataSource (private val database: MusicDB = musicDatabase) : MusicDataSource {
    //private val mDatabase: MusicDB
    //private val mObservableProducts: MediatorLiveData<List<MusicItem>>
    /**
     * Get the list of products from the database and get notified when the data changes.
     */

    override fun observeAllMusicItems(): LiveData<Result<List<MusicItem>>> {
        val res = database.musicItemDao().observeAllMusic()
        return res.map {
            Result.Success(it)
        }
    }
    override fun observeMusicItem(petId: Int): LiveData<Result<MusicItem>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getAllMusic(): Result<List<MusicItem>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadMusicItems(): LiveData<PagedList<MusicItem>> =
        database.musicItemDao().getAll().toLiveData(Config (pageSize = 30, enablePlaceholders = true, maxSize = 200))

    override suspend fun getMusicItemById(petId: Int): Result<MusicItem> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun insertAMusicItem(pet: MusicItem): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateAMusicItem(pet: MusicItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteAPetById(petId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteAllPets(pets: List<MusicItem>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
