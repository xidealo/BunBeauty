package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ServiceComment(
    @PrimaryKey
    var id: String = "",
    @ColumnInfo(index = true)
    var userId: String = "",
    var serviceId: String = "",
    var ownerId: String = "",
    var rating: Double = 0.0,
    var review: String = "",
    var time: Long = 0L
) {


}