package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.IProfilePresenter

interface IProfileInteractor {
    fun loadProfile(ownerId:String)
    fun updateProfile(ownerId:String)
    fun getCountOfRates() : String
    fun getUserId() : String
    fun getOwnerId() : String?
    fun checkSubscription() : Boolean
    fun initFCM()
    fun isFirstEnter():Boolean
    fun isUserOwner():Boolean
    fun getProfileOwner(profilePresenter: IProfilePresenter)
    //fun getProfileServiceList(profileCallback: IProfileCallback)

}