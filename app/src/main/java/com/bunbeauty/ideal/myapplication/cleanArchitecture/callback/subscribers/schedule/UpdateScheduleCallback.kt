package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.baseSubscribers.BaseUpdateCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithWorkingTime

interface UpdateScheduleCallback: BaseUpdateCallback<ScheduleWithWorkingTime> {}