package com.bunbeauty.ideal.myapplication.clean_architecture.callback.sessions

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingDay

interface SessionsPresenterCallback {
    fun showDays(days: List<WorkingDay>)
    fun showNoAvailableSessions()
    fun clearTime(time: String)
    fun selectTime(selectedTime: String)
    fun disableMakeAppointmentButton()
    fun enableMakeAppointmentButton()
    fun showMadeAppointment()
    fun updateSchedule(order: Order)
    fun sendMessages(order: Order)
}