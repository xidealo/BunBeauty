package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User.Companion.USERS
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.Schedule.Companion.SCHEDULE
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithDays.Companion.WORKING_DAYS
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay.Companion.DATE
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingTime.Companion.TIME
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingTime.Companion.WORKING_TIME
import com.google.firebase.database.*

class ScheduleFirebase {

    fun getByUserId(userId: String, getScheduleCallback: GetScheduleCallback): ScheduleWithDays {
        val scheduleReference = FirebaseDatabase.getInstance()
            .getReference(USERS)
            .child(userId)
            .child(SCHEDULE)
            .child(WORKING_DAYS)

        scheduleReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(workingDaysSnapshot: DataSnapshot) {
                val workingDays = getWorkingDaysFromSnapshot(workingDaysSnapshot)

                val schedule = ScheduleWithDays(Schedule(userId), workingDays.toMutableList())
                getScheduleCallback.returnGotSchedule(schedule)
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        return ScheduleWithDays()
    }

    private fun getWorkingDaysFromSnapshot(workingDaysSnapshot: DataSnapshot): List<WorkingDayWithTimes> {
        return workingDaysSnapshot.children.map { workingDaySnapshot ->
            val date = workingDaySnapshot.child(DATE).value as String
            val workingTime = getWorkingTimeFromSnapshot(workingDaySnapshot)

            WorkingDayWithTimes(WorkingDay(date = date), workingTime.toMutableList())
        }
    }

    private fun getWorkingTimeFromSnapshot(workingDaySnapshot: DataSnapshot): List<WorkingTime> {
        return workingDaySnapshot.child(WORKING_TIME).children.map { workingTimeSnapshot ->
            val time = workingTimeSnapshot.child(TIME).value as String
            WorkingTime(time = time)
        }
    }

    fun update(scheduleWithDays: ScheduleWithDays) {
        val scheduleReference = FirebaseDatabase.getInstance()
            .getReference(USERS)
            .child(scheduleWithDays.schedule.userId)
            .child(SCHEDULE)

        scheduleReference.removeValue { _, _ ->
            insert(scheduleWithDays)
        }
    }

    private fun insert(scheduleWithDays: ScheduleWithDays) {
        val daysReference = FirebaseDatabase.getInstance()
            .getReference(USERS)
            .child(scheduleWithDays.schedule.userId)
            .child(SCHEDULE)
            .child(WORKING_DAYS)

        for (workingDayWithTimes in scheduleWithDays.workingDays) {
            val dayId = getIdForNew(daysReference)
            val newDayReference = daysReference.child(dayId)
            newDayReference.setValue(buildWorkingDayMap(workingDayWithTimes.workingDay))

            for (workingTime in workingDayWithTimes.workingTimes) {
                val timeId = getIdForNew(newDayReference.child(WORKING_TIME))
                val newTimeReference = newDayReference.child(WORKING_TIME).child(timeId)
                newTimeReference.setValue(buildWorkingTimeMap(workingTime))
            }
        }
    }

    private fun buildWorkingDayMap(workingDay: WorkingDay): HashMap<String, Any> {
        val workingDayMap = HashMap<String, Any>()
        workingDayMap[DATE] = workingDay.date
        return workingDayMap
    }

    private fun buildWorkingTimeMap(workingTime: WorkingTime): HashMap<String, Any> {
        val workingTimeMap = HashMap<String, Any>()
        workingTimeMap[TIME] = workingTime.time
        return workingTimeMap
    }

    private fun getIdForNew(reference: DatabaseReference): String {
        return reference.push().key!!
    }
}