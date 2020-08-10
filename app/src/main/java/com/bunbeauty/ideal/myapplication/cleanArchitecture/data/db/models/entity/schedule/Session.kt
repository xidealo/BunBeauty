package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

import org.joda.time.DateTime

data class Session(
    val startTime: Long,
    val finishTime: Long
) {

    fun getTime(): String {
        return DateTime(startTime).hourOfDay().toString() +
                WorkingTime.TIME_DELIMITER +
                DateTime(startTime).minuteOfHour().toString()
    }

    companion object {
        const val START_TIME = "start time"
        const val FINISH_TIME = "finish time"
    }
}