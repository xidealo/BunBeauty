package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.subs.iSubs.ISubscriptionsSubscriberInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.subs.iSubs.ISubscriptionsSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.subs.iSubs.ISubscriptionsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscriber
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.SubscriptionsView

@InjectViewState
class SubscriptionsPresenter(
    private val subscriptionsSubscriptionInteractor: ISubscriptionsSubscriptionInteractor,
    private val subscriptionsUserInteractor: ISubscriptionsUserInteractor,
    private val subscriptionsSubscriberInteractor: ISubscriptionsSubscriberInteractor
) : MvpPresenter<SubscriptionsView>(), SubscriptionsPresenterCallback {

    fun createSubscriptionsScreen(loadingLimit: Int) {
        subscriptionsUserInteractor.createSubscriptionScreen(loadingLimit, this)
    }

    fun deleteSubscription(subscription: Subscription) {
        subscriptionsSubscriptionInteractor.deleteSubscription(subscription, this)
    }

    fun deleteSubscriber(subscriber: Subscriber) {
        subscriptionsSubscriberInteractor.deleteSubscriber(subscriber)
    }

    override fun deleteUser(subscriptionId: String) {
        subscriptionsUserInteractor.deleteUser(subscriptionId, this)
    }

    override fun getSubscriptions(user: User, loadingLimit: Int) {
        subscriptionsSubscriptionInteractor.getSubscriptions(user, loadingLimit, this)
    }

    override fun getUserBySubscription(subscription: Subscription) {
        subscriptionsUserInteractor.getUsersBySubscription(subscription, this)
    }

    override fun showSubscription(subscription: Subscription) {
        viewState.showSubscription(subscription)
        viewState.hideLoading()
    }

    override fun removeSubscription(subscription: Subscription) {
        viewState.removeSubscription(subscription)
    }

    override fun showDeletedSubscription(subscription: Subscription) {
        viewState.showMessage("Вы отписались от ${subscription.subscriptionUser.name} ${subscription.subscriptionUser.surname} ")
    }

    override fun showEmptySubscription() {
        viewState.hideLoading()
        viewState.showEmptySubscriptions()
    }

    override fun fillSubscription(
        user: User
    ) {
        subscriptionsSubscriptionInteractor.fillSubscription(user, this)
    }

}