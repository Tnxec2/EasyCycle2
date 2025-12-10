package com.kontranik.easycycle.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

@Entity(tableName = "cycles_archive")
data class Cycle(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long? = null,

    @ColumnInfo(name = "year")
    var year: Int? = null,

    @ColumnInfo(name = "month")
    var month: Int? = null,

    @ColumnInfo(name = "cyclestart")
    var cycleStart: Date,

    @ColumnInfo(name = "last_cycle_length")
    var lengthOfLastCycle: Int
)

class DateConverter {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return dateLong?.let { Date(it) }
    }

    @TypeConverter
fun fromDate(date: Date?): Long? {
        return date?.time
    }
}