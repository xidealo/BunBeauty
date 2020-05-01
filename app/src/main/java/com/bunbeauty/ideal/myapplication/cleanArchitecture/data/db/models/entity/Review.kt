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
    var type: String = ""
) {

    companion object {
        const val REVIEWS = "reviews"
        const val REVIEW = "review"
        const val RATING = "rating"
        const val WORKING_TIME_ID = "working time id"
        const val TYPE = "type"
    }
}