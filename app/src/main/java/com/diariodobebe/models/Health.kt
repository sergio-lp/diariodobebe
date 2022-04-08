package com.diariodobebe.models

import android.os.Parcel

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
    var symptoms: Array<String>? = arrayOf()
) : Entry(id, date, type, comment) {
    constructor(parcel: Parcel): this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        null
    ) {
        parcel.readStringArray(symptoms!!)
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeInt(id ?: 0)
        parcel.writeLong(date ?: 0)
        parcel.writeInt(type ?: 0)
        parcel.writeString(comment)
        parcel.writeString(healthEvent)
        parcel.writeString(medication)
        parcel.writeInt(medAmount?: 0)
        parcel.writeInt(temperature ?: 0)
        parcel.writeInt(mood ?: 0)
        parcel.writeStringArray(symptoms)
    }


    companion object {
        const val MOOD_BAD = 0
        const val MOOD_NORMAL = 1
        const val MOOD_GOOD = 2
    }
}