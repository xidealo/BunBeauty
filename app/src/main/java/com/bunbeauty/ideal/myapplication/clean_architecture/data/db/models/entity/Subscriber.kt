package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
class Subscriber(
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    var date: Long = 0,
    var subscriberId: String = "", // who
    @Ignore
    var subscriberUser: User = User()
) {

    companion object {
        const val SUBSCRIBERS = "subscribers"
        const val SUBSCRIBER_ID = "subscriber id"
        const val DATE = "date"
        const val USER_ID = "user id"
    }
}