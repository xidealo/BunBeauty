package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo(
    @PrimaryKey
    var id: String = "",
    var link: String = "",
    var userId: String = "",
    var serviceId: String = ""
) {
    companion object {
        const val PHOTOS = "photos"
        const val LINK = "link"
        const val SERVICE_ID = "service id"
    }

}
