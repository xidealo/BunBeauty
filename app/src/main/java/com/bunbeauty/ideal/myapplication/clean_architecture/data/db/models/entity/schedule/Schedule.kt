package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Schedule(
    @PrimaryKey var id: String = "",
    var masterId: String = ""
) {

    companion object {
        const val SCHEDULE = "schedule"
    }
}