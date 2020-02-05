package ru.tohaman.mywearapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**Класс помеченный как Entity, определяет поля таблицы, по умолчанию таблица называется как класс,
 * но можно переименовать таблицу через (tableName = "RQ_Results)"
 */
@Entity(tableName = "RQ_Results")
data class MusicItem (
    @PrimaryKey(autoGenerate = true) val id: Int,
    var artist: String = "",
    var title: String = "",
    var recTime: Long = 0,
    var date: Date = Date()
)


