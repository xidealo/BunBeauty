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

    fun initFCM() {
        profileUserInteractor.initFCM()
    }

    fun getProfileOwner() {
        viewState.showProgress()
        viewState.invisibleSubscribeButton()
        profileUserInteractor.getProfileOwner(this)
    }

    override fun returnProfileOwner(user: User) {
        viewState.showProfileInfo(user.name, user.surname, user.city)
        viewState.showAvatar(user.photoLink)
        viewState.showRating(user.rating, user.countOfRates)
        viewState.setServiceAdapter(profileServiceInteractor.getServices(), user)

        profileServiceInteractor.getServicesByUserId(user.id, this)
        profileSubscriberInteractor.getSubscribers(user.id, this)
    }

    override fun showMyProfile(user: User) {
        viewState.showOrders()
        viewState.showTabLayout()
        viewState.showScheduleButton()
        viewState.hideDialogsButton()
        viewState.hideSubscribeButton()
    }

    override fun showAlienProfile(user: User) {
        viewState.showServices()
        viewState.showDialogsButton()
        viewState.hideScheduleButton()
        viewState.hideSubscriptionsButton()
        viewState.hideTabLayout()
        viewState.hideCreateServiceButton()
    }

    fun updateUser(user: User) {
        profileUserInteractor.updateUser(user, this)
    }

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

    override fun goToEditProfile(user: User) {
        //viewState.goToEditProfile(user)
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
        subscriber.date = WorkWithTimeApi.getDateInFormatYMDHMS(Date())
        subscriber.userId = profileUserInteractor.getCurrentUser().id
        subscriber.subscriberId = User.getMyId()
        profileSubscriberInteractor.addSubscriber(subscriber, this)
    }

    fun goToSubscriptions() {
        viewState.goToSubscriptions(profileUserInteractor.getCurrentUser())
    }

    override fun getProfileServiceList(userId: String) {
        profileServiceInteractor.getServicesByUserId(userId, this)
    }

    override fun showSubscribed() {
        viewState.showSubscribeButton()
        viewState.showSubscribed()
    }

    override fun showUnsubscribed() {
        viewState.showSubscribeButton()
        viewState.showUnsubscribed()
    }

    override fun showCountOfSubscriber(count: Long) {
        viewState.showCountOfSubscriber(count)
    }

    override fun setServiceList(serviceList: List<Service>) {
        viewState.showUserServices(serviceList, profileUserInteractor.getCurrentUser())
        viewState.hideProgress()
    }
}