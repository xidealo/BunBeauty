package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class WorkingDay(
    @PrimaryKey val id: Long = 0,
    val date: String = "", // format: dd-MM-yyyy
    val userId: Long = 0,
    val scheduleId: Long = 0
) {
    fun getDayOfMonth(): Int {
        return date.split(DATE_DELIMITER).first().toInt()
    }

    companion object {
        const val DATE_DELIMITER = "-"
    }
}