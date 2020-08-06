package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.DayElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.SessionsPresenter

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
        holder.bind(dayList[i])
        holder.itemView.setOnClickListener {
            dayList.find { it.isSelected && dayList.indexOf(it) != i }?.isSelected = false
            dayList[i].isSelected = !dayList[i].isSelected

            if (dayList[i].isSelected) {
                sessionsPresenter.getSessions(dayList[i])
            } else {
                sessionsPresenter.clearSessions()
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

    inner class DayViewHolder(private val view: View, private val context: Context) :
        RecyclerView.ViewHolder(view) {

        fun bind(day: WorkingDay): DayElement {
            val dayElement = DayElement(view, context, day)
            dayElement.createElement()

            return dayElement
        }
    }
}