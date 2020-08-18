package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers.BaseGetCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface UserCallback : BaseGetCallback<User>