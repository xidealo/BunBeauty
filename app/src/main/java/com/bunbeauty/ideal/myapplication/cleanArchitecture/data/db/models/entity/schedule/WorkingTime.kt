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

    companion object {
        const val WORKING_TIME = "working time"
        const val TIME = "time"
    }
}