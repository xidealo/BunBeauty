package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileSubscriberInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscriber.DeleteSubscriberCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscriber.InsertSubscriberCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscriber.SubscribersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.ISubscriberRepository

class ProfileSubscriberInteractor(private val subscriberRepository: ISubscriberRepository) :
    IProfileSubscriberInteractor, InsertSubscriberCallback, SubscribersCallback,
    DeleteSubscriberCallback {
    private var isSubscribed = false
    private val cacheSubscribers = mutableListOf<Subscriber>()
    private lateinit var profilePresenterCallback: ProfilePresenterCallback

    override fun checkSubscriber(
        subscriber: Subscriber,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        if (!isSubscribed) {
            addSubscriber(subscriber, profilePresenterCallback)
        } else {
            deleteSubscriber(
                cacheSubscribers.find { it.subscriberId == subscriber.subscriberId }!!,
                profilePresenterCallback
            )
        }
    }

    private fun addSubscriber(
        subscriber: Subscriber,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        subscriberRepository.insert(subscriber, this)
    }

    override fun returnCreatedCallback(obj: Subscriber) {
        isSubscribed = true
        cacheSubscribers.add(obj)
        profilePresenterCallback.addSubscription(obj)
        profilePresenterCallback.updateCountOfSubscribers(1)
    }

    private fun deleteSubscriber(
        subscriber: Subscriber,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        subscriberRepository.delete(subscriber, this)
    }

    override fun returnDeletedCallback(obj: Subscriber) {
        isSubscribed = false
        cacheSubscribers.remove(obj)
        profilePresenterCallback.deleteSubscription(obj)
        profilePresenterCallback.updateCountOfSubscribers(-1)
    }

    //проверить в моих подписках это айди
    override fun checkSubscribed(
        userId: String,
        user: User,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback

        if (cacheSubscribers.isEmpty()) {
            subscriberRepository.getBySubscriberId(userId, user.id, this)
        } else {
            isSubscribed = true
            profilePresenterCallback.showSubscribed()
        }
    }

    override fun returnList(objects: List<Subscriber>) {
        if (objects.isEmpty()) {
            isSubscribed = false
            profilePresenterCallback.showUnsubscribed()
        } else {
            cacheSubscribers.clear()
            cacheSubscribers.addAll(objects)
            isSubscribed = true
            profilePresenterCallback.showSubscribed()
        }
    }

}