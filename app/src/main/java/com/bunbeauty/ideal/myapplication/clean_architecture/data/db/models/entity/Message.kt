package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity

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
    var message: String = "",
    var orderId: String = "",
    var ownerId: String = "",
    var time: Long = 0L,
    var type: Int = 0
) : Serializable {

    companion object {
        const val MESSAGES = "messages"
        const val MESSAGE = "message"
        const val TYPE = "type"
        const val TEXT_MESSAGE_STATUS = 0
        const val USER_REVIEW_MESSAGE_STATUS = 1
        const val SERVICE_REVIEW_MESSAGE_STATUS = 2
        const val CANCEL_MESSAGE_STATUS = 3
        const val TIME = "time"
        const val ORDER_ID = "order id"
        const val OWNER_ID = "owner id"
    }

}