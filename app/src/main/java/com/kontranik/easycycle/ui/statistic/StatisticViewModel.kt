package com.kontranik.easycycle.ui.statistic

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.easycycle.database.Cycle
import com.kontranik.easycycle.database.CycleRepository
import com.kontranik.easycycle.model.StatisticItem
import com.kontranik.easycycle.sdfIso
import com.kontranik.easycycle.storage.SettingsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

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

    fun importStatisticFromFile(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val r = BufferedReader(InputStreamReader(inputStream))
                val cycles = mutableListOf<Cycle>()
                var line: String? = ""
                while (true) {
                    try {
                        if ( (r.readLine().also { line = it }) == null) break
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    var delimiter = ""
                    if ( line!!.contains(";")) delimiter = ";"
                    else if (line.contains(",")) delimiter = ","
                    if ( delimiter != "") {
                        val ar = line.split(delimiter)
                        if (ar.size >= 2) {
                            try {
                                val cycleStart = sdfIso.parse(ar[0])
                                val length = ar[1].toInt()
                                if (cycleStart != null) {
                                    val cycleItem = Cycle(
                                        cycleStart = cycleStart,
                                        lengthOfLastCycle = length
                                    )
                                    cycles.add(cycleItem)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                r.close()
                inputStream?.close()

                Log.d("EasyCycle", "Imported items ${cycles.size}")
                if ( cycles.isNotEmpty()) {
                    repository.addAll(cycles)
                    loadStatistic()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}