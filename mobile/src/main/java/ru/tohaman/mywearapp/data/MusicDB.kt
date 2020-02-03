package ru.tohaman.mywearapp.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.tohaman.mywearapp.ioThread
import java.util.*


@Database(entities = [MusicItem::class], version = 1)
@TypeConverters(Converters::class)
abstract class MusicDB : RoomDatabase() {
    abstract fun musicItemDao(): MusicItemDao

    companion object {
        private var instance: MusicDB? = null
        @Synchronized
        fun get (context: Context): MusicDB {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext, MusicDB::class.java, "base.db"
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        fillDb(context.applicationContext)
                    }

                }).build()
            }
            return instance!!
        }

        private fun fillDb(context: Context) {
            ioThread {
                get(context).musicItemDao().insert(MusicItem(0, "SomeArtist", "SomeSong", Date()))
            }
        }
    }
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