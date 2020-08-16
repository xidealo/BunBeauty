package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.baseSubscribers.BaseGetCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.Schedule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithWorkingTime

interface GetScheduleCallback: BaseGetCallback<ScheduleWithWorkingTime>