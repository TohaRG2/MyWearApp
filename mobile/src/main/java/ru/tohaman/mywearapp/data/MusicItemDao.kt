package ru.tohaman.mywearapp.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*


@Dao
interface MusicItemDao {

    @Query ("select * FROM RQ_Results order by id")
    fun observeAllMusic(): LiveData<List<MusicItem>>

    @Query ("SELECT * FROM RQ_Results WHERE id = :itemId")
    fun observeItemById(itemId : Int): LiveData<MusicItem>

    @Query("SELECT * FROM RQ_Results")
    fun getAll(): DataSource.Factory<Int, MusicItem>

    @Query("SELECT * FROM RQ_Results WHERE id = :id")
    fun getById(id: Long): LiveData<MusicItem?>

    @Insert
    fun insert(musicItem: MusicItem?)

    @Update
    fun update(musicItem: MusicItem?)

    @Delete
    fun delete(musicItem: MusicItem?)
}