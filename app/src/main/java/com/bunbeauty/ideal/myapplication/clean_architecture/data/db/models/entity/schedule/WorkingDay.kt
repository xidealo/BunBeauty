package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule

data class WorkingDay(
    var dayOfMonth: Int,
    var isSelected: Boolean = false
) {}