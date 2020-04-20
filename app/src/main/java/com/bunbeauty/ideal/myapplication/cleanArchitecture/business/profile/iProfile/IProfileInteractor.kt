package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback

interface IProfileInteractor {
    fun loadProfile(ownerId:String)
    fun updateProfile(ownerId:String)
    fun getCountOfRates() : String
    fun getUserId() : String
    fun checkSubscription() : Boolean
    fun initFCM()
    fun isFirstEnter():Boolean
    fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback)
    //fun getProfileServiceList(profileCallback: IProfileCallback)

}