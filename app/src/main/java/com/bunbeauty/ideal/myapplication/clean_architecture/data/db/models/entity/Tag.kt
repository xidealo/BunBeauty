package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
    (
    foreignKeys = [ForeignKey(
        entity = Service::class,
        parentColumns = ["id"],
        childColumns = ["serviceId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Tag(
    @PrimaryKey
    var id: String = "",
    @ColumnInfo(index = true)
    var serviceId: String = "",
    var tag: String = "",
    var userId: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(tag)
        parcel.writeString(serviceId)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tag> {
        const val TAGS = "tags"
        const val TAG = "tag"

        override fun createFromParcel(parcel: Parcel): Tag {
            return Tag(parcel)
        }

        override fun newArray(size: Int): Array<Tag?> {
            return arrayOfNulls(size)
        }
    }
}