package ru.tohaman.mywearapp.data

import androidx.room.*


@Dao
interface MusicItemDao {
    @get:Query("SELECT * FROM RQ_Results")
    val all: List<MusicItem>?

    @Query("SELECT * FROM RQ_Results WHERE id = :id")
    fun getById(id: Long): MusicItem?

    @Insert
    fun insert(musicItem: MusicItem?)

    @Update
    fun update(musicItem: MusicItem?)

    @Delete
    fun delete(musicItem: MusicItem?)
}