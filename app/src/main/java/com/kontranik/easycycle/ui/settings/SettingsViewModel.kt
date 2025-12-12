package com.kontranik.easycycle.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.easycycle.model.Settings
import com.kontranik.easycycle.storage.SettingsService
import com.kontranik.easycycle.ui.DrawerParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val context: Context
) : ViewModel() {

    fun changeShowOnStart(showOnStartId: Int) {
        viewModelScope.launch {
            val newSettings = _settingsState.value.copy(
                showOnStart = showOnStartId
            )
            SettingsService.saveSettings(
                newSettings,
                context
            )
            _settingsState.value = newSettings
        }
    }

    fun changeDaysOnHome(daysOnHome: Int) {
        viewModelScope.launch {
            val newSettings = _settingsState.value.copy(
                daysOnHome = daysOnHome
            )
            SettingsService.saveSettings(
                newSettings,
                context
            )
            _settingsState.value = newSettings
        }
    }

    fun changeYearsOnStatistic(yearsInStatistic: Int) {
        viewModelScope.launch {
            val newSettings = _settingsState.value.copy(
                yearsOnStatistic = yearsInStatistic
            )
            SettingsService.saveSettings(
                newSettings,
                context
            )
            _settingsState.value = newSettings
        }
    }

    private val _settingsState = MutableStateFlow(Settings())
    val settingsState: StateFlow<Settings> = _settingsState

    init {
        viewModelScope.launch {
            SettingsService.loadSettings(context).let {
                _settingsState.value = it
            }
        }
    }

    companion object {
        val drawerNavigationIds = DrawerParams.drawerButtons.map { param ->
            param.navigationId
        }
        val drawerTitles = DrawerParams.drawerButtons.map { param ->
            param.title
        }

        val drawerIcons = DrawerParams.drawerButtons.map { it.imageVector }
    }
}