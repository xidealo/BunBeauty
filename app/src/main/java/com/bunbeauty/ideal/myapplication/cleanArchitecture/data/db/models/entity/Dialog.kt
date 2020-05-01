package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["ownerId"],
        onDelete = CASCADE
    )]
)
data class Dialog(
    @PrimaryKey
    var id: String = "",
    @ColumnInfo(index = true)
    var ownerId: String = "", // Owner
    var isChecked: Boolean = true, // Read or no
    @Embedded(prefix = "user_")
    var user: User = User(), //with who
    @Embedded(prefix = "message_")
    var lastMessage: Message = Message()
){
    companion object{
        const val DIALOGS = "dialogs"
        const val IS_CHECKED = "is checked"
        const val COMPANION_ID = "companion id"
        const val MESSAGE_ID = "message id"
    }
}