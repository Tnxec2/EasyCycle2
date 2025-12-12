package com.kontranik.easycycle.model

import com.kontranik.easycycle.database.Cycle

class StatisticItem(
    val year: String,
    val items: MutableList<Cycle>,
    val averageCycleLength: Int,
) {
}