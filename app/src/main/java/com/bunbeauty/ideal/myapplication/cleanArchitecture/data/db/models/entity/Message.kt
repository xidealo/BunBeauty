package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

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
    var message: String = "",
    var time: String = ""
) {
}