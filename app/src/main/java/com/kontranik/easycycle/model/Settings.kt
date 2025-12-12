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
    var yearsOnStatistic: Int = 3
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(showOnStart)
        parcel.writeInt(daysOnHome)
        parcel.writeInt(yearsOnStatistic)
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
    var yearsOnStatistic: String
)

fun Settings.toUiState() : SettingsUiState {
    return SettingsUiState(
        showOnStart = this.showOnStart,
        daysOnHome = this.daysOnHome.toString(),
        yearsOnStatistic = this.yearsOnStatistic.toString()
    )
}

fun SettingsUiState.toSettings(): Settings {
    return Settings(
        showOnStart = this.showOnStart,
        daysOnHome = this.daysOnHome.toInt(),
        yearsOnStatistic = this.yearsOnStatistic.toInt()
    )
}