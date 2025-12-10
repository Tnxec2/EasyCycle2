package com.kontranik.easycycle.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.easycycle.model.Settings
import com.kontranik.easycycle.storage.SettingsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val context: Context
) : ViewModel() {

    private val _settingsState = MutableStateFlow(Settings())
    val settingsState: StateFlow<Settings> = _settingsState

    init {
        viewModelScope.launch {
            SettingsService.loadSettings(context)?.let {
                _settingsState.value = it
            }
        }
    }

}