package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

import androidx.room.Embedded
import androidx.room.Relation

data class WorkingDayWithTimes(
    @Embedded
    val workingDay: WorkingDay,

    @Relation(parentColumn = "id", entityColumn = "workingDayId")
    val workingTimes: MutableList<WorkingTime> = ArrayList()
) {
    fun getDayOfMonth(): Int {
        return workingDay.getDayOfMonth()
    }

    fun containsWorkingTime(workingTime: WorkingTime): Boolean {
        return workingTimes.contains(workingTime)
    }

    fun addWorkingTime(time: String) {
        val workingTime = WorkingTime(time = time)
        if (containsWorkingTime(workingTime)) {
            return
        }
        workingTimes.add(workingTime)
    }

    fun removeTime(time: String) {
        workingTimes.remove(workingTimes.find { it.time == time })
    }

    fun isEmpty(): Boolean {
        return workingTimes.isEmpty()
    }
}