package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Subscription(
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    var date: String = "",
    var subscriptionId: String = "", // on who
    @Embedded(prefix = "user")
    var subscriptionUser: User = User()
) {
    companion object {
        const val SUBSCRIPTIONS = "subscriptions"
        const val SUBSCRIPTION_ID = "subscription id"
        const val DATE = "date"
        const val USER_ID = "user id"
    }
}