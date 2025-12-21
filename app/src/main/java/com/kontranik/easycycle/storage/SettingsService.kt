package com.kontranik.easycycle.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.kontranik.easycycle.constants.DefaultPhasesData
import com.kontranik.easycycle.model.Phase
import com.kontranik.easycycle.model.Settings
import androidx.core.content.edit


class SettingsService {
    companion object {
        private val gson = Gson()

        private var phasesInstance = emptyList<Phase>()
        private var settingsInstance: Settings? = null

        fun saveSettings(settings: Settings, context: Context) {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(PREFERENCES_FILE_NAME, 0)
            sharedPreferences.edit {
                val serializedObject = gson.toJson(settings)
                putString(APP_SETTINGS, serializedObject)
                apply()
            }
            settingsInstance = settings
        }

        fun loadSettings(context: Context): Settings {
            if (settingsInstance != null) return settingsInstance!!

            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(PREFERENCES_FILE_NAME, 0)
            val json = sharedPreferences.getString(APP_SETTINGS, null)
            settingsInstance = gson.fromJson(json, Settings::class.java)
            settingsInstance?.let { sI ->
                if (sI.notificationHour == null || sI.notificationMinute == null) {
                    settingsInstance = settingsInstance?.copy(
                        notificationHour = 9,
                        notificationMinute = 0
                    )
                }
            }
            return settingsInstance ?: Settings()
        }

        fun saveCustomPhase(context: Context, phase: Phase): List<Phase> {
            saveCustomPhases(context, loadCustomPhases(context)
                .filter { it.key != phase.key }
                .plus(phase)
                .sortedBy { it.from })
            return phasesInstance
        }

        fun removeCustomPhase(context: Context, key: Long): List<Phase> {
            val phases = loadCustomPhases(context).filter {
                it.key != key
            }
            saveCustomPhases(context, phases)
            return phasesInstance
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
            phasesInstance = phases.sortedBy { it.from }
        }

        fun loadCustomPhases(context: Context): List<Phase> {
            if (phasesInstance.isNotEmpty()) return phasesInstance

            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(PREFERENCES_FILE_NAME, 0)
            val phasesSet = sharedPreferences.getStringSet(CUSTOM_PHASES, null)

            try {
                phasesInstance = phasesSet?.mapNotNull { json ->
                    val p = gson.fromJson(json, Phase::class.java)
                    if (p.notificateStart == null) {
                        p.notificateStart = true
                    }
                    return@mapNotNull p
                }?.sortedBy { it.from }
                    ?: DefaultPhasesData.ar.sortedBy { it.from }
            } catch (e: Exception) {
                Log.e("SettingsService", "Error loading custom phases: ${e.message}")
                e.printStackTrace()
                phasesInstance = DefaultPhasesData.ar.sortedBy { it.from }
            }
            return phasesInstance
        }

        fun wipeCustomPhases(context: Context): List<Phase> {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(PREFERENCES_FILE_NAME, 0)
            sharedPreferences.edit {
                remove(CUSTOM_PHASES)
                apply()
            }
            phasesInstance = DefaultPhasesData.ar.sortedBy { it.from }
            return phasesInstance
        }

        private const val PREFERENCES_FILE_NAME = "EASYCYCLE_PREFS"
        private const val APP_SETTINGS = "APP_SETTINGS"
        private const val CUSTOM_PHASES = "CUSTOM_PHASES"
    }
}