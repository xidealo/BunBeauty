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

        val MONTHS = mapOf(
            1 to "янв",
            2 to "фев",
            3 to "мар",
            4 to "апр",
            5 to "май",
            6 to "июнь",
            7 to "июль",
            8 to "авг",
            9 to "сен",
            10 to "окт",
            11 to "ноя",
            12 to "дек"
        )
    }
}