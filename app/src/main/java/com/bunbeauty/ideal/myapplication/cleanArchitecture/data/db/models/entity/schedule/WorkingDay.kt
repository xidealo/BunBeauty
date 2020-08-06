package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class WorkingDay(
    @PrimaryKey var id: String = "",
    var date: String = "", // format: dd-MM-yyyy
    var scheduleId: String = "",
    @Ignore
    var isSelected: Boolean = false
) {


    fun getDayOfMonth(): Int {
        return date.split(DATE_DELIMITER).first().toInt()
    }

    companion object {
        const val DATE = "date"

        const val DATE_DELIMITER = "-"
    }
}