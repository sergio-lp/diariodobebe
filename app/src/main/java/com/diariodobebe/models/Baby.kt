package com.diariodobebe.models

class Baby(
    var id: Int?,
    var name: String?,
    var sex: Int?,
    var birthDate: Long?,
    var picPath: String?,
    var lastEntryId: Int?,
    var entryList: MutableList<Entry>?
) {


    object BabyGender {
        const val BABY_GENDER_MALE = 0
        const val BABY_GENDER_FEMALE = 1
    }

}

/*class Baby(
    var name: String?,
    var sex: Int?,
    var birthDay: Long?,
    var picPath: String?,
    var lastId: Int? = 0,
    var feedingList: MutableList<Feeding>? = mutableListOf(),
    var measurementList: MutableList<Measurement>? = mutableListOf(),
    var healthList: MutableList<HealthEvent>? = mutableListOf(),
    var sleepList: MutableList<Sleep>? = mutableListOf(),
    var diaperList: MutableList<Diaper>? = mutableListOf(),
    var eventList: MutableList<Event>? = mutableListOf()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(sex ?: 0)
        parcel.writeLong(birthDay ?: 0)
        parcel.writeString(picPath)
        parcel.writeInt(lastId ?: 0)
    }

    object BabyGender {
        const val BABY_GENDER_MALE = 0
        const val BABY_GENDER_FEMALE = 1
    }

    companion object CREATOR : Parcelable.Creator<Baby> {
        override fun createFromParcel(parcel: Parcel): Baby {
            return Baby(parcel)
        }

        override fun newArray(size: Int): Array<Baby?> {
            return arrayOfNulls(size)
        }
    }*/
