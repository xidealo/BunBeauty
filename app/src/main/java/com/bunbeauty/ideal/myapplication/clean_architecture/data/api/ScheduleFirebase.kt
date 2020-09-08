package com.bunbeauty.ideal.myapplication.clean_architecture.data.api

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.UpdateScheduleAddOrderCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.UpdateScheduleRemoveOrderCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.Schedule
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.Schedule.Companion.SCHEDULE
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.ScheduleWithWorkingTime
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingTime
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingTime.Companion.CLIENT_ID
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingTime.Companion.ORDER_ID
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingTime.Companion.TIME
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.BaseRepository
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScheduleFirebase : BaseRepository() {

    fun getByMasterId(masterId: String, getScheduleCallback: GetScheduleCallback) {
        val workingTimeReference = FirebaseDatabase.getInstance()
            .getReference(SCHEDULE)
            .child(masterId)

        workingTimeReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(workingTimeSnapshot: DataSnapshot) {
                val workingTimeList = getWorkingTimeFromSnapshot(workingTimeSnapshot)


                CoroutineScope(Dispatchers.Main).launch {
                    getScheduleCallback.returnGottenObject(
                        ScheduleWithWorkingTime(
                            schedule = Schedule(masterId = masterId),
                            workingTimeList = ArrayList(workingTimeList)
                        )
                    )
                }
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

    fun updateScheduleAddOrder(
        schedule: ScheduleWithWorkingTime,
        updateScheduleAddOrderCallback: UpdateScheduleAddOrderCallback
    ) {
        val scheduleReference = FirebaseDatabase.getInstance()
            .getReference(SCHEDULE)
            .child(schedule.schedule.masterId)
        for (workingTime in schedule.workingTimeList) {
            val workingTimeReference = scheduleReference.child(workingTime.id)
            workingTimeReference.child(ORDER_ID).setValue(workingTime.orderId)
            workingTimeReference.child(CLIENT_ID).setValue(workingTime.clientId)
        }
        updateScheduleAddOrderCallback.returnUpdatedScheduleAddOrderCallback(schedule)
    }

    fun updateScheduleRemoveOrder(
        order: Order,
        updateScheduleRemoveOrderCallback: UpdateScheduleRemoveOrderCallback
    ) {
        val scheduleReference = FirebaseDatabase.getInstance()
            .getReference(SCHEDULE)
            .child(order.masterId)
        val scheduleQuery = scheduleReference
            .orderByChild(ORDER_ID)
            .equalTo(order.id)

        scheduleQuery.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount > 0) {
                    val workingTimeList = getWorkingTimeFromSnapshot(snapshot)
                    for (workingTime in workingTimeList) {
                        scheduleReference.child(workingTime.id).child(ORDER_ID).setValue("")
                        scheduleReference.child(workingTime.id).child(CLIENT_ID).setValue("")
                    }

                    updateScheduleRemoveOrderCallback.returnUpdatedScheduleRemoveOrderCallback(
                        ScheduleWithWorkingTime(workingTimeList = ArrayList(workingTimeList))
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun insert(scheduleWithWorkingTime: ScheduleWithWorkingTime) {
        val workingTimeReference = FirebaseDatabase.getInstance()
            .getReference(SCHEDULE)
            .child(scheduleWithWorkingTime.schedule.masterId)

        for (workingTime in scheduleWithWorkingTime.workingTimeList) {
            val workingTimeId = getIdForNew(workingTimeReference)
            val newTimeReference = workingTimeReference.child(workingTimeId)
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

    fun delete(scheduleWithWorkingTime: ScheduleWithWorkingTime) {
        val workingTimeReference = FirebaseDatabase.getInstance()
            .getReference(SCHEDULE)
            .child(scheduleWithWorkingTime.schedule.masterId)

        for (workingTime in scheduleWithWorkingTime.workingTimeList) {
            val newTimeReference = workingTimeReference.child(workingTime.id)
            newTimeReference.removeValue()
        }
    }

    private fun getIdForNew(reference: DatabaseReference): String {
        return reference.push().key!!
    }
}