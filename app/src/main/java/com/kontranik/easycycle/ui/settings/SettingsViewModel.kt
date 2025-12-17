package com.kontranik.easycycle.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.easycycle.database.CycleRepository
import com.kontranik.easycycle.model.Settings
import com.kontranik.easycycle.service.AlarmScheduler
import com.kontranik.easycycle.storage.SettingsService
import com.kontranik.easycycle.ui.DrawerParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val context: Context,
    val cycleRepository: CycleRepository
) : ViewModel() {

    private val _settingsState = MutableStateFlow(Settings())
    val settingsState: StateFlow<Settings> = _settingsState

    init {
        viewModelScope.launch {
            _settingsState.value = SettingsService.loadSettings(context)
        }
    }

    fun changeShowOnStart(showOnStartId: Int) {
        if (_settingsState.value.showOnStart == showOnStartId) return
        viewModelScope.launch {
            val newSettings = _settingsState.value.copy(
                showOnStart = showOnStartId
            )
            save(newSettings)
        }
    }

    fun changeDaysOnHome(daysOnHome: Int) {
        if (_settingsState.value.daysOnHome == daysOnHome) return
        viewModelScope.launch {
            val newSettings = _settingsState.value.copy(
                daysOnHome = daysOnHome
            )
            save(newSettings)
        }
    }

    fun changeYearsOnStatistic(yearsInStatistic: Int) {
        if (_settingsState.value.yearsOnStatistic == yearsInStatistic) return
        viewModelScope.launch {
            val newSettings = _settingsState.value.copy(
                yearsOnStatistic = yearsInStatistic
            )
            save(newSettings)
        }
    }

    fun changeNotificationTime(hour: Int, min: Int) {
        if (_settingsState.value.notificationHour == hour && _settingsState.value.notificationMinute == min) return

        viewModelScope.launch(Dispatchers.IO) {
            val newSettings = _settingsState.value.copy(
                notificationHour = hour,
                notificationMinute = min
            )
            save(newSettings)

            // reschedule phases
            val lastCycle = cycleRepository.getLastOne()
            val phases = SettingsService.loadCustomPhases(context)
            lastCycle?.cycleStart?.let { cycleStart ->
                val alarmScheduler = AlarmScheduler(context)
                alarmScheduler.schedulePhaseNotifications(cycleStart, phases)
            }
        }
    }

    private fun save(newSettings: Settings) {
        SettingsService.saveSettings(
            newSettings,
            context
        )

        _settingsState.value = newSettings
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