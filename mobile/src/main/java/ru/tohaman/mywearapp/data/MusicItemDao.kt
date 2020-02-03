package ru.tohaman.mywearapp.data

import androidx.paging.DataSource
import androidx.room.*


@Dao
interface MusicItemDao {
    @Query("SELECT * FROM RQ_Results")
    fun getAll(): DataSource.Factory<Int, MusicItem>

    @Query("SELECT * FROM RQ_Results WHERE id = :id")
    fun getById(id: Long): MusicItem?

    @Insert
    fun insert(musicItem: MusicItem?)

    @Update
    fun update(musicItem: MusicItem?)

    @Delete
    fun delete(musicItem: MusicItem?)
}