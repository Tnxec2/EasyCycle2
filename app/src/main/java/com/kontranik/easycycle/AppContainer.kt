package com.kontranik.easycycle


import android.content.Context
import com.kontranik.easycycle.database.AppDatabase
import com.kontranik.easycycle.database.CycleRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val cycleRepository: CycleRepository
}


class AppDataContainer(private val context: Context) : AppContainer {

    override val cycleRepository: CycleRepository by lazy {
        CycleRepository(
            AppDatabase.getDatabase(context).cycleDao(),
        )
    }
}
