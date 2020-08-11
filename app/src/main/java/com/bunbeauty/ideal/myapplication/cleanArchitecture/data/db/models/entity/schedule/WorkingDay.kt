package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity
data class WorkingDay(
    @PrimaryKey var id: String = "",
    var date: Long,
    var scheduleId: String = ""
) {
    @Ignore
    var isSelected: Boolean = false

    fun getDayOfMonth(): Int {
        return DateTime(date).dayOfMonth
    }

    companion object {
        const val DATE = "date"
    }
}