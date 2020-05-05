package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs.ISubscriptionSubscriberInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.SubscriptionsView

@InjectViewState
class SubscriptionSubscriptionPresenter(private val subscriptionInteractor: ISubscriptionSubscriberInteractor) :
    MvpPresenter<SubscriptionsView>(), SubscriptionsPresenterCallback {

    fun createSubscriptionsScreen(){
        subscriptionInteractor.createSubscriptionScreen(this)
    }

    override fun getUserBySubscriptionId(subscriptions: List<Subscription>) {

    }

    override fun showSubscriptions(subscriptions: List<Subscription>) {

    }


}