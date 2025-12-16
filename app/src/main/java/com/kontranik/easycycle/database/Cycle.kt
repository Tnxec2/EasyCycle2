package com.kontranik.easycycle.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.kontranik.easycycle.ui.calendar.CalendarViewModel.Companion.sdfISO
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
    var cycleStart: Date?,

    @ColumnInfo(name = "last_cycle_length")
    var lengthOfLastCycle: Int?
)

class DateConverter {
    @TypeConverter
    fun toDate(dateLong: String?): Date? {
        return dateLong?.let { sdfISO.parse(it) }
    }

    @TypeConverter
fun fromDate(date: Date?): String? {
        return date?.let { sdfISO.format(date) }
    }
}