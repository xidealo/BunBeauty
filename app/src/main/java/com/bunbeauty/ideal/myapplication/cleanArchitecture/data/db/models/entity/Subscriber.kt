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
data class Subscriber(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val date: String = "",
    val workerId: String = ""
) {
    companion object {
        const val SUBSCRIBERS = "subscribers"
        const val USER_ID = "user id"
        const val WORKER_ID = "worker id"
    }
}