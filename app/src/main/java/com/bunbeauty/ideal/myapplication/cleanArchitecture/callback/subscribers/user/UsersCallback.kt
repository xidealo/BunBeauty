package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface UsersCallback {
    fun returnUsers(users: List<User>)
}