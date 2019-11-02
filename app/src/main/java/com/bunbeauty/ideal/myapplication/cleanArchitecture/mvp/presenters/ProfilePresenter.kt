package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ProfileCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
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

    fun showProfileView() {
        if (profileInteractor.isUserOwner()) {
            viewState.showMyProfileView()
        } else {
            viewState.showAlienProfileView()
        }
    }

    fun isUserOwner() = profileInteractor.isUserOwner()

    fun getOwnerId() = profileInteractor.getOwnerId()

    fun updateProfileData() {
        updateUserInfo()
        updateServiceList()
    }

    private fun updateServiceList() = profileInteractor.getProfileServiceList(this)

    private fun updateUserInfo() = profileInteractor.getProfileOwner(this)

    override fun callbackGetUser(user: User) {
        viewState.showUserInfo(user)
    }

    override fun callbackGetServiceList(serviceList: List<Service>) {
        viewState.showUserServices(serviceList)
    }
}