package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["ownerId"],
        onDelete = ForeignKey.CASCADE
    )]
)
class Subscriber(
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    var date: String = "",
    var subscriberId: String = "" // who
) {

    companion object{
        const val SUBSCRIBERS = "subscribers"
        const val SUBSCRIBER_ID = "subscriber id"
        const val DATE = "date"
        const val USER_ID = "user id"
    }
}