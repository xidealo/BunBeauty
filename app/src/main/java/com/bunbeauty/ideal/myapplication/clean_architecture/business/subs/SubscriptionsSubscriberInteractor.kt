package com.bunbeauty.ideal.myapplication.clean_architecture.business.subs

import com.bunbeauty.ideal.myapplication.clean_architecture.business.subs.iSubs.ISubscriptionsSubscriberInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscriber.DeleteSubscriberCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscriber.SubscribersCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscriber
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.ISubscriberRepository

class SubscriptionsSubscriberInteractor(private val subscriberRepository: ISubscriberRepository) :
    ISubscriptionsSubscriberInteractor, DeleteSubscriberCallback, SubscribersCallback {

    override fun deleteSubscriber(subscriber: Subscriber) {
        getSubscriberBySubscriberId(subscriber.subscriberId, subscriber.userId)
    }

    private fun getSubscriberBySubscriberId(subscriberId: String, ownerId: String) {
        subscriberRepository.getBySubscriberId(
            subscriberId,
            ownerId, this
        )
    }

    override fun returnList(objects: List<Subscriber>) {
        if (objects.isNotEmpty())
            subscriberRepository.delete(objects.first(), this)
    }

    override fun returnDeletedCallback(obj: Subscriber) {}

}