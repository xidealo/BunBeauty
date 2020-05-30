package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

@Entity
data class UserComment(
    @PrimaryKey
    var id: String = "",
    @ColumnInfo(index = true)
    var userId: String = "",
    var ownerId: String = "",
    @Ignore
    var user: User = User(), // owner
    var rating: Double = 0.0,
    var review: String = "",
    var time: Long = 0L
) {
    companion object {
        const val COMMENTS = "comments"
        const val COMMENT = "comment"
        const val RATING = "rating"
        const val REVIEW = "creation_comment"
        const val TIME = "time"
        const val OWNER_ID = "owner id"
    }
}