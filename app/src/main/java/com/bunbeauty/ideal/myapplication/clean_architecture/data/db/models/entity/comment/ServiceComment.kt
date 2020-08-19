package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import java.io.Serializable

@Entity
data class ServiceComment(
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    @ColumnInfo(index = true)
    var serviceId: String = "",
    var serviceName: String = "",
    @Ignore
    var user: User = User(), // owner
    var ownerId: String = "",
    var rating: Float = 0F,
    var review: String = "",
    var date: Long = 0L
) : Serializable {
    companion object {
        const val SERVICE_COMMENT = "service comment"
        const val SERVICE_COMMENTS = "service comments"
        const val RATING = "rating"
        const val REVIEW = "comment"
        const val TIME = "time"
        const val OWNER_ID = "owner id"
    }
}