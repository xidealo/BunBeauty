package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Schedule(
    @PrimaryKey var id: String = "",
    var userId: String = ""
) {

    companion object {
        const val SCHEDULE = "schedule"
    }
}