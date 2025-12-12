package com.kontranik.easycycle.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface CycleDao {
    @Query("SELECT * FROM cycles_archive ORDER BY cyclestart DESC")
    fun getAll(): List<Cycle>

    @Query("SELECT * FROM cycles_archive WHERE year > :year ORDER BY cyclestart DESC")
    fun getAllByYearsAmount(year: Int): List<Cycle>

    @Query("DELETE FROM cycles_archive WHERE year < :year")
    fun removeAllOld(year: Int)

    @Query("SELECT * FROM cycles_archive WHERE _id = :id")
    fun getById(id: Long): Cycle?

    @Query("SELECT * FROM cycles_archive WHERE cyclestart = :date")
    fun getByDate(date: Date): Cycle?

    @Query("SELECT * FROM cycles_archive ORDER BY cyclestart DESC LIMIT 1")
    fun getLast(): Cycle?

    @Query("SELECT * FROM cycles_archive ORDER BY cyclestart DESC LIMIT 1")
    fun getLastAsFlow(): Flow<Cycle?>

    @Query("SELECT AVG(last_cycle_length) FROM (SELECT last_cycle_length FROM cycles_archive ORDER BY cyclestart DESC LIMIT :limit)")
    fun getAverageLengthOfLastMonths(limit: Int = 12): Flow<Int?>

    @Insert
    fun insert(cycle: Cycle): Long

    @Update
    fun update(cycle: Cycle)

    @Delete
    fun delete(cycle: Cycle)

    @Query("DELETE FROM cycles_archive")
    fun deleteAll()
}