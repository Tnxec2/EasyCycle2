package com.kontranik.easycycle.database

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.helper.PhasesHelper
import com.kontranik.easycycle.model.CDay
import com.kontranik.easycycle.model.LastCycle
import com.kontranik.easycycle.model.StatisticItem
import com.kontranik.easycycle.storage.SettingsService
import com.kontranik.easycycle.ui.calendar.CalendarDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class CycleViewModel(
    private val context: Context,
    private val repository: CycleRepository
) : ViewModel() {

    private val _archivList = MutableLiveData<List<StatisticItem>>()
    val archivList: LiveData<List<StatisticItem>> = _archivList

    val lastCycle = repository.getLastOneAsFlow()

    val averageLength = repository.getAverageLength()

    private var daysOnHome = DefaultSettings.settings.daysOnHome
    val cDays = lastCycle.transform { lc ->
        emit(loadCycleDays(lc))
    }

    init {
    }

    private fun loadPhases() {
//        val list = SettingsService.loadCustomPhases(context)
//
//        if (list.isNotEmpty())  {
//            phases.value = list.sortedWith(compareBy({ it.from }, { it.to }))
//        } else {
//            phases.value = listOf()
//        }
    }

    fun loadArchivList(yearsToLoad: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _archivList.postValue(repository.getArchivList(yearsToLoad))
        }
    }

    fun loadCycleDays(lastCycle: Cycle?): List<CDay> {
        return if ( lastCycle != null) {
            PhasesHelper.getDaysInfo(
                context,
                daysOnHome,
                lastCycle
            )
        } else {
            emptyList()
        }
    }

    fun addCycle(cycle: Cycle) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.add(cycle)
        }
    }

    fun deleteCycleById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteById(id)
        }
    }

    fun addCycle(cycle: CalendarDay) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.add(
                Cycle(
                    year = cycle.date.year,
                    month = cycle.date.month,
                    cycleStart = cycle.date,
                    lengthOfLastCycle = 0 // TODO: calculate length

                )
            )
        }
    }
}