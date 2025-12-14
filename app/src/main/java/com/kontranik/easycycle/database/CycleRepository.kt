package com.kontranik.easycycle.database

import android.util.Log
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.model.StatisticItem
import kotlinx.coroutines.flow.Flow
import java.util.*
import kotlin.collections.HashMap

class CycleRepository(private val cycleDao: CycleDao) {

    fun getArchivList(yearsToLoad: Int): List<StatisticItem> {
        Log.d("CycleRepository", "getArchivList: $yearsToLoad")
        val years: HashMap<Int, MutableList<Cycle>> = hashMapOf()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -yearsToLoad)
        val startYear = calendar.get(Calendar.YEAR)

        cycleDao.getAllByYearsAmount(startYear).forEach { cycle ->
            var year = cycle.year
            calendar.time = cycle.cycleStart
            if ( year == null) year = calendar.get(Calendar.YEAR)
            if ( !years.containsKey(year)) {
                years[year] = mutableListOf()
            }
            years[year]!!.add(cycle)
        }
        Log.d("CycleRepository", "getArchivList: ${years.size}")
        val result: MutableList<StatisticItem> = mutableListOf()
        years.keys.sortedDescending().forEach { key ->
            if ( years[key] != null) {
                val averageLengthSum = years[key]!!.sumOf {
                    it.lengthOfLastCycle
                }
                result.add(
                    StatisticItem(
                        year = key.toString(),
                        items =years[key]!!,
                        averageCycleLength = (averageLengthSum / years[key]!!.size)
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


    fun getLastOne(): Cycle? {
        return cycleDao.getLast()
    }

    fun getLastOneAsFlow(): Flow<Cycle?> {
        return cycleDao.getLastAsFlow()
    }

    fun add(cycle: Cycle) {
        val cal = Calendar.getInstance().apply {
            time = cycle.cycleStart
        }

        cycle.year = cal.get(Calendar.YEAR)
        cycle.month = cal.get(Calendar.MONTH)

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
