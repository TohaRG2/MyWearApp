package ru.tohaman.mywearapp.dataSource

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import ru.tohaman.mywearapp.data.MusicItem

interface MusicDataSource {

    fun observeAllMusicItems(): LiveData<Result<List<MusicItem>>>

    fun observeMusicItem(petId: Int): LiveData<Result<MusicItem>>

    suspend fun getAllMusic(): Result<List<MusicItem>>

    fun loadMusicItems(): LiveData<PagedList<MusicItem>>

    suspend fun getMusicItemById(petId: Int): Result<MusicItem>

    suspend fun insertAMusicItem(pet: MusicItem): Long

    suspend fun updateAMusicItem(pet: MusicItem)

    suspend fun deleteAPetById(petId: Int)

    suspend fun deleteAllPets(pets: List<MusicItem>)

}