package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import android.app.Activity
import android.content.Intent

interface IProfileInteractor {
    fun loadProfile(ownerId:String)
    fun updateProfile(ownerId:String)
    fun getCountOfRates() : String
    fun getUserId() : String
    fun getOwnerId(intent: Intent) : String?
    fun checkSubscription() : Boolean
    fun goToAddService(activity: Activity)
    fun goToSubscribers(activity: Activity, status:String)
    fun goToUserComments(activity: Activity, status:String, ownerId:String)
    fun initFCM(ownerId: String)
    fun isFirstEnter(intent: Intent):Boolean
    fun userIsOwner(intent: Intent):Boolean

}