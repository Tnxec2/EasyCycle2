package com.kontranik.easycycle.model

import android.os.Parcel
import android.os.Parcelable
import com.kontranik.easycycle.R

class Settings(
    var showOnStart: String = "HOME",
    var daysOnHome: Int = 5,
    var yearsOnStatistic: Int = 3
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "HOME",
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(showOnStart)
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