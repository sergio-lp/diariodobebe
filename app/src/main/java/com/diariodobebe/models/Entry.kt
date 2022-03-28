package com.diariodobebe.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

open class Entry(
    var id: String?,
    var type: Int?,
    var hasDetails: Boolean?,
    var comment: String?
) : Parcelable {
    var time: Date? = null

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte().compareTo(0) == 1,
        parcel.readString()
    ) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = parcel.readLong()
        this.time = calendar.time
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        val calendar = Calendar.getInstance()
        calendar.time = this.time ?: Date()
        parcel.writeLong(calendar.timeInMillis)
        parcel.writeInt(type ?: 0)
        parcel.writeByte((if (hasDetails == true) 1 else 0).toByte())
        parcel.writeString(comment)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object {
        const val ENTRY_SLEEP = 0
        const val ENTRY_FEEDING = 1
        const val ENTRY_HEALTH = 2
        const val ENTRY_MEASUREMENT = 3

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