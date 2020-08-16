package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

data class WorkingDay(
    var dayOfMonth: Int,
    var isSelected: Boolean = false
) {}