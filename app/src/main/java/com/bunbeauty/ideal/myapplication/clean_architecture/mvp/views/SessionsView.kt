package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.Session
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingDay

interface SessionsView: MvpView {
    fun showDays(days: List<WorkingDay>)
    fun clearSessionsLayout()
    fun showTime(sessions: List<Session>)
    fun clearTime(time: String)
    fun selectTime(selectedTime: String)
    fun enableMakeAppointmentButton()
    fun disableMakeAppointmentButton()
    fun showMessage(message: String)
    fun goBack()
}