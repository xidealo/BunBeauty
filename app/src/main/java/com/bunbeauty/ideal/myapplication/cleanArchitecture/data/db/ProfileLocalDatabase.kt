package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.logIn.ProfileRepository

class ProfileLocalDatabase : ProfileRepository {

    fun addUserInLocalStorage(user: User) {
        //add user
    }

    fun addServiceInLocalStorage(service: Service) {
        //add user
    }
    override fun updateProfileData() {
        // update user data
    }

    override fun getCountOfRates(): Long {
        //getCountOfRates
        return 0
    }

}