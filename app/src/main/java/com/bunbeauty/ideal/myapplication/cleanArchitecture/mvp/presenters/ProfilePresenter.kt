package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.android.ideal.myapplication.R
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
        viewState.setServiceAdapter(profileServiceInteractor.getServices(), user)

        profileServiceInteractor.getServicesByUserId(user.id, this)
        profileSubscriberInteractor.getSubscribers(
            user.id,
            profileUserInteractor.isMyProfile(user.id, User.getMyId()),
            this
        )
    }

    override fun showMyProfile(user: User) {
        viewState.showBottomPanel(R.id.navigation_profile)
        viewState.showOrders()
        viewState.showTabLayout()
        viewState.showScheduleButton()
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
        val subscriber = Subscriber()
        subscriber.date = WorkWithTimeApi.getDateInFormatYMDHMS(Date())
        subscriber.userId = profileUserInteractor.getCurrentUser().id
        subscriber.subscriberId = User.getMyId()
        profileSubscriberInteractor.checkSubscriber(subscriber, this)
    }

    override fun addSubscription(subscriber: Subscriber) {
        val subscription = Subscription()
        subscription.date = subscriber.date
        subscription.userId = subscriber.subscriberId
        subscription.subscriptionId = subscriber.userId
        profileSubscriptionInteractor.addSubscription(subscription, this)
    }

    override fun deleteSubscription(subscriber: Subscriber) {
        val subscription = Subscription()
        subscription.date = subscriber.date
        subscription.userId = subscriber.subscriberId
        subscription.subscriptionId = subscriber.userId // по не му подписку и удалить ее
        profileSubscriptionInteractor.deleteSubscription(subscription, this)
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

    fun updateBottomPanel() {
        profileUserInteractor.updateBottomPanel(this)
    }

    override fun showUpdatedBottomPanel(selectedItemId: Int) {
        viewState.showUpdatedBottomPanel(selectedItemId)
    }
}