package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters

import com.android.ideal.myapplication.R
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.profile.iProfile.*
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.*
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.ProfileView

@InjectViewState
class ProfilePresenter(
    private val profileUserInteractor: IProfileUserInteractor,
    private val profileServiceInteractor: IProfileServiceInteractor,
    private val profileOrderInteractor: IProfileOrderInteractor,
    private val profileDialogInteractor: IProfileDialogInteractor,
    private val profileSubscriptionInteractor: IProfileSubscriptionInteractor,
    private val profileSubscriberInteractor: IProfileSubscriberInteractor
) : MvpPresenter<ProfileView>(), ProfilePresenterCallback {

    fun initFCM() {
        profileUserInteractor.initFCM()
    }

    fun getProfileOwner() {
        viewState.showProgress()
        viewState.hideSubscribeButton()
        viewState.hideSubscriptionsButton()
        viewState.hideScheduleButton()
        viewState.hideDialogsButton()

        profileUserInteractor.getProfileOwner(this)
    }

    override fun returnProfileOwner(user: User) {
        viewState.showProfileInfo(user.name, user.surname, user.city)
        viewState.showAvatar(user.photoLink)
        viewState.showRating(user.rating, user.countOfRates)

        profileServiceInteractor.getServicesByUserId(user.id, this)
    }

    override fun showMyProfile(user: User) {
        viewState.showBottomPanel(R.id.navigation_profile)
        viewState.showOrders()
        viewState.showTabLayout()
        viewState.showScheduleButton()
        viewState.showSubscriptionsButton()
        viewState.showTopPanelWithEditIcon()
        viewState.showCreateServiceButton()

        viewState.hideDialogsButton()
        viewState.hideSubscribeButton()
    }

    override fun showAlienProfile(user: User) {
        viewState.showBottomPanel()
        viewState.showServices()
        viewState.showDialogsButton()
        viewState.showEmptyTopPanel()

        viewState.hideScheduleButton()
        viewState.hideSubscriptionsButton()
        viewState.hideTabLayout()
        viewState.disableSwipe()
        viewState.hideCreateServiceButton()

        profileSubscriberInteractor.checkSubscribed(User.getMyId(), user, this)
    }

    fun checkProfileToUpdateServices() {
        profileUserInteractor.checkProfileToUpdateServices(this)
    }

    override fun getServiceList(userId: String) {
        profileServiceInteractor.getServicesByUserId(userId, this)
    }

    override fun showServiceList(serviceList: List<Service>) {
        viewState.showServiceList(serviceList)
        viewState.hideProgress()
    }

    fun checkProfileToUpdateOrders() {
        profileUserInteractor.checkProfileToUpdateOrders(this)
    }

    override fun getOrderList(userId: String) {
        profileOrderInteractor.getOrderListByUserId(userId, this)
    }

    override fun showOrderList(orderList: List<Order>) {
        viewState.showOrderList(orderList)
        viewState.hideProgress()
    }

    fun updateUser(user: User) {
        profileUserInteractor.updateUserFromEditUser(user, this)
    }

    fun getCacheOwner() = profileUserInteractor.owner!!

    fun getDialog() {
        profileDialogInteractor.getDialog(User.cacheUser, profileUserInteractor.owner!!, this)
    }

    override fun goToMessages(myDialog: Dialog, companionDialog: Dialog) {
        viewState.goToMessages(myDialog, companionDialog)
    }

    fun subscribe() {
        val subscriber = Subscriber()
        subscriber.userId = profileUserInteractor.owner!!.id
        subscriber.subscriberId = User.getMyId()
        profileSubscriberInteractor.checkSubscriber(subscriber, this)
    }

    override fun updateCountOfSubscribers(subscriber: Int) {
        profileUserInteractor.updateCountOfSubscribers(
            profileUserInteractor.owner!!,
            subscriber,
            this
        )
    }

    override fun addSubscription(subscriber: Subscriber) {
        val subscription = Subscription()
        subscription.userId = subscriber.subscriberId
        subscription.subscriptionId = subscriber.userId
        profileSubscriptionInteractor.addSubscription(subscription, this)
    }

    override fun deleteSubscription(subscriber: Subscriber) {
        val subscription = Subscription()
        subscription.date = subscriber.date
        subscription.userId = subscriber.subscriberId
        subscription.subscriptionId = subscriber.userId
        profileSubscriptionInteractor.deleteSubscription(subscription, this)
    }

    fun goToSubscriptions() {
        viewState.goToSubscriptions(profileUserInteractor.owner!!)
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

    override fun showUpdatedBottomPanel(selectedItemId: Int) {
        viewState.showUpdatedBottomPanel(selectedItemId)
    }
}