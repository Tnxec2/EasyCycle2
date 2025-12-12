package com.kontranik.easycycle.ui.statistic

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.easycycle.database.CycleRepository
import com.kontranik.easycycle.model.StatisticItem
import com.kontranik.easycycle.storage.SettingsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatisticViewModel(
    private val context: Context,
    private val repository: CycleRepository
) : ViewModel() {


    private val _statisticList = MutableStateFlow<List<StatisticItem>>(emptyList())
    val statisticList: StateFlow<List<StatisticItem>> = _statisticList

    val averageLength = repository.getAverageLength()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearOldStatistic()
        }
    }

    fun loadStatistic() {
        viewModelScope.launch(Dispatchers.IO) {
            val yearsOnStatistic = SettingsService.loadSettings(context).yearsOnStatistic
            _statisticList.value = repository.getArchivList(yearsOnStatistic)
        }
    }

    fun deleteCycleById(id: Long?) {
        if (id == null) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteById(id)
            loadStatistic()
        }
    }
}