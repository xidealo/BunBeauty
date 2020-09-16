package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.RefreshableAdapterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.SessionsPresenter
import kotlinx.android.synthetic.main.element_day.view.*

class DayElement(private val view: View, private val day: WorkingDay, context: Context) {

    init {
        view.element_day_btn.text = day.dayOfMonth.toString() + "\n" + day.month
        if (day.isSelected) {
            view.element_day_btn.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
        } else {
            view.element_day_btn.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    fun setClickListener(
        dayList: List<WorkingDay>,
        sessionsPresenter: SessionsPresenter,
        refreshableAdapterCallback: RefreshableAdapterCallback
    ) {
        view.element_day_btn.setOnClickListener {
            val selectedDay =
                dayList.find { it.isSelected && it.dayOfMonth != day.dayOfMonth && it.month != day.month }
            selectedDay?.isSelected = false

            day.isSelected = !day.isSelected
            if (day.isSelected) {
                sessionsPresenter.getSessions(day)
            } else {
                sessionsPresenter.clearSessions()
            }

            refreshableAdapterCallback.refresh()
        }
    }

}