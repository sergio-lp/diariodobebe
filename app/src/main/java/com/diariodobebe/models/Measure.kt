package com.diariodobebe.models

import android.os.Parcel
import java.util.*

class Measure(
    id: Int?,
    date: Long?,
    type: Int?,
    comment: String?,
    var height: Float?,
    var weight: Float?
) : Entry(
    id, date, type, comment
) {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeInt(id ?: 0)
        parcel.writeLong(date ?: Calendar.getInstance().timeInMillis)
        parcel.writeInt(type ?: 0)
        parcel.writeString(comment)
        parcel.writeFloat(height ?: 0f)
        parcel.writeFloat(weight ?: 0f)
    }

}