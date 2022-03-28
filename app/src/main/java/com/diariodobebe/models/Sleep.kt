package com.diariodobebe.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Sleep(
    id: String?,
    type: Int?,
    hasDetails: Boolean?,
    comment: String?,
) : Entry(id, type, hasDetails, comment), Parcelable {
    var timeEnd: Date = Date()
    var duration: Date = Date()

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte().compareTo(0) == 1,
        parcel.readString()
    ) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = parcel.readLong()
        super.time = calendar.time

        calendar.timeInMillis = parcel.readLong()
        this.timeEnd = calendar.time

        val duration = Date()
        duration.time = this.timeEnd!!.time - super.time!!.time
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
}