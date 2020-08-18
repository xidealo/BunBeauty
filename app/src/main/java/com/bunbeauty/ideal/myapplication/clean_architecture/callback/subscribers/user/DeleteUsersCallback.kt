package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers.BaseDeleteCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface DeleteUsersCallback : BaseDeleteCallback<User>