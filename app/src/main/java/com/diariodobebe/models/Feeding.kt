package com.diariodobebe.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Feeding(
    id: Int?,
    date: Long?,
    type: Int?,
    comment: String?,
    var feedingType: Int? = 0,
    var rightBreastTime: Int? = 0,
    var leftBreastTime: Int? = 0,
    var milliliters: Int? = 0,
    var foodType: String? = ""

) : Entry(id, date, type, comment), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeInt(id ?: 0)
        parcel.writeLong(date ?: Calendar.getInstance().timeInMillis)
        parcel.writeInt(type ?: 0)
        parcel.writeString(comment)
        parcel.writeInt(feedingType ?: 0)
        parcel.writeInt(rightBreastTime ?: 0)
        parcel.writeInt(leftBreastTime ?: 0)
        parcel.writeInt(milliliters ?: 0)
        parcel.writeString(foodType)
    }

    object FeedingType {
        const val FEEDING_BOTTLE = 0
        const val FEEDING_BREAST = 1
        const val FEEDING_FOOD = 2
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Feeding> {
        override fun createFromParcel(parcel: Parcel): Feeding {
            return Feeding(parcel)
        }

        override fun newArray(size: Int): Array<Feeding?> {
            return arrayOfNulls(size)
        }
    }
}