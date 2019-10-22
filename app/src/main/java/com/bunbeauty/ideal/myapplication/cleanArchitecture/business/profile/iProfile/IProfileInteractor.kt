package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

interface IProfileInteractor {
    fun loadProfile(ownerId:String)
    fun updateProfile(ownerId:String)
    fun getCountOfRates() : String
    fun getUserId() : String
    fun getOwnerId() : String?
    fun checkSubscription() : Boolean
    fun initFCM()
    fun isFirstEnter():Boolean
    fun userIsOwner():Boolean
    fun getProfileOwner(userSubscriber: IUserSubscriber)
    fun getProfileServiceList()

}