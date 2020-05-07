package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscriber

interface ISubscriptionsSubscriberInteractor {
    fun deleteSubscriber(subscriber: Subscriber)
}