package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order

@Entity
data class WorkingTime(
    @PrimaryKey var id: String = "",
    var time: String = "",
    @Embedded(prefix = "order_") val order: Order = Order(),
    var workingDayId: String = ""
) {

    fun getIndex(): Int {
        val timeParts = time.split(TIME_DELIMITER)
        var index = (timeParts.first().toIntOrNull() ?: 0) * 2
        if (timeParts[1] == "30") {
            index += 1
        }

        return index
    }

    fun isNext(previousTime: WorkingTime): Boolean {
        val index = getIndex()
        val previousIndex = previousTime.getIndex()

        return (index - previousIndex) == 1
    }

    fun getTimeBefore(beforeCount: Int): String {
        return getTimeByIndex(getIndex() - beforeCount + 1)
    }

    fun getFinishTime(): String {
        return getTimeByIndex(getIndex() + 1)
    }

    companion object {
        const val WORKING_TIME = "working time"
        const val TIME = "time"
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