package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

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

    fun getDateForComparison(): String {
        return arrayListOf(
            date.split(DATE_DELIMITER)[2],
            addFirstZero(date.split(DATE_DELIMITER)[1].toInt()),
            addFirstZero(getDayOfMonth())
        ).joinToString(DATE_DELIMITER)
    }

    fun addFirstZero(number: Int): String {
        return if (number > 10) {
            number.toString()
        } else {
            "0$number"
        }
    }

    companion object {
        const val DATE = "date"

        const val DATE_DELIMITER = "-"
    }
}