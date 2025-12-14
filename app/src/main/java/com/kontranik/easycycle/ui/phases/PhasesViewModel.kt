package com.kontranik.easycycle.ui.phases

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.easycycle.model.Phase
import com.kontranik.easycycle.storage.SettingsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhasesViewModel(
    private val context: Context
) : ViewModel() {

    private val _phases = MutableStateFlow(listOf<Phase>())
    val phases: StateFlow<List<Phase>> = _phases

    init {
        viewModelScope.launch {
            SettingsService.loadCustomPhases(context).let {
                _phases.value = it
            }
        }
    }

    fun onRemovePhase(key: Int) {
        viewModelScope.launch {
            _phases.value = SettingsService.removeCustomPhase(context, key)
        }
    }

    fun savePhase(phase: Phase) {
        viewModelScope.launch {
            _phases.value = SettingsService.saveCustomPhase(context, phase)
        }
    }

    fun wipeCustomPhases() {
        viewModelScope.launch {
            _phases.value = SettingsService.wipeCustomPhases(context)
        }
    }
}