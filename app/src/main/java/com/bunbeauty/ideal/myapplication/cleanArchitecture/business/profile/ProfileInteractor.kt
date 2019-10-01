package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ProfileCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ProfileFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.presentation.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.logIn.ProfileRepository

class ProfileInteractor(private val profileActivity: ProfileActivity) : IProfileInteractor, ProfileCallback{

    private var profileRepository:ProfileRepository = ProfileFirebase(this)

    override fun loadProfile(ownerId: String) {
        profileRepository.loadProfileData(ownerId)
    }

    override fun callbackSetProfile(user: User) {
        profileActivity.setUser(user)
    }

    override fun updateProfile(ownerId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCountOfRates(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserId(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkSubscription(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun goToAddService(intent: Intent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun goToSubscribers(intent: Intent, status: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun goToUserComments(intent: Intent, status: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}