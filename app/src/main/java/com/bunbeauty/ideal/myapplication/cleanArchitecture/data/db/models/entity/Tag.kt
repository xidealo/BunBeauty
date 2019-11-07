package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tag(
        @PrimaryKey
        var id: String = "",
        var tag: String = "",
        var serviceId: String = "") {

    lateinit var userId: String
    companion object {
        const val TAGS = "tags"
        const val TAG = "tag"
    }
}