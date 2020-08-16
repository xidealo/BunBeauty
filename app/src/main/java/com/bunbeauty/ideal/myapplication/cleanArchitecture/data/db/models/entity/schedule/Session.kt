package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingTime.Companion.TIME_DELIMITER
import org.joda.time.DateTime

data class Session(
    val startTime: Long,
    val finishTime: Long
) {

    override fun toString(): String {
        val dateTime = DateTime(startTime)
        return dateTime.dayOfMonth.toString() + " " +
                MONTHS[dateTime.monthOfYear] + ", " +
                WEEK_DAYS[dateTime.dayOfWeek] + " " +
                getStringStartTime() + " - " +
                getStringFinishTime()
    }

    fun getStringStartTime(): String {
        return getStringTime(startTime)
    }

    fun getStringFinishTime(): String {
        return getStringTime(finishTime)
    }

    fun getStringTime(time: Long): String {
        var stringTime = DateTime(time).hourOfDay.toString() + TIME_DELIMITER
        stringTime += if (DateTime(time).minuteOfHour == 0) {
            "00"
        } else {
            DateTime(time).minuteOfHour.toString()
        }

        return stringTime
    }

    companion object {
        const val START_TIME = "start time"
        const val FINISH_TIME = "finish time"

        val MONTHS: Map<Int, String> = mapOf(
            1 to "янв",
            2 to "фев",
            3 to "мар",
            4 to "апр",
            5 to "мая",
            6 to "июн",
            7 to "июл",
            8 to "авг",
            9 to "сен",
            10 to "окт",
            11 to "ноя",
            12 to "дек"
        )
        val WEEK_DAYS: Map<Int, String> = mapOf(
            1 to "пн",
            2 to "вт",
            3 to "ср",
            4 to "чт",
            5 to "пт",
            6 to "сб",
            7 to "вс"
        )
    }
}