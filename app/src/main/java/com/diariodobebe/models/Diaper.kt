package com.diariodobebe.models

import android.os.Parcel

class Diaper(
    id: Int?,
    date: Long?,
    type: Int?,
    comment: String?,
    var diaperBrand: String?,
    var state: Int?
) : Entry(id, date, type, comment) {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeInt(id ?: 0)
        parcel.writeLong(date ?: 0)
        parcel.writeInt(type ?: 0)
        parcel.writeString(comment)
        parcel.writeString(diaperBrand)
        parcel.writeInt(state ?: 0)
    }

    companion object {
        const val STATE_DRY = 0
        const val STATE_PEE = 1
        const val STATE_POOP = 2
    }
}