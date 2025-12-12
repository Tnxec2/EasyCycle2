package com.kontranik.easycycle.database

import android.util.Log
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.model.LastCycle
import com.kontranik.easycycle.model.StatisticItem
import com.kontranik.easycycle.ui.calendar.Calendar
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

    fun clearOldStatistic() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -DefaultSettings.yearsToLeave)
        Log.d("CycleRepository", "clearOldStatistic ${DefaultSettings.yearsToLeave} years - ${calendar.get(Calendar.YEAR)}")
        cycleDao.removeAllOld(calendar.get(Calendar.YEAR))
    }


    fun getLastOne(): LastCycle? {
        return cycleDao.getLast()?.toLastCycle()
    }

    fun getLastOneAsFlow(): Flow<Cycle?> {
        return cycleDao.getLastAsFlow()
    }

    fun add(cycle: Cycle) {
        Log.d("CycleRepository", "add")

        Calendar.getInstance().time = cycle.cycleStart
        cycle.year = Calendar.getInstance().get(Calendar.YEAR)
        cycle.month = Calendar.getInstance().get(Calendar.MONTH)

        val item = cycleDao.getByDate(cycle.cycleStart)
        if (item == null) {
            cycleDao.insert(cycle)
        } else {
            cycle.id = item.id
            cycleDao.update(cycle)
        }
    }

    fun getAverageLength(): Flow<Int?> {
        return cycleDao.getAverageLengthOfLastMonths(DefaultSettings.monthsForAverageCycleLength)
    }

    fun deleteById(id: Long) {
        Log.d("CycleRepository", "deleteById")
        val cycleToDelete = cycleDao.getById(id)
        if (cycleToDelete != null) {
            cycleDao.delete(cycleToDelete)
        }
    }

    fun addAll(cycles: MutableList<Cycle>) {
        cycles.forEach {
            add(it)
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
