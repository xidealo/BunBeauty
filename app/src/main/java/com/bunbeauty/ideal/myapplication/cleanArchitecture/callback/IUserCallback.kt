package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IUserCallback {
    fun returnUser(user: User)
    fun returnUsers(users: List<User>)
}