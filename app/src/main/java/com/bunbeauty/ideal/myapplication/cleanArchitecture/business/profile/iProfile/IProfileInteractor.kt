package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface IProfileInteractor {
    fun loadProfile(ownerId: String)
    fun updateProfile(ownerId: String)
    fun getCountOfRates(): String
    fun getUserId(): String
    fun checkSubscription(): Boolean
    fun initFCM()
    fun isFirstEnter(): Boolean
    fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback)
    fun getServicesLink(): MutableList<Service>
    //fun getProfileServiceList(profileCallback: IProfileCallback)
}