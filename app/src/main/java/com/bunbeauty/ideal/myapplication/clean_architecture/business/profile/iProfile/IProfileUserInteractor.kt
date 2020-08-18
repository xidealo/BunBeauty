package com.bunbeauty.ideal.myapplication.clean_architecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IProfileUserInteractor {
    fun getCacheOwner(): User
    fun getCacheUser(): User
    fun isMyProfile(ownerId: String, myId: String): Boolean
    fun initFCM()
    fun updateMyProfileServices(
        profilePresenterCallback: ProfilePresenterCallback
    )

    fun checkIconClick(profilePresenterCallback: ProfilePresenterCallback)
    fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback)
    fun updateBottomPanel(profilePresenterCallback: ProfilePresenterCallback)
    fun updateUserFromEditUser(user: User, profilePresenterCallback: ProfilePresenterCallback)
    fun updateCountOfSubscribers(
        user: User,
        subscriber: Int,
        profilePresenterCallback: ProfilePresenterCallback
    )
}