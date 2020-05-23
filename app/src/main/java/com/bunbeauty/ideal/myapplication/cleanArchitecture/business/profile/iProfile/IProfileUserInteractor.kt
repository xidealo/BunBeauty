package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IProfileUserInteractor {
    fun getCacheOwner(): User
    fun getCacheUser(): User
    fun updateUser(user: User, profilePresenterCallback: ProfilePresenterCallback)
    fun getCountOfRates(): String
    fun isMyProfile(ownerId: String, myId: String): Boolean
    fun checkSubscription(): Boolean
    fun initFCM()
    fun checkIconClick(profilePresenterCallback: ProfilePresenterCallback)
    fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback)
    fun updateBottomPanel(profilePresenterCallback: ProfilePresenterCallback)
}