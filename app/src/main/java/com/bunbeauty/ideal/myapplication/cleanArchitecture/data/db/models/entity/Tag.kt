package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    foreignKeys = [ForeignKey(
        entity = Service::class,
        parentColumns = ["id"],
        childColumns = ["serviceId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Tag(
    @PrimaryKey
    var id: String = "",
    var tag: String = "",
    var serviceId: String = ""
) : Serializable {

    lateinit var userId: String

    companion object {
        const val TAGS = "tags"
        const val TAG = "tag"
    }
}