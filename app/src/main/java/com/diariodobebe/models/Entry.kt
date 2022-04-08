package com.diariodobebe.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

open class Entry(
    var id: Int?,
    var date: Long?,
    var type: Int?,
    var comment: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeInt(id ?: 0)
        parcel.writeLong(date ?: Calendar.getInstance().timeInMillis)
        parcel.writeInt(type ?: 0)
        parcel.writeString(comment)
    }

    object EntryType {
        const val ENTRY_SLEEP = 0
        const val ENTRY_FEEDING = 1
        const val ENTRY_HEALTH = 2
        const val ENTRY_MEASUREMENT = 3
        const val ENTRY_EVENT = 4
        const val ENTRY_DIAPER = 5
    }

    companion object CREATOR : Parcelable.Creator<Entry> {
        override fun createFromParcel(parcel: Parcel): Entry {
            return Entry(parcel)
        }

        override fun newArray(size: Int): Array<Entry?> {
            return arrayOfNulls(size)
        }
    }

}

/*open class Entry(
    var id: Int?,
    var type: Int?,
    var hasDetails: Boolean?,
    var comment: String?
) : Parcelable {
    var time: Long? = null

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte().compareTo(0) == 1,
        parcel.readString()
    ) {
        time = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id ?: 0)
        parcel.writeLong(time ?: 0)
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
}*/