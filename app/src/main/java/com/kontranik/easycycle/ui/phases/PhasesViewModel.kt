package com.kontranik.easycycle.ui.phases

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.easycycle.database.CycleRepository
import com.kontranik.easycycle.model.Phase
import com.kontranik.easycycle.service.AlarmScheduler
import com.kontranik.easycycle.storage.SettingsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhasesViewModel(
    private val context: Context,
    private val cycleRepository: CycleRepository
) : ViewModel() {

    private val _phases = MutableStateFlow(listOf<Phase>())
    val phases: StateFlow<List<Phase>> = _phases.asStateFlow()


    init {
        viewModelScope.launch {
            SettingsService.loadCustomPhases(context).let {
                _phases.value = it
            }
        }
    }

    fun onRemovePhase(index: Int) {
        viewModelScope.launch {
            SettingsService.removeCustomPhase(context, index).let {
                val oldPhases = _phases.value.map { phase -> phase.copy() }
                _phases.emit(it.map { phase -> phase.copy() })
                updateScheduler(oldPhases, it)
            }
        }
    }

    fun savePhase(phase: Phase) {
        viewModelScope.launch {
            SettingsService.saveCustomPhase(context, phase).let {
                val oldPhases = _phases.value.map { phase -> phase.copy() }
                _phases.emit(it.map { phase -> phase.copy() })
                updateScheduler(oldPhases, it)
            }
        }
    }

    fun wipeCustomPhases() {
        viewModelScope.launch {
            SettingsService.wipeCustomPhases(context).let {
                val oldPhases = _phases.value.map { phase -> phase.copy() }
                _phases.emit(it.map { phase -> phase.copy() })
                updateScheduler(oldPhases, it)
            }
        }
    }

    private fun updateScheduler(oldPhases: List<Phase>, allPhases: List<Phase>) {
        viewModelScope.launch(Dispatchers.IO) {
            val lastCycle = cycleRepository.getLastOne()
            lastCycle?.cycleStart?.let { cycleStart ->
                val alarmScheduler = AlarmScheduler(context)
                // clear old phases
                alarmScheduler.cancelAllPhaseNotifications(oldPhases)
                // schedule new phases
                alarmScheduler.schedulePhaseNotifications(cycleStart, allPhases)
            }
        }
    }
}