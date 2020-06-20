package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IProfileUserInteractor {
    fun getCacheOwner(): User
    fun getCacheUser(): User
    fun getCountOfRates(): String
    fun updateUser(user: User, profilePresenterCallback: ProfilePresenterCallback)
    fun isMyProfile(ownerId: String, myId: String): Boolean
    fun initFCM()
    fun checkIconClick(profilePresenterCallback: ProfilePresenterCallback)
    fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback)
    fun updateBottomPanel(profilePresenterCallback: ProfilePresenterCallback)
    fun updateUserFromEditService(user: User, profilePresenterCallback: ProfilePresenterCallback)
    fun updateCountOfSubscribers(
        user: User,
        subscriber: Int,
        profilePresenterCallback: ProfilePresenterCallback
    )
}