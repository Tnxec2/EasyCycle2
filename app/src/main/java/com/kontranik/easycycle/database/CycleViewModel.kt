package com.kontranik.easycycle.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.easycycle.helper.PhasesHelper
import com.kontranik.easycycle.model.CDay
import com.kontranik.easycycle.model.StatisticItem
import com.kontranik.easycycle.storage.SettingsService
import com.kontranik.easycycle.ui.calendar.CalendarDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class CycleViewModel(
    private val context: Context,
    private val repository: CycleRepository
) : ViewModel() {

    val lastCycle = repository.getLastOneAsFlow()

    val cDays = lastCycle.transform { lc ->
        emit(loadCycleDays(lc))
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearOldStatistic()
        }
    }


    fun loadCycleDays(lastCycle: Cycle?): List<CDay> {
        val settings = SettingsService.loadSettings(context)
        return if ( lastCycle != null) {
            PhasesHelper.getDaysInfo(
                context,
                settings.daysOnHome,
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
}