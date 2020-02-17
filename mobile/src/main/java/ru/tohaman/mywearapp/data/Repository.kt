package ru.tohaman.mywearapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class Repository private constructor(database: MusicDB) {
    private val mDatabase: MusicDB
    private val mObservableProducts: MediatorLiveData<List<MusicItem>>
    /**
     * Get the list of products from the database and get notified when the data changes.
     */

    fun loadComments(productId: Long): LiveData<MusicItem?> {
        return mDatabase.musicItemDao().getById(productId) //?: MusicItem(0, "NullArtist", "NullTitle")
    }

    companion object {
        private var sInstance: Repository? = null
        fun getInstance(database: MusicDB): Repository? {
            if (sInstance == null) {
                synchronized(Repository::class.java) {
                    if (sInstance == null) {
                        sInstance = Repository(database)
                    }
                }
            }
            return sInstance
        }
    }

    init {
        mDatabase = database
        mObservableProducts = MediatorLiveData<List<MusicItem>>()
    }
}
