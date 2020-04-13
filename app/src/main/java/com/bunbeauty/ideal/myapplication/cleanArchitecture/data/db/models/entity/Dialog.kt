package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Dialog(
        @PrimaryKey
        var id: String = "",
        var userName: String = "",
        var time : String = ""
        )