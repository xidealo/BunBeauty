package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import android.content.Intent
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ProfileView

@InjectViewState
class ProfilePresenter(private val profileInteractor: ProfileInteractor): MvpPresenter<ProfileView>() {

    private val TAG = "DBInf"

    fun initFCM(intent: Intent) {
        if (profileInteractor.isFirstEnter(intent)) {
            profileInteractor.initFCM()
        }
    }

    fun showProfile(intent: Intent) {
        if (profileInteractor.userIsOwner(intent)) {
            viewState.showMyProfile()
        } else {
            viewState.showNotMyProfile()
        }
    }

    fun getOwnerId(intent: Intent) = profileInteractor.getOwnerId(intent)

    fun updateProfileData(intent: Intent) {
        updateUserInfo(intent)
        updateServiceList(intent)
    }

    private fun updateServiceList(intent: Intent) {
        viewState.showUserServices(profileInteractor.getProfileServiceList(intent))
    }

    private fun updateUserInfo(intent: Intent) {
        viewState.showUserInfo(profileInteractor.getProfileOwner(intent))
    }





}