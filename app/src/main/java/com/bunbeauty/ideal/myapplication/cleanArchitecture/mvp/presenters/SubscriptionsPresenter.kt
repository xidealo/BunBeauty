package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs.ISubscriptionsSubscriberInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs.ISubscriptionsSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs.ISubscriptionsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.SubscriptionsView

@InjectViewState
class SubscriptionsPresenter(
    private val subscriptionsSubscriptionInteractor: ISubscriptionsSubscriptionInteractor,
    private val subscriptionsUserInteractor: ISubscriptionsUserInteractor,
    private val subscriptionsSubscriberInteractor: ISubscriptionsSubscriberInteractor
) : MvpPresenter<SubscriptionsView>(), SubscriptionsPresenterCallback {

    fun createSubscriptionsScreen() {
        subscriptionsUserInteractor.createSubscriptionScreen(this)
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

    override fun getSubscriptions(user: User) {
        subscriptionsSubscriptionInteractor.getSubscriptions(user, this)
    }

    override fun getUsersBySubscription(subscriptions: List<Subscription>) {
        subscriptionsUserInteractor.getUsersBySubscription(subscriptions, this)
    }

    override fun showSubscriptions(subscriptions: List<Subscription>) {
        viewState.showSubscriptions(subscriptions)
        viewState.hideLoading()
    }

    override fun showDeletedSubscription(subscription: Subscription) {
        viewState.showMessage("Вы отписались от ${subscription.subscriptionUser.name} ${subscription.subscriptionUser.surname} ")
    }

    override fun showEmptySubscriptions() {
        viewState.hideLoading()
        viewState.showEmptySubscriptions()
    }

    override fun fillSubscriptions(
        users: List<User>
    ) {
        subscriptionsSubscriptionInteractor.fillSubscriptions(users, this)
    }

}