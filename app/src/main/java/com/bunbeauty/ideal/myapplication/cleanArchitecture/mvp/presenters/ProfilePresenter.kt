package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.android.ideal.myapplication.R
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ProfileView

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

    fun updateMyProfileServices() {
        profileUserInteractor.updateMyProfileServices(this)
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

    fun updateUser(user: User) {
        profileUserInteractor.updateUserFromEditUser(user, this)
    }

    fun getCacheOwner() = profileUserInteractor.getCacheOwner()

    fun getCacheUser() = profileUserInteractor.getCacheUser()

    fun goToDialog() {
        profileDialogInteractor.goToDialog(getCacheUser(), getCacheOwner(), this)
    }

    override fun goToEditProfile(user: User) {
        //viewState.goToEditProfile(user)
    }

    override fun goToDialog(myDialog: Dialog, companionDialog: Dialog) {
        viewState.goToMessages(myDialog, companionDialog)
    }

    fun subscribe() {
        val subscriber = Subscriber()
        subscriber.userId = profileUserInteractor.getCacheOwner().id
        subscriber.subscriberId = User.getMyId()
        profileSubscriberInteractor.checkSubscriber(subscriber, this)
    }

    override fun updateCountOfSubscribers(subscriber: Int) {
        profileUserInteractor.updateCountOfSubscribers(
            profileUserInteractor.getCacheOwner(),
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
        viewState.goToSubscriptions(profileUserInteractor.getCacheOwner())
    }

    override fun getServiceList(userId: String) {
        profileServiceInteractor.getServicesByUserId(userId, this)
    }

    override fun showServiceList(serviceList: List<Service>) {
        viewState.showServiceList(serviceList)
        viewState.hideProgress()
    }

    override fun getOrderList(userId: String) {
        profileOrderInteractor.getOrderListByUserId(userId, this)
    }

    override fun showOrderList(orderList: List<Order>) {
        viewState.showOrderList(orderList)
        viewState.hideProgress()
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

    fun updateBottomPanel() {
        profileUserInteractor.updateBottomPanel(this)
    }

    override fun showUpdatedBottomPanel(selectedItemId: Int) {
        viewState.showUpdatedBottomPanel(selectedItemId)
    }
}