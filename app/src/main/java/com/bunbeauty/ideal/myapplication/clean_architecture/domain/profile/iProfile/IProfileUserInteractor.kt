package com.bunbeauty.ideal.myapplication.clean_architecture.domain.profile.iProfile

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IProfileUserInteractor {

    var owner: User?

    fun initFCM()
    fun getProfileOwner(intent: Intent, profilePresenterCallback: ProfilePresenterCallback)
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