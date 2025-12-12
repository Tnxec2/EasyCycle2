package com.kontranik.easycycle

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.database.Cycle
import com.kontranik.easycycle.database.CycleRepository
import com.kontranik.easycycle.model.LastCycle
import com.kontranik.easycycle.model.Phase
import com.kontranik.easycycle.model.Settings
import com.kontranik.easycycle.model.StatisticItem
import com.kontranik.easycycle.ui.calendar.CalendarViewModel.Companion.sdfISO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Locale



class MainViewModel(
    private val context: Context,
    private val cycleRepository: CycleRepository
    ) : ViewModel() {

    val settings = MutableLiveData<Settings>()
    val phases = MutableLiveData<List<Phase>>(listOf())
    val lastCycle = MutableLiveData<LastCycle?>()
    val averageCycleLength = MutableLiveData<Int>(DefaultSettings.defaultCycleLength)

    val statisticList = MutableLiveData<List<StatisticItem>>()

    init {
//        settings.value = SettingsService.loadSettings(context) ?: DefaultSettings.settings
        loadPhases()
        loadLastOneCycle()
    }

    fun saveSettings(newSettings: Settings) {
       //  SettingsService.saveSettings(newSettings, context)
        settings.value = newSettings
    }

    fun loadPredefindePhases() {
        // SettingsService.removeCustomPhases(context)
        loadPhases()
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

    fun removePhase(phase: Phase) {
//        val newPhases = phases.value!!.filter { it.key != phase.key }
//        SettingsService.saveCustomPhases(context, newPhases)
//        phases.value = newPhases
    }

    fun savePhase(phase: Phase) {
//        val newPhases = SettingsService.saveCustomPhase( context, phase)
//        phases.value = newPhases.sortedWith(compareBy({ it.from }, { it.to }))
    }

    fun loadListOfYearsStatistic(yearsOnStatistic: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val items = cycleRepository.getArchivList(yearsOnStatistic)
            statisticList.postValue(items)
        }
    }

    fun saveCycleItem(item: Cycle) {
        viewModelScope.launch(Dispatchers.IO) {
            cycleRepository.add(item)
            loadLastOneCycle()
        }
    }

    private fun loadLastOneCycle() {
        viewModelScope.launch(Dispatchers.IO) {
            val last = cycleRepository.getLastOne()
            lastCycle.postValue(last)
            loadAverageLength()
        }
    }

    private fun loadAverageLength() {
        viewModelScope.launch(Dispatchers.IO) {
            val databaseAverageLength = cycleRepository.getAverageLength()
            // averageCycleLength.postValue(databaseAverageLength ?: DefaultSettings.defaultCycleLength)
        }
    }

    fun importStatisticFromFile(fileUri: Uri?) {
        if ( fileUri != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(fileUri)
                    val r = BufferedReader(InputStreamReader(inputStream))
                    var countSaved: Int = 0
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
                                    val cycleStart = sdfISO.parse(ar[0])
                                    val length = ar[1].toInt()
                                    if (cycleStart != null) {
                                        val cycleItem = Cycle(
                                            cycleStart = cycleStart,
                                            lengthOfLastCycle = length
                                        )
                                        cycleRepository.add(cycleItem)
                                        countSaved += 1
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                    r.close()
                    inputStream?.close()

                    Log.d("EasyCycle", "Imported items $countSaved")
                    if ( countSaved > 0) {
                        Log.d("EasyCycle", "set lastCycle...")
                        loadLastOneCycle()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun deleteStatisticItemById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            cycleRepository.deleteById(id)
            loadLastOneCycle()
        }
    }
}