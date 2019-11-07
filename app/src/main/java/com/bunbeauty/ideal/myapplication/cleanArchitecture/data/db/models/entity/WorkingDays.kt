package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WorkingDays(@PrimaryKey
                       val id: String = "",
                       val date: String = "",
                       val serviceId: String = "") {
    companion object {
        const val WORKING_DAYS = "working days"
        const val DATE = "date"
    }
}



