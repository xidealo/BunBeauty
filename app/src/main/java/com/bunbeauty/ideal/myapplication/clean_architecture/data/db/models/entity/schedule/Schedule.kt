package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity
data class Schedule(
    @PrimaryKey var id: String = "",
    var masterId: String = "",
    var gettingTime: Long = DateTime.now().millis
) {

    companion object {
        const val SCHEDULE = "schedule"
        const val GETTING_TIME = "getting time"
    }
}