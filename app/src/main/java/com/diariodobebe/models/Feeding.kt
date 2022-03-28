package com.diariodobebe.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Feeding(
    id: String?,
    type: Int?,
    hasDetails: Boolean?,
    comment: String?,
    var feedingType: Int?,
    var leftBreastTime: Long?,
    var rightBreastTime: Long?
) : Entry(id, type, hasDetails, comment) {
    var timeEnd: Date = Date()

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte().compareTo(0) == 1,
        parcel.readString(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readLong()
    ) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = parcel.readLong()
        super.time = calendar.time

        calendar.timeInMillis = parcel.readLong()
        timeEnd = calendar.time
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        val calendar = Calendar.getInstance()
        calendar.time = this.time ?: Date()
        parcel.writeLong(calendar.timeInMillis)
        parcel.writeInt(type ?: 0)
        parcel.writeByte((if (hasDetails == true) 1 else 0).toByte())
        parcel.writeString(comment)
        parcel.writeInt(feedingType ?: 0)
        parcel.writeLong(leftBreastTime ?: 0)
        parcel.writeLong(rightBreastTime ?: 0)
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
}