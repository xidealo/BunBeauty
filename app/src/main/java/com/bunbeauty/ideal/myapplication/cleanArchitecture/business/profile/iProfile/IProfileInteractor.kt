package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import android.content.Intent

interface IProfileInteractor {
    fun loadProfile(ownerId:String)
    fun updateProfile(ownerId:String)
    fun getCountOfRates() : String
    fun getUserId() : String
    fun checkSubscription() : Boolean
    fun goToAddService(intent: Intent)
    fun goToSubscribers(intent: Intent, status:String)
    fun goToUserComments(intent: Intent, status:String)

}