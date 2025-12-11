package com.kontranik.easycycle


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kontranik.easycycle.database.CycleViewModel
import com.kontranik.easycycle.ui.calendar.CalendarViewModel
import com.kontranik.easycycle.ui.phases.PhasesViewModel
import com.kontranik.easycycle.ui.settings.SettingsViewModel


/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    val Factory = viewModelFactory {

        initializer {
            CycleViewModel(
                application().applicationContext,
                application().container.cycleRepository
            )
        }


        initializer {
            MainViewModel(
                application().applicationContext,
                application().container.cycleRepository
            )
        }

        initializer {
            SettingsViewModel(
                application().applicationContext,
            )
        }

        initializer {
            PhasesViewModel(
                application().applicationContext,
            )
        }

        initializer {
            CalendarViewModel(
                application(),
                application().container.cycleRepository
            )
        }

    }
}


/**
 * Extension function to queries for [EasyCylceApplication] object and returns an instance of
 * [EasyCylceApplication].
 */
fun CreationExtras.application(): EasyCylceApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as EasyCylceApplication)
