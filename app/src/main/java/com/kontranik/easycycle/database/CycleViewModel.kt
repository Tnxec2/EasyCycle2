package com.kontranik.easycycle.database

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.easycycle.helper.PhasesHelper
import com.kontranik.easycycle.model.CDay
import com.kontranik.easycycle.storage.SettingsService
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
        val daysOnHome = SettingsService.loadSettings(context).daysOnHome
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
}