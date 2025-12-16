package com.kontranik.easycycle.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.kontranik.easycycle.constants.DefaultPhasesData
import com.kontranik.easycycle.model.Phase
import com.kontranik.easycycle.model.Settings
import androidx.core.content.edit


class SettingsService {
    companion object {
        private val gson = Gson()

        fun saveSettings(settings: Settings, context: Context) {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(PREFERENCES_FILE_NAME, 0)
            sharedPreferences.edit {
                val serializedObject = gson.toJson(settings)
                putString(APP_SETTINGS, serializedObject)
                apply()
            }
        }

        fun loadSettings(context: Context): Settings {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(PREFERENCES_FILE_NAME, 0)
            val settings = sharedPreferences.getString(APP_SETTINGS, null)
            return gson.fromJson(settings, Settings::class.java) ?: Settings()
        }

        fun saveCustomPhase(context: Context, phase: Phase): List<Phase> {
            val phases = loadCustomPhases(context).toMutableList()
            var updated = false
            for( i in phases.indices) {
                val it = phases[i]
                if ( it.key == phase.key) {
                    it.from = phase.from
                    it.to = phase.to
                    it.desc = phase.desc
                    it.color = phase.color
                    it.colorP = phase.colorP
                    it.markwholephase = phase.markwholephase
                    updated = true
                    break
                }
            }
            if (!updated) phases.add(phase)
            saveCustomPhases(context, phases.sortedBy { it.from })
            return phases.sortedBy { it.from }
        }

        fun removeCustomPhase(context: Context, key: Long): List<Phase> {
            val phases = loadCustomPhases(context).filter {
                it.key != key
            }
            saveCustomPhases(context, phases)
            return phases
        }

        fun saveCustomPhases(context: Context, phases: List<Phase>) {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(PREFERENCES_FILE_NAME, 0)
            sharedPreferences.edit {
                val resultSet = mutableSetOf<String>()
                phases.forEach {
                    val serializedObject = gson.toJson(it)
                    if (serializedObject != null) {
                        resultSet.add(serializedObject)
                    }
                }
                putStringSet(CUSTOM_PHASES, resultSet)
                apply()
            }
        }

        fun loadCustomPhases(context: Context): List<Phase> {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(PREFERENCES_FILE_NAME, 0)
            val phasesSet = sharedPreferences.getStringSet(CUSTOM_PHASES, null)
            val result = mutableListOf<Phase>()
            return if ( phasesSet != null) {
                phasesSet.forEach {
                    val phase = gson.fromJson(it, Phase::class.java)
                    if ( phase != null) result.add(phase)
                }
                result.sortedBy { it.from }
            } else {
                DefaultPhasesData.ar.sortedBy { it.from }
            }
        }

        fun wipeCustomPhases(context: Context): List<Phase> {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(PREFERENCES_FILE_NAME, 0)
            sharedPreferences.edit {
                remove(CUSTOM_PHASES)
                apply()
            }
            return DefaultPhasesData.ar.sortedBy { it.from }
        }

        private const val PREFERENCES_FILE_NAME = "EASYCYCLE_PREFS"
        private const val APP_SETTINGS = "APP_SETTINGS"
        private const val CUSTOM_PHASES = "CUSTOM_PHASES"
    }
}