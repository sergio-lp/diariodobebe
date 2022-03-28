package com.diariodobebe.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Measurement(
    id: String?,
    type: Int?,
    hasDetails: Boolean?,
    comment: String?,
    var measureType: Int?,
    var value: Float?
) : Entry(id, type, hasDetails, comment), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte().compareTo(0) == 1,
        parcel.readString(),
        parcel.readInt(),
        parcel.readFloat()
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
        parcel.writeInt(this.measureType ?: 0)
        parcel.writeFloat(this.value ?: 0f)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        const val MEASURE_WEIGHT = 0
        const val MEASURE_HEIGHT = 0

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