package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tags(
        @PrimaryKey
        var id: String = "",
        var tag: String = "",
        var serviceId: String
)
{
        companion object{
                const val TAGS = "tags"
        }
}