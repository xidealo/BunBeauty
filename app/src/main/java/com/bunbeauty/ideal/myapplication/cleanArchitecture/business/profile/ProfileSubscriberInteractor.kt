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
    private var isMyProfile = false
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

    override fun getSubscribers(
        userId: String,
        isMyProfile: Boolean,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        this.isMyProfile = isMyProfile
        subscriberRepository.getByUserId(userId, this)
    }

    private fun addSubscriber(
        subscriber: Subscriber,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        subscriberRepository.insert(subscriber, this)
    }

    private fun deleteSubscriber(
        subscriber: Subscriber,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        subscriberRepository.delete(subscriber, this)
    }

    override fun returnCreatedCallback(obj: Subscriber) {
        isSubscribed = true
        cacheSubscribers.add(obj)
        profilePresenterCallback.addSubscription(obj)
        updateCountOfSubscribers(cacheSubscribers, profilePresenterCallback)
    }

    override fun returnDeletedCallback(obj: Subscriber) {
        isSubscribed = false
        cacheSubscribers.remove(obj)
        profilePresenterCallback.deleteSubscription(obj)
        updateCountOfSubscribers(cacheSubscribers, profilePresenterCallback)
    }

    override fun returnList(objects: List<Subscriber>) {
        cacheSubscribers.addAll(objects)

        if (!isMyProfile) {
            checkSubscribed(User.getMyId(), cacheSubscribers, profilePresenterCallback)
        }

        if (cacheSubscribers.isNotEmpty()) {
            updateCountOfSubscribers(cacheSubscribers, profilePresenterCallback)
        }
    }

    override fun checkSubscribed(
        userId: String,
        subscribers: List<Subscriber>,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        isSubscribed = subscribers.find { it.subscriberId == userId } != null

        if (isSubscribed) {
            profilePresenterCallback.showSubscribed()
        } else {
            profilePresenterCallback.showUnsubscribed()
        }
    }

    override fun updateCountOfSubscribers(
        subscribers: List<Subscriber>, profilePresenterCallback: ProfilePresenterCallback
    ) {
        profilePresenterCallback.showCountOfSubscriber(subscribers.size.toLong())
    }


}