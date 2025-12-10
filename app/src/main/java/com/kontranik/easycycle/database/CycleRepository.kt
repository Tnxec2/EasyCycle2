package com.kontranik.easycycle.database

import android.util.Log
import com.kontranik.easycycle.model.LastCycle
import com.kontranik.easycycle.model.StatisticItem
import kotlinx.coroutines.flow.Flow
import java.util.*
import kotlin.collections.HashMap

class CycleRepository(private val cycleDao: CycleDao) {

    fun getArchivList(yearsToLoad: Int): List<StatisticItem> {
        Log.d("CycleRepository", "getArchivList")
        val years: HashMap<Int, MutableList<LastCycle>> = hashMapOf()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -yearsToLoad)
        val startYear = calendar.get(Calendar.YEAR)

        cycleDao.getAllByYearsAmount(startYear).forEach { cycle ->
            val lastCycle = cycle.toLastCycle()
            var year = lastCycle.year
            calendar.time = lastCycle.cycleStart
            if ( year == null) year = calendar.get(Calendar.YEAR)
            if ( !years.containsKey(year)) {
                years[year] = mutableListOf()
            }
            years[year]!!.add(lastCycle)
        }
        val result: MutableList<StatisticItem> = mutableListOf()
        years.keys.sortedDescending().forEach { key ->
            if ( years[key] != null) {
                var averageLengthSum = 0
                years[key]!!.forEach {
                    averageLengthSum += it.lengthOfLastCycle
                }
                result.add( StatisticItem(key.toString(), years[key]!!,
                    (averageLengthSum / years[key]!!.size)
                ))
            }
        }
        return result
    }

    fun getLastOne(): LastCycle? {
        return cycleDao.getLast()?.toLastCycle()
    }

    fun getLastOneAsFlow(): Flow<Cycle?> {
        return cycleDao.getLastAsFlow()
    }

    fun add(cycle: Cycle) {
        Log.d("CycleRepository", "add")
        val item = cycleDao.getByDate(cycle.cycleStart)
        if (item == null) {
            cycleDao.insert(cycle)
        }
    }

    fun getAverageLength(): Flow<Int?> {
        return cycleDao.getAverageLengthOfLastMonths()
    }

    fun deleteById(id: Long) {
        Log.d("CycleRepository", "deleteById")
        val cycleToDelete = cycleDao.getById(id)
        if (cycleToDelete != null) {
            cycleDao.delete(cycleToDelete)
        }
    }
}

fun LastCycle.toCycle(): Cycle {
    return Cycle(
        id = this.id,
        year = this.year,
        month = this.month,
        cycleStart = this.cycleStart,
        lengthOfLastCycle = this.lengthOfLastCycle
    )
}

fun Cycle.toLastCycle(): LastCycle {
    return LastCycle(
        id = this.id,
        year = this.year,
        month = this.month,
        cycleStart = this.cycleStart,
        lengthOfLastCycle = this.lengthOfLastCycle
    )
}
