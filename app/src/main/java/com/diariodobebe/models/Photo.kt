package com.diariodobebe.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Photo(
    id: Int?,
    date: Long?,
    type: Int?,
    comment: String?,
    var path: String?
) : Entry(id, date, type, comment), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeInt(id ?: 0)
        parcel.writeLong(date ?: Calendar.getInstance().timeInMillis)
        parcel.writeInt(type ?: 0)
        parcel.writeString(comment)
        parcel.writeString(path)
    }

    companion object {
        const val EXTRA_PATH: String = "photo_path"
        const val EXTRA_HAS_PHOTO = "has_photo"

        @JvmField
        val CREATOR: Parcelable.Creator<Photo> = object : Parcelable.Creator<Photo> {
            override fun createFromParcel(parcel: Parcel): Photo {
                return Photo(parcel)
            }

            override fun newArray(size: Int): Array<Photo?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}