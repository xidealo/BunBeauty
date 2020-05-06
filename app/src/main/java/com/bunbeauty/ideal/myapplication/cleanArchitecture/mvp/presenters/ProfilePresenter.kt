package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ProfileView
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import java.util.*

@InjectViewState
class ProfilePresenter(
    private val profileUserInteractor: IProfileUserInteractor,
    private val profileServiceInteractor: IProfileServiceInteractor,
    private val profileDialogInteractor: IProfileDialogInteractor,
    private val profileSubscriptionInteractor: IProfileSubscriptionInteractor,
    private val profileSubscriberInteractor: IProfileSubscriberInteractor
) : MvpPresenter<ProfileView>(), ProfilePresenterCallback {

    private val TAG = "DBInf"

    fun initFCM() {
        profileUserInteractor.initFCM()
    }

    fun createProfileScreen() {
        viewState.showProgress()
        profileUserInteractor.getProfileOwner(this)
    }

    fun updateUser(user: User) {
        profileUserInteractor.updateUser(user, this)
    }

    fun getServiceLink() = profileServiceInteractor.getServicesLink()

    fun checkIconClick() {
        profileUserInteractor.checkIconClick(this)
    }

    fun getCurrentUser() = profileUserInteractor.getCurrentUser()

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
        viewState.hideSubscribe()
    }

    override fun showAlienProfile(user: User) {
        viewState.showAlienProfileView()
        viewState.hideSubscriptions()
        viewState.hideSwitcher()
        viewState.createTopPanelForOtherProfile("${user.name} ${user.surname}")
        viewState.showDialogs()
        viewState.showSubscribe()

        profileSubscriptionInteractor.checkSubscribed(user.id, this)
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

    fun subscribe() {
        val subscription = Subscription()
        subscription.date = WorkWithTimeApi.getDateInFormatYMDHMS(Date())
        subscription.userId = User.getMyId()
        subscription.subscriptionId = profileUserInteractor.getCurrentUser().id
        profileSubscriptionInteractor.addSubscription(subscription, this)

        val subscriber = Subscriber()
        subscription.date = WorkWithTimeApi.getDateInFormatYMDHMS(Date())
        subscription.userId = profileUserInteractor.getCurrentUser().id
        subscription.subscriptionId = User.getMyId()
        profileSubscriberInteractor.addSubscriber(subscriber, this)
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

    override fun showSubscribed() {
        viewState.showSubscribed()
    }

    override fun showUnsubscribed() {
        viewState.showUnsubscribed()
    }

    override fun setServiceList(serviceList: List<Service>) {
        viewState.showUserServices(serviceList, profileUserInteractor.getCurrentUser())
        viewState.hideProgress()
    }
}