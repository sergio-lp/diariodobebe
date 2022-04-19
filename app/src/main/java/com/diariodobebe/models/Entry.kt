package com.diariodobebe.models

open class Entry(
    var id: Int?,
    var date: Long?,
    var type: Int?,
    var comment: String?
) {

    /*constructor(parcel: Parcel) : this(
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
    }*/

    object EntryType {
        const val ENTRY_SLEEP = 0
        const val ENTRY_FEEDING = 1
        const val ENTRY_HEALTH = 2
        const val ENTRY_MEASUREMENT = 3
        const val ENTRY_EVENT = 4
        const val ENTRY_DIAPER = 5
        const val ENTRY_PICTURE = 6
    }

    /*companion object CREATOR : Parcelable.Creator<Entry> {
        override fun createFromParcel(parcel: Parcel): Entry {
            return Entry(parcel)
        }

        override fun newArray(size: Int): Array<Entry?> {
            return arrayOfNulls(size)
        }
    }*/

}