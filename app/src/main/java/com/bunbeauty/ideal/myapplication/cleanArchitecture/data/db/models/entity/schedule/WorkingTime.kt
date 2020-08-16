package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity
data class WorkingTime(
    @PrimaryKey var id: String = "",
    var time: Long,
    var orderId: String = "",
    var clientId: String = "",
    var scheduleId: String = ""
) {

    fun getDayOfMonth(): Int {
        return DateTime(time).dayOfMonth
    }

    fun getHour(): Int {
        return DateTime(time).hourOfDay
    }

    fun getMinutes(): Int {
        return DateTime(time).minuteOfHour
    }

    fun getIndex(): Int {
        return DateTime(time).hourOfDay * 2 + DateTime(time).minuteOfHour / 30
    }

    fun isNext(previousTime: WorkingTime?): Boolean {
        if (previousTime == null) {
            return false
        }

        val index = getIndex()
        val previousIndex = previousTime.getIndex()

        return (index - previousIndex) == 1
    }

    fun getTimeBefore(beforeCount: Int): Long {
        return DateTime(time).minusMinutes(30 * (beforeCount - 1)).millis
    }

    fun getFinishTime(): Long {
        return DateTime(time).plusMinutes(30).millis
    }

    fun getStringTime(): String {
        var stringTime = DateTime(time).hourOfDay.toString() + TIME_DELIMITER
        stringTime += if (DateTime(time).minuteOfHour == 0) {
            "00"
        } else {
            DateTime(time).minuteOfHour.toString()
        }

        return stringTime
    }

    companion object {
        const val WORKING_TIME = "working time"
        const val TIME = "time"
        const val ORDER_ID = "order id"
        const val CLIENT_ID = "client id"
        const val TIME_DELIMITER = ":"

        fun getTimeByIndex(index: Int): String {
            val hours = index / 2
            val minutes = (index % 2) * 30
            val minutesString = if (minutes == 0) {
                "00"
            } else {
                minutes.toString()
            }

            return "$hours$TIME_DELIMITER$minutesString"
        }
    }
}