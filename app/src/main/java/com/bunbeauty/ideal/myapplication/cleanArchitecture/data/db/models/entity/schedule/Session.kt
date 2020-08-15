package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.api.StringApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingTime.Companion.TIME_DELIMITER
import org.joda.time.DateTime

data class Session(
    val startTime: Long,
    val finishTime: Long
) {

    override fun toString(): String {
        val dateTime = DateTime(startTime)
        return dateTime.dayOfMonth.toString() + "." +
                StringApi.addFirstZero(dateTime.monthOfYear.toString(), 2) + " " +
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
    }
}