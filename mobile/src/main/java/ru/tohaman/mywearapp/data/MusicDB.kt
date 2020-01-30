package ru.tohaman.mywearapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.*


@Database(entities = [MusicItem::class], version = 1)
@TypeConverters(Converters::class)
abstract class MusicDB : RoomDatabase() {
    abstract fun musicItemDao(): MusicItemDao?


}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}