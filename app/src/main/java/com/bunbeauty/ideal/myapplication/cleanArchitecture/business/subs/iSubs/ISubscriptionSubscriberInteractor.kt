package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subs.SubscriptionsPresenterCallback

interface ISubscriptionSubscriberInteractor {
    fun createSubscriptionScreen(subscriptionsPresenterCallback: SubscriptionsPresenterCallback)
}