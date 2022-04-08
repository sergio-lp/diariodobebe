package com.diariodobebe.models

import android.os.Parcel
import java.util.*

class Feeding(
    id: Int?,
    date: Long?,
    type: Int?,
    comment: String?,
    var feedingType: Int?,
    var rightBreastTime: Int?,
    var leftBreastTime: Int?,

    ) : Entry(id, date, type, comment) {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
    )

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeInt(id ?: 0)
        parcel.writeLong(date ?: Calendar.getInstance().timeInMillis)
        parcel.writeInt(type ?: 0)
        parcel.writeString(comment)
        parcel.writeInt(feedingType ?: 0)
        parcel.writeInt(rightBreastTime ?: 0)
        parcel.writeInt(leftBreastTime?: 0)
    }

    object FeedingType {
        const val FEEDING_BOTTLE = 0
        const val FEEDING_BREAST = 1
        const val FEEDING_FOOD = 2
    }
}

/*class Feeding(
    id: Int?,
    type: Int?,
    hasDetails: Boolean?,
    comment: String?,
    var feedingType: Int?,
    var leftBreastTime: String?,
    var rightBreastTime: String?
) : Entry(id, type, hasDetails, comment) {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte().compareTo(0) == 1,
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
        super.time = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id ?: 0)
        parcel.writeInt(type ?: 0)
        parcel.writeByte((if (hasDetails == true) 1 else 0).toByte())
        parcel.writeString(comment)
        parcel.writeInt(feedingType ?: 0)
        parcel.writeString(leftBreastTime)
        parcel.writeString(rightBreastTime)
        parcel.writeLong(super.time ?: 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        const val FEEDING_BOTTLE = 0
        const val FEEDING_BREAST = 1
        const val FEEDING_FOOD = 2

        @JvmField
        val CREATOR = object : Parcelable.Creator<Entry> {
            override fun createFromParcel(parcel: Parcel): Entry {
                return Entry(parcel)
            }

            override fun newArray(size: Int): Array<Entry?> {
                return arrayOfNulls(size)
            }
        }
    }
}*/