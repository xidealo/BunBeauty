package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs.ISubscriptionsSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs.ISubscriptionsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.SubscriptionsView

@InjectViewState
class SubscriptionsPresenter(
    private val subscriptionsSubscriptionInteractor: ISubscriptionsSubscriptionInteractor,
    private val subscriptionsUserInteractor: ISubscriptionsUserInteractor
) : MvpPresenter<SubscriptionsView>(), SubscriptionsPresenterCallback {

    fun getUsersLink() = subscriptionsUserInteractor.getUsersLink()

    fun createSubscriptionsScreen() {
        subscriptionsUserInteractor.createSubscriptionScreen(this)
    }

    override fun getSubscriptions(user: User) {
        subscriptionsSubscriptionInteractor.getSubscriptions(user, this)
    }

    override fun getUsersBySubscription(subscriptions: List<Subscription>) {
        subscriptionsUserInteractor.getUsersBySubscription(subscriptions, this)
    }

    override fun showSubscriptions(users: List<User>) {
        viewState.showSubscriptions(users)
        viewState.hideLoading()
    }

    override fun showEmptySubscriptions() {
        viewState.showEmptySubscriptions()
    }

}