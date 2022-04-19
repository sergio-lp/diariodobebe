package com.diariodobebe.models

import android.os.Parcel
import android.os.Parcelable

class Diaper(
    id: Int?,
    date: Long?,
    type: Int?,
    comment: String?,
    var diaperBrand: String?,
    var state: Int?
) : Entry(id, date, type, comment), Parcelable {
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
        const val STATE_BOTH = 3

        @JvmField
        val CREATOR: Parcelable.Creator<Diaper> = object : Parcelable.Creator<Diaper> {
            override fun createFromParcel(parcel: Parcel): Diaper {
                return Diaper(parcel)
            }

            override fun newArray(size: Int): Array<Diaper?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}