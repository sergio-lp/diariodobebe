package com.diariodobebe.models

import android.os.Parcel
import java.util.*

class Event(id: Int?, date: Long?, type: Int?, comment: String?, var activityType: String?) :
    Entry(id, date, type, comment) {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeInt(id ?: 0)
        parcel.writeLong(date ?: Calendar.getInstance().timeInMillis)
        parcel.writeInt(type ?: 0)
        parcel.writeString(comment)
        parcel.writeString(activityType)
    }
}