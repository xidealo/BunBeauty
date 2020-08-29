package com.bunbeauty.ideal.myapplication.clean_architecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IProfileUserInteractor {

    var owner: User?

    fun initFCM()
    fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback)
    fun whoseProfile(user: User, profilePresenterCallback: ProfilePresenterCallback)
    fun isMyProfile(ownerId: String, myId: String): Boolean

    fun checkProfileToUpdateServices(profilePresenterCallback: ProfilePresenterCallback)
    fun checkProfileToUpdateOrders(profilePresenterCallback: ProfilePresenterCallback)
    fun updateUserFromEditUser(user: User, profilePresenterCallback: ProfilePresenterCallback)
    fun updateCountOfSubscribers(
        user: User,
        subscriber: Int,
        profilePresenterCallback: ProfilePresenterCallback
    )
}