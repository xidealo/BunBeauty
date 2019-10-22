package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Photo(@PrimaryKey
            var id: String = "",
            var link: String = "",
            var ownerId: String = "") {

    lateinit var userId: String
    lateinit var serviceId: String
    companion object {
        const val PHOTOS = "photos"
        const val PHOTO_LINK = "photo link"
        const val OWNER_ID = "owner id"
    }

}
