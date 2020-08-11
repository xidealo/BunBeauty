package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

import org.joda.time.DateTime

data class Session(
    val startTime: Long,
    val finishTime: Long
) {

    constructor(stringTime: String): this(0, 0)

    //private fun getStartTimeB

    fun getStringStartTime(): String {
        var stringTime = DateTime(startTime).hourOfDay.toString() + WorkingTime.TIME_DELIMITER
        stringTime += if (DateTime(startTime).minuteOfHour == 0) {
            "00"
        } else {
            DateTime(startTime).minuteOfHour.toString()
        }

        return stringTime
    }

    companion object {
        const val START_TIME = "start time"
        const val FINISH_TIME = "finish time"
    }
}