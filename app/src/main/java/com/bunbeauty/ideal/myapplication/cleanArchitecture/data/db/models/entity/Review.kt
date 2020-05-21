package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Review(
    @PrimaryKey
    var id: String = "",
    var review: String = "",
    var rating: String = "",
    var workingTimeId: String = "",
    var type: String = "",
    var time: Long = 0L
) {

    companion object {
        const val REVIEWS = "reviews"
        const val REVIEW = "creation_comment"
        const val RATING = "rating"
        const val WORKING_TIME_ID = "working time id"
        const val TYPE = "type"
    }
}