package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.sessions

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay

interface SessionsPresenterCallback {
    fun showDays(days: Set<WorkingDay>)
    fun clearTime(time: String)
    fun selectTime(selectedTime: String)
    fun disableMakeAppointmentButton()
    fun enableMakeAppointmentButton()
    fun showMadeAppointment()
    fun updateSchedule(order: Order)
}