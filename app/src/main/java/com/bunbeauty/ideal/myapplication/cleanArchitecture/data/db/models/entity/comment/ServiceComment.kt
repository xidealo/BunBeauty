package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class ServiceComment(
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    @ColumnInfo(index = true)
    var serviceId: String = "",
    var ownerId: String = "",
    var rating: Double = 0.0,
    var review: String = "",
    var time: Long = 0L
) : Serializable {
    companion object {
        const val SERVICE_COMMENT = "service comment"
    }

}