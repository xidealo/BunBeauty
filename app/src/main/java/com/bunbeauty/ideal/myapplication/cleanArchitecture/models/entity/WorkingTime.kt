package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WorkingTime(@PrimaryKey
                       val id:String = "",
                       val time: String ="",
                       val isBlocked: Boolean = false,
                       val dateId:String = ""){
    companion object {
        const val WORKING_TIME = "working time"
        const val TIME = "time"
    }
}