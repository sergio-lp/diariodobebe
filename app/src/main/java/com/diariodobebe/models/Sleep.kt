package com.diariodobebe.models

import android.os.Parcel
import java.util.*

class Sleep(id: Int?, date: Long?, type: Int?, comment: String?, var duration: Int?) :
    Entry(id, date, type, comment) {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeInt(id ?: 0)
        parcel.writeLong(date ?: Calendar.getInstance().timeInMillis)
        parcel.writeInt(type ?: 0)
        parcel.writeString(comment)
        parcel.writeInt(duration ?: 0)
    }

}