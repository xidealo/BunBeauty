package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.Duration

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)data class Service(
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    var name: String = "",
    var duration: Float = 0.5f,
    var address: String = "",
    var description: String = "",
    var category: String = "",
    var rating: Float = 0f,
    var countOfRates: Long = 0,
    var cost: Long = 0L,
    var creationDate: Long = 0L,
    var premiumDate: Long = 0L,
    @Ignore
    var tags: ArrayList<Tag> = ArrayList()
) : Serializable {

    companion object {
        const val SERVICES = "services"
        const val SERVICE = "service"
        const val SERVICE_OWNER = "service owner"
        const val USER_ID = "user id"
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
}
