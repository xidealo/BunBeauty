package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo(
    @PrimaryKey
    var id: String = "",
    var link: String = "",
    var userId: String = "",
    var uri: String = "",
    var serviceId: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(link)
        parcel.writeString(userId)
        parcel.writeString(uri)
        parcel.writeString(serviceId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Photo> {

        const val PHOTOS = "photos"
        const val PHOTO = "photo"
        const val LINK = "link"
        const val SERVICE_ID = "service id"

        override fun createFromParcel(parcel: Parcel): Photo {
            return Photo(parcel)
        }

        override fun newArray(size: Int): Array<Photo?> {
            return arrayOfNulls(size)
        }
    }
}
