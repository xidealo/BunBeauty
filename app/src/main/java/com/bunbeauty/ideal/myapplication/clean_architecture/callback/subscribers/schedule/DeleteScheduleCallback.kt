package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers.BaseDeleteCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.Schedule
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.ScheduleWithWorkingTime

interface DeleteScheduleCallback : BaseDeleteCallback<ScheduleWithWorkingTime>