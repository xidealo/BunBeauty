package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IProfileInteractor {
    fun loadProfile(ownerId: String)
    fun updateProfile(ownerId: String)
    fun getCountOfRates(): String
    fun getCurrentUser(): User
    fun getUserId(): String
    fun checkSubscription(): Boolean
    fun initFCM()
    fun checkIconClick(profilePresenterCallback: ProfilePresenterCallback)
    fun isFirstEnter(): Boolean
    fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback)
    fun getServicesLink(): MutableList<Service>
    //fun getProfileServiceList(profileCallback: IProfileCallback)
}