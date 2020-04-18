package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.IProfilePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ProfileView

@InjectViewState
class ProfilePresenter(private val profileInteractor: ProfileInteractor) :
        MvpPresenter<ProfileView>(), IProfilePresenter {

    private val TAG = "DBInf"

    fun initFCM() {
        profileInteractor.initFCM()
    }

    fun createProfileScreen() {
        viewState.showProgress()
        profileInteractor.getProfileOwner(this)
    }

    fun showProfile(){
        profileInteractor.showProfile(this)
    }

    override fun showMyProfile() {
        viewState.showMyProfileView()
        viewState.createSwitcher()
    }

    override fun showAlienProfile() {
        viewState.showAlienProfileView()
        viewState.hideSubscriptions()
        viewState.showDialogs()
    }

    override fun showRating(rating: Float) {
        viewState.showRating(rating)
    }

    override fun showWithoutRating() {
        viewState.showWithoutRating()
    }

    @Deprecated("Удалить, когда исправим панели")
    fun isUserOwner() = profileInteractor.isUserOwner()

    fun getOwnerId() = profileInteractor.getOwnerId()

    override fun setUserProfile(user: User) {
        viewState.showProfileInfo(user.name, user.city, user.phone)
        viewState.showAvatar(user.photoLink)
        viewState.showSubscribers(user.subscribersCount)
        viewState.showSubscriptions(user.subscriptionsCount)
        viewState.createTopPanelForMyProfile(user.name)

        profileInteractor.setRating(user.rating, this)
    }

    override fun setServiceListWithOwner(serviceList: List<Service>, user: User) {
        viewState.showUserServices(serviceList, user)
        viewState.hideProgress()
    }
}