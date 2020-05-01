package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ProfileView

@InjectViewState
class ProfilePresenter(
    private val profileUserInteractor: IProfileUserInteractor,
    private val profileServiceInteractor: IProfileServiceInteractor,
    private val profileDialogInteractor: IProfileDialogInteractor
) :
    MvpPresenter<ProfileView>(), ProfilePresenterCallback {

    private val TAG = "DBInf"

    fun initFCM() {
        profileUserInteractor.initFCM()
    }

    fun createProfileScreen() {
        viewState.showProgress()
        profileUserInteractor.getProfileOwner(this)
    }

    fun getServiceLink() = profileServiceInteractor.getServicesLink()

    fun checkIconClick() {
        profileUserInteractor.checkIconClick(this)
    }

    fun goToDialog() {
        profileDialogInteractor.goToDialog(
            User.getMyId(),
            profileUserInteractor.getCurrentUser(),
            this
        )
    }

    override fun showMyProfile(user: User) {
        viewState.showMyProfileView()
        viewState.showSwitcher()
        viewState.createTopPanelForMyProfile("${user.name} ${user.surname}")
        viewState.hideDialogs()
    }

    override fun showAlienProfile(user: User) {
        viewState.showAlienProfileView()
        viewState.hideSubscriptions()
        viewState.hideSwitcher()
        viewState.createTopPanelForOtherProfile("${user.name} ${user.surname}")
        viewState.showDialogs()
    }

    override fun showRating(rating: Float) {
        viewState.showRating(rating)
    }

    override fun showWithoutRating() {
        viewState.showWithoutRating()
    }

    override fun goToEditProfile(user: User) {
        viewState.goToEditProfile(user)
    }

    override fun goToDialog(dialog: Dialog) {
        viewState.goToDialog(dialog)
    }

    override fun subscribe() {
        viewState.subscribe()
    }

    override fun getProfileServiceList(userId: String) {
        profileServiceInteractor.getServicesByUserId(userId, this)
    }

    override fun setUserProfile(user: User) {
        viewState.showProfileInfo(user)
        viewState.showAvatar(user.photoLink)
        viewState.showSubscribers(user.subscribersCount)
        viewState.showSubscriptions(user.subscriptionsCount)
        viewState.hideAddService()
    }

    override fun setServiceList(serviceList: List<Service>) {
        viewState.showUserServices(serviceList, profileUserInteractor.getCurrentUser())
        viewState.hideProgress()
    }
}