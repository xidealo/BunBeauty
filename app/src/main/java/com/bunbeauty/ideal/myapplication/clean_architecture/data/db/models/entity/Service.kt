package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Service(
    @PrimaryKey
    var id: String = "",
    var userId: String,
    var name: String,
    var duration: Float,
    var address: String,
    var description: String,
    var category: String,
    var rating: Float = 0f,
    var countOfRates: Long = 0,
    var cost: Long,
    var creationDate: Long = 0L,
    var premiumDate: Long = 0L
) : Parcelable {
    @Ignore
    var tags: ArrayList<Tag> = ArrayList()

    @Ignore
    var photos: ArrayList<Photo> = arrayListOf()

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong()
    )

    companion object CREATOR : Parcelable.Creator<Service> {

        override fun createFromParcel(parcel: Parcel): Service {
            return Service(parcel)
        }

        override fun newArray(size: Int): Array<Service?> {
            return arrayOfNulls(size)
        }

        const val SERVICES = "services"
        const val SERVICE = "service"

        const val NAME = "name"
        const val SERVICE_PHOTO = "service photo"
        const val DURATION = "duration"
        const val ADDRESS = "address"
        const val DESCRIPTION = "description"
        const val CATEGORY = "category"
        const val COST = "cost"
        const val AVG_RATING = "avg rating"
        const val COUNT_OF_RATES = "count of rates"
        const val CREATION_DATE = "creation date"
        const val PREMIUM_DATE = "premium date"
        const val DEFAULT_PREMIUM_DATE = 0L
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeString(name)
        parcel.writeFloat(duration)
        parcel.writeString(address)
        parcel.writeString(description)
        parcel.writeString(category)
        parcel.writeFloat(rating)
        parcel.writeLong(countOfRates)
        parcel.writeLong(cost)
        parcel.writeLong(creationDate)
        parcel.writeLong(premiumDate)
    }

    override fun describeContents(): Int {
        return 0
    }

}
