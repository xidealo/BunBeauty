package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.logIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

interface RegistrationRepository {
    fun addUser(user:User)
}