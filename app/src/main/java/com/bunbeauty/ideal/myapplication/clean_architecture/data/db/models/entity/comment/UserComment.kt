package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import java.io.Serializable

@Entity
data class UserComment(
    @PrimaryKey
    var id: String = "",
    @ColumnInfo(index = true)
    var userId: String = "",
    var ownerId: String = "",
    @Ignore
    var user: User = User(), // owner
    var rating: Float = 0f,
    var review: String = "",
    var date: Long = 0L
) : Serializable {
    companion object {
        const val COMMENTS = "comments"
        const val USER_COMMENT = "user comment"
        const val RATING = "rating"
        const val REVIEW = "comment"
        const val TIME = "time"
        const val OWNER_ID = "owner id"
    }
}