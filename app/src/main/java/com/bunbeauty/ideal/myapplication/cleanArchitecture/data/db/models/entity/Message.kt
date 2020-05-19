package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    foreignKeys = [ForeignKey(
        entity = Dialog::class,
        parentColumns = ["id"],
        childColumns = ["dialogId"],
        onDelete = CASCADE
    )]
)
data class Message(
    @PrimaryKey
    var id: String = "",
    @ColumnInfo(index = true)
    var dialogId: String = "",
    var userId: String = "",
    var isAlert: Boolean = false,
    var message: String = "",
    var orderId: String = "",
    var time: Long = 0L
) : Serializable {

    companion object {
        const val MESSAGES = "messages"
        const val MESSAGE = "message"
        const val IS_ALERT = "is alert"
        const val TIME = "time"
        const val ORDER_ID = "order id"
    }
}