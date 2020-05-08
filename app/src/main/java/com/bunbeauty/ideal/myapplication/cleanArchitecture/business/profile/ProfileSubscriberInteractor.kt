package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileSubscriberInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscriber.InsertSubscriberCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscriber.SubscribersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.ISubscriberRepository

class ProfileSubscriberInteractor(private val subscriberRepository: ISubscriberRepository) :
    IProfileSubscriberInteractor, InsertSubscriberCallback, SubscribersCallback {

    private var isSubscribed = false
    private val cacheSubscribers = mutableListOf<Subscriber>()
    private lateinit var profilePresenterCallback: ProfilePresenterCallback

    override fun getSubscribers(
        userId: String,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        subscriberRepository.getByUserId(userId, this)
    }

    override fun addSubscriber(
        subscriber: Subscriber,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        subscriberRepository.insert(subscriber, this)
    }

    override fun returnCreatedCallback(obj: Subscriber) {

    }

    override fun returnList(objects: List<Subscriber>) {
        cacheSubscribers.addAll(objects)
        if (cacheSubscribers.isNotEmpty()) {
            checkSubscribed(User.getMyId(), cacheSubscribers, profilePresenterCallback)
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