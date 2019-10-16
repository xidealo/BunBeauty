package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

interface IProfileInteractor {
    fun loadProfile(ownerId:String)
    fun updateProfile(ownerId:String)
    fun getCountOfRates() : String
    fun getUserId() : String
    fun getOwnerId(intent: Intent) : String?
    fun checkSubscription() : Boolean
    fun initFCM()
    fun isFirstEnter(intent: Intent):Boolean
    fun userIsOwner(intent: Intent):Boolean
    fun getProfileOwner(intent: Intent): User
    fun getProfileServiceList(intent: Intent): List<Service>

}