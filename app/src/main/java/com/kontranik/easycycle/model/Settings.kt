package com.kontranik.easycycle.model

import android.os.Parcel
import android.os.Parcelable

const val navigation_info = 0
const val navigation_calendar = 1
const val navigation_statistic = 2
const val navigation_phases = 3


data class Settings(
    var showOnStart: Int = navigation_info,
    var daysOnHome: Int = 5,
    var yearsOnStatistic: Int = 3,
    val notificationHour: Int? = 7,
    val notificationMinute: Int? = 0,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(showOnStart)
        parcel.writeInt(daysOnHome)
        parcel.writeInt(yearsOnStatistic)
        notificationHour?.let { parcel.writeInt(it) }
        notificationMinute?.let { parcel.writeInt(it) }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Settings> {
        override fun createFromParcel(parcel: Parcel): Settings {
            return Settings(parcel)
        }

        override fun newArray(size: Int): Array<Settings?> {
            return arrayOfNulls(size)
        }
    }
}

data class SettingsUiState (
    var showOnStart: Int,
    var daysOnHome: String,
    var yearsOnStatistic: String,
    val notificationHour: Int,
    val notificationMinute: Int
)

fun Settings.toUiState() : SettingsUiState {
    return SettingsUiState(
        showOnStart = this.showOnStart,
        daysOnHome = this.daysOnHome.toString(),
        yearsOnStatistic = this.yearsOnStatistic.toString(),
        notificationHour = this.notificationHour ?: 7,
        notificationMinute = this.notificationMinute ?: 0
    )
}

fun SettingsUiState.toSettings(): Settings {
    return Settings(
        showOnStart = this.showOnStart,
        daysOnHome = this.daysOnHome.toInt(),
        yearsOnStatistic = this.yearsOnStatistic.toInt(),
        notificationHour = this.notificationHour,
        notificationMinute = this.notificationMinute,
    )
}