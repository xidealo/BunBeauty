package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity

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
    var serviceId: String = "",
    var width: Int = 0,
    var height: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(link)
        parcel.writeString(userId)
        parcel.writeString(serviceId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Photo> {

        const val PHOTOS_EXTRA = "photos"
        const val PHOTO_EXTRA = "photo"
        const val LINK = "link"

        override fun createFromParcel(parcel: Parcel): Photo {
            return Photo(parcel)
        }

        override fun newArray(size: Int): Array<Photo?> {
            return arrayOfNulls(size)
        }
    }
}
