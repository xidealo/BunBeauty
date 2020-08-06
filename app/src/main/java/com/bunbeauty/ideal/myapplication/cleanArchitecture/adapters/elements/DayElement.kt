package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay
import kotlinx.android.synthetic.main.element_day.view.*

class DayElement(
    private val view: View,
    private val context: Context,
    private val day: WorkingDay
) {

    fun createElement() {
        view.dayBtn.text = day.getDayOfMonth().toString()
        if (day.isSelected) {
            view.dayBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
        }
    }
}