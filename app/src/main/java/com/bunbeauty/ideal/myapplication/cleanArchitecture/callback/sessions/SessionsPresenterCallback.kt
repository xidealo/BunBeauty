package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.sessions

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay

interface SessionsPresenterCallback {
    fun showDays(days: List<WorkingDay>)
}