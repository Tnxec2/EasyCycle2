package com.kontranik.easycycle.model

class StatisticItem(
    val year: String,
    val items: MutableList<LastCycle>,
    val averageCycleLength: Int,
) {
}