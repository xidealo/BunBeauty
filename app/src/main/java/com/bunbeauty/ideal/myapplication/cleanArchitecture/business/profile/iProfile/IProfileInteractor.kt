package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IProfileCallback

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
    fun getProfileOwner(profileCallback: IProfileCallback)
    //fun getProfileServiceList(profileCallback: IProfileCallback)

}