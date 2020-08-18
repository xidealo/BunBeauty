package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule

import androidx.room.Embedded
import androidx.room.Relation

data class WorkingDayWithTimes(
    @Embedded
    val workingDay: WorkingDay,

    @Relation(parentColumn = "id", entityColumn = "workingDayId")
    val workingTimes: MutableList<WorkingTime> = ArrayList()
) {

    fun isAvailable(serviceDuration: Float): Boolean {
        if (isEmpty()) {
            return false
        }

        var timeInRaw = 0
        var previousTime: WorkingTime? = null
        for (time in workingTimes) {
            if (timeInRaw == 0) {
                timeInRaw = 1
            } else {
                if (time.isNext(previousTime)) {
                    timeInRaw++
                } else {
                    timeInRaw = 1
                }
            }
            previousTime = time

            if ((serviceDuration / 0.5f).toInt() == timeInRaw) {
                return true
            }
        }

        return false
    }

    fun getSessions(serviceDuration: Float): List<Session> {
        if (isEmpty()) {
            return listOf()
        }

        val sessionList = ArrayList<Session>()
        var timeInRaw = 0
        lateinit var previousTime: WorkingTime
        for (time in workingTimes) {
            if (timeInRaw == 0) {
                timeInRaw = 1
            } else {
                if (time.isNext(previousTime)) {
                    timeInRaw++
                } else {
                    timeInRaw = 1
                }
            }
            previousTime = time

            if ((serviceDuration / 0.5f).toInt() == timeInRaw) {
                val session = Session(time.getTimeBefore(timeInRaw), time.getFinishTime())
                sessionList.add(session)
                timeInRaw = 0
            }
        }

        return sessionList
    }

    fun isEmpty(): Boolean {
        return workingTimes.isEmpty()
    }
}