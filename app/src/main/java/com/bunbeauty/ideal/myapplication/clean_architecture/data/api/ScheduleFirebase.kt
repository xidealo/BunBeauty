package com.bunbeauty.ideal.myapplication.clean_architecture.data.api

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.UpdateScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.Schedule
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.Schedule.Companion.SCHEDULE
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.ScheduleWithWorkingTime
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingTime
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingTime.Companion.CLIENT_ID
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingTime.Companion.ORDER_ID
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingTime.Companion.TIME
import com.google.firebase.database.*

class ScheduleFirebase {

    fun getByMasterId(masterId: String, getScheduleCallback: GetScheduleCallback) {
        val workingTimeReference = FirebaseDatabase.getInstance()
            .getReference(SCHEDULE)
            .child(masterId)

        workingTimeReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(workingTimeSnapshot: DataSnapshot) {
                val workingTimeList = getWorkingTimeFromSnapshot(workingTimeSnapshot)

                getScheduleCallback.returnGottenObject(
                    ScheduleWithWorkingTime(
                        schedule = Schedule(masterId = masterId),
                        workingTimeList = ArrayList(workingTimeList)
                    )
                )
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun getWorkingTimeFromSnapshot(workingTimeSnapshot: DataSnapshot): List<WorkingTime> {
        return workingTimeSnapshot.children.map { snapshot ->
            val time = snapshot.child(TIME).value as Long
            val orderId = snapshot.child(ORDER_ID).value as? String ?: ""
            val clientId = snapshot.child(CLIENT_ID).value as? String ?: ""

            WorkingTime(
                id = snapshot.key!!,
                time = time,
                orderId = orderId,
                clientId = clientId
            )
        }
    }


    fun updateScheduleRemoveOrders(
        order: Order,
        updateScheduleCallback: UpdateScheduleCallback
    ) {
        val scheduleQuery = FirebaseDatabase.getInstance()
            .getReference(SCHEDULE)
            .child(order.masterId)
            .orderByChild(ORDER_ID).equalTo(order.id)
        scheduleQuery.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount > 0) {
                    val workingTimes = getWorkingTimeFromSnapshot(snapshot)
                    val clearWorkingTimes = mutableListOf<WorkingTime>()
                    for (time in workingTimes) {
                        time.orderId = ""
                        time.clientId = ""
                        clearWorkingTimes.add(time)
                    }
                    val schedule = ScheduleWithWorkingTime(
                        Schedule(masterId = order.masterId),
                        clearWorkingTimes
                    )
                    update(schedule)
                    updateScheduleCallback.returnUpdatedCallback(schedule)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }


    fun update(scheduleWithWorkingTime: ScheduleWithWorkingTime) {
        val scheduleReference = FirebaseDatabase.getInstance()
            .getReference(SCHEDULE)
            .child(scheduleWithWorkingTime.schedule.masterId)

        scheduleReference.removeValue { _, _ ->
            insert(scheduleWithWorkingTime)
        }
    }

    private fun insert(scheduleWithWorkingTime: ScheduleWithWorkingTime) {
        val workingTimeReference = FirebaseDatabase.getInstance()
            .getReference(SCHEDULE)
            .child(scheduleWithWorkingTime.schedule.masterId)

        for (workingTime in scheduleWithWorkingTime.workingTimeList) {
            val timeId = getIdForNew(workingTimeReference)
            val newTimeReference = workingTimeReference.child(timeId)
            newTimeReference.setValue(buildWorkingTimeMap(workingTime))
        }
    }

    private fun buildWorkingTimeMap(workingTime: WorkingTime): HashMap<String, Any> {
        val workingTimeMap = HashMap<String, Any>()
        workingTimeMap[TIME] = workingTime.time
        workingTimeMap[ORDER_ID] = workingTime.orderId
        workingTimeMap[CLIENT_ID] = workingTime.clientId
        return workingTimeMap
    }

    private fun getIdForNew(reference: DatabaseReference): String {
        return reference.push().key!!
    }
}