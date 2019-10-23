package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import android.content.Intent
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ProfileCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ProfileView

@InjectViewState
class ProfilePresenter(private val profileInteractor: ProfileInteractor):
        MvpPresenter<ProfileView>(), ProfileCallback {

    private val TAG = "DBInf"

    fun initFCM() {
        if (profileInteractor.isFirstEnter()) {
            profileInteractor.initFCM()
        }
    }

    fun showProfile() {
        if (profileInteractor.userIsOwner()) {
            viewState.showMyProfile()
        } else {
            viewState.showNotMyProfile()
        }
    }

    fun getOwnerId() = profileInteractor.getOwnerId()

    fun updateProfileData() {
        updateUserInfo()
        updateServiceList()
    }

    private fun updateServiceList() {
        //viewState.showUserServices(profileInteractor.getProfileServiceList(intent))
    }

    private fun updateUserInfo() {
        profileInteractor.getProfileOwner(this)
    }

    override fun callbackGetUser(user: User) {
        viewState.showUserInfo(user)
    }





}