package ru.tohaman.mywearapp.dataSource

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import ru.tohaman.mywearapp.data.MusicItem

interface MusicDataSource {

    fun observeAllMusicItems(): LiveData<Result<List<MusicItem>>>

    fun observeMusicItem(musicItemId: Int): LiveData<Result<MusicItem>>

    suspend fun getAllMusic(): Result<List<MusicItem>>

    fun loadAllMusicItems(): LiveData<PagedList<MusicItem>>

    suspend fun getMusicItemById(itemId: Int): Result<MusicItem>

    fun insertMusicItem(musicItem: MusicItem)

    suspend fun updateMusicItem(musicItem: MusicItem)

    suspend fun deleteMusicItemById(musicItemId: Int)

    fun deleteMusicItem(musicItem: MusicItem)

}