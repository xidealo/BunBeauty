package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.logIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.entity.User

interface RegistrationRepository {
    fun addUser(user: User)
}