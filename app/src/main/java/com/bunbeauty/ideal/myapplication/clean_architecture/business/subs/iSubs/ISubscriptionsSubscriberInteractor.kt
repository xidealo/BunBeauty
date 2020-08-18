package com.bunbeauty.ideal.myapplication.clean_architecture.business.subs.iSubs

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscriber

interface ISubscriptionsSubscriberInteractor {
    fun deleteSubscriber(subscriber: Subscriber)
}