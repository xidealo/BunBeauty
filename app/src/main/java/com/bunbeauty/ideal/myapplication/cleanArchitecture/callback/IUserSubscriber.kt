package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

interface IUserSubscriber {
    fun returnUser(user: User)
    fun returnUsers(users: List<User>)
}