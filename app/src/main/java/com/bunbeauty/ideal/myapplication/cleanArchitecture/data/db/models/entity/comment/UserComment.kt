package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserComment(
    @PrimaryKey
    var id: String = "",
    @ColumnInfo(index = true)
    var userId: String = "",
    var ownerId: String = "",
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