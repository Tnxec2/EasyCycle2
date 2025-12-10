package com.kontranik.easycycle.constants

import com.kontranik.easycycle.R
import com.kontranik.easycycle.model.Settings

class DefaultSettings {
    companion object {

        val settings = Settings(
            showOnStart = "HOME",
            daysOnHome = 7,
            yearsOnStatistic = 3,
        )
        const val defaultCycleLength: Int = 28

    }
}