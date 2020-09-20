package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.RefreshableAdapterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.SessionsPresenter
import kotlinx.android.synthetic.main.element_schedule_button.view.*

class DayElement(private val view: View, private val day: WorkingDay, context: Context) {

    init {
        val button = view.element_schedule_btn

        val margin = context.resources.getDimensionPixelSize(R.dimen.schedule_button_margin)
        val width =
            (context.resources.displayMetrics.widthPixels / DAY_COUNT_ON_SCREEN).toInt()
        val height = width
        button.layoutParams = LinearLayout.LayoutParams(width - 2 * margin, height).apply {
            setMargins(margin, 0, margin, 0)
        }
        button.text = day.dayOfMonth.toString() + "\n" + day.month

        if (day.isSelected) {
            button.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
        } else {
            button.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    fun setClickListener(
        dayList: List<WorkingDay>,
        sessionsPresenter: SessionsPresenter,
        refreshableAdapterCallback: RefreshableAdapterCallback
    ) {
        view.element_schedule_btn.setOnClickListener {
            val selectedDay =
                dayList.find { it.isSelected && (it.dayOfMonth != day.dayOfMonth || it.month != day.month) }
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

    companion object {
        const val DAY_COUNT_ON_SCREEN = 6.3
    }
}