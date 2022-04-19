package com.diariodobebe.models

import android.os.Parcel
import android.os.Parcelable

class Health(
    id: Int?,
    date: Long?,
    type: Int?,
    comment: String?,
    var healthEvent: String?,
    var medication: String?,
    var medAmount: Int?,
    var temperature: Int?,
    var mood: Int?,
    var medTime: Long?,
    var vitalsTime: Long?,
    var symptoms: MutableList<String>? = mutableListOf<String>()
) : Entry(id, date, type, comment), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readLong()
    ) {
        symptoms?.let {
            parcel.readStringList(it)
        }
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeInt(id ?: 0)
        parcel.writeLong(date ?: 0)
        parcel.writeInt(type ?: 0)
        parcel.writeString(comment)
        parcel.writeString(healthEvent)
        parcel.writeString(medication)
        parcel.writeInt(medAmount ?: 0)
        parcel.writeInt(temperature ?: -1)
        parcel.writeInt(mood ?: -1)
        parcel.writeLong(medTime ?: 0)
        parcel.writeLong(vitalsTime ?: 0)
        parcel.writeStringList(symptoms)
    }


    companion object {
        const val MOOD_BAD = 0
        const val MOOD_NORMAL = 1
        const val MOOD_GOOD = 2

        @JvmField
        val CREATOR: Parcelable.Creator<Health> = object : Parcelable.Creator<Health> {
            override fun createFromParcel(parcel: Parcel): Health {
                return Health(parcel)
            }

            override fun newArray(size: Int): Array<Health?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}