package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingDay
import kotlinx.android.synthetic.main.element_day.view.*

class DayElement(
    private val view: View,
    private val context: Context,
    private val day: WorkingDay
) {

    fun createElement() {
        view.dayBtn.text = day.dayOfMonth.toString()
        if (day.isSelected) {
            view.dayBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
        } else {
            view.dayBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    fun setClickListener(listener: View.OnClickListener) {
        view.dayBtn.setOnClickListener(listener)
    }
}