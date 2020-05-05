package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IProfileUserInteractor {
    fun updateUser(user: User,  profilePresenterCallback: ProfilePresenterCallback)
    fun getCountOfRates(): String
    fun getCurrentUser(): User
    fun checkSubscription(): Boolean
    fun initFCM()
    fun checkIconClick(profilePresenterCallback: ProfilePresenterCallback)
    fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback)
}