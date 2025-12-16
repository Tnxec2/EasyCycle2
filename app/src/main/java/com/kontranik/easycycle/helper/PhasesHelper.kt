package com.kontranik.easycycle.helper

import android.app.Application
import android.content.Context
import android.util.Log
import com.kontranik.easycycle.database.Cycle
import com.kontranik.easycycle.model.CDay

import com.kontranik.easycycle.model.Phase
import com.kontranik.easycycle.storage.SettingsService
import java.util.*

class PhasesHelper {
    companion object {
        fun getDaysInfo(context: Context, amount: Int, lastCycle: Cycle): List<CDay> {
            val dateNow = GregorianCalendar()
            dateNow.time = Date()

            val allDays: MutableList<CDay> = mutableListOf()
            for (day in 0 until amount) {
                val diff = TimeHelper.getDifferenceInDays(dateNow.time, lastCycle.cycleStart!!)
                if (diff >= 0) {
                    val cDay = diff + 1
                    val repeated = false
                    val dayPhases = getPhasesByDay(context, cDay)
                    var color: String? = null
                    dayPhases.forEach {
                        val tempColor = if (repeated) it.colorP else it.color
                        if (tempColor != null) {
                            // in tabinfoscreen ignore markwholephase property
                            if (cDay >= it.from && (it.to == null || cDay <= it.to!!)) color = tempColor
                        }
                    }
                    allDays.add(
                        CDay(
                            id = dateNow.time.time,
                            date = dateNow.time,
                            cyclesDay = cDay,
                            phases = dayPhases,
                            color = color
                        )
                    )
                }

                dateNow.add(Calendar.DAY_OF_YEAR, 1)
            }
            return allDays
        }

        fun getPhasesByDay(context: Context, day: Int): List<Phase> {
            val phases = SettingsService.loadCustomPhases(context)
            return phases.filter { phase ->
                (phase.to == null && day == phase.from)
                ||
                (phase.to != null && day >= phase.from && day <= phase.to!!)
            }
        }

        fun getPhases(context: Context): List<Phase> {
            return SettingsService.loadCustomPhases(context)
        }
    }
}