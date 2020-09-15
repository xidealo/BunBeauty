package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.DayElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.SessionsPresenter

class DayAdapter(
    private val dayList: List<WorkingDay>,
    private val sessionsPresenter: SessionsPresenter
) : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayAdapter.DayViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.element_day, parent, false)
        return DayViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: DayViewHolder, i: Int) {
        holder.bind(dayList[i], i)
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

    inner class DayViewHolder(private val view: View, private val context: Context) :
        RecyclerView.ViewHolder(view) {

        fun bind(day: WorkingDay, i: Int): DayElement {
            val dayElement = DayElement(view, context, day)
            dayElement.setClickListener(View.OnClickListener {
                dayList.find { it.isSelected && dayList.indexOf(it) != i }?.isSelected = false
                day.isSelected = !day.isSelected

                if (day.isSelected) {
                    sessionsPresenter.getSessions(day)
                } else {
                    sessionsPresenter.clearSessions()
                }
                notifyDataSetChanged()
            })

            return dayElement
        }
    }
}