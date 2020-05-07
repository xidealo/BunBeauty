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

    fun getUsersLink() = subscriptionsUserInteractor.getUsersLink()
    fun getSubscriptionsLink() = subscriptionsSubscriptionInteractor.getSubscriptionsLink()

    fun createSubscriptionsScreen() {
        subscriptionsUserInteractor.createSubscriptionScreen(this)
    }

    fun deleteSubscription(subscription: Subscription) {
        subscriptionsSubscriptionInteractor.deleteSubscription(subscription, this)
    }

    fun deleteSubscriber(subscriber: Subscriber) {
        subscriptionsSubscriberInteractor.deleteSubscriber(subscriber)
    }

    fun deleteUser(user: User) {
        subscriptionsUserInteractor.deleteUser(user, this)
    }

    override fun getSubscriptions(user: User) {
        subscriptionsSubscriptionInteractor.getSubscriptions(user, this)
    }

    override fun getUsersBySubscription(subscriptions: List<Subscription>) {
        subscriptionsUserInteractor.getUsersBySubscription(subscriptions, this)
    }

    override fun showSubscriptions() {
        viewState.showSubscriptions()
        viewState.hideLoading()
    }

    override fun showEmptySubscriptions() {
        viewState.showEmptySubscriptions()
    }

}