package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscription.DeleteSubscriptionCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscription.InsertSubscriptionCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscription.SubscriptionsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.SubscriptionRepository

class ProfileSubscriptionInteractor(private val subscriptionRepository: SubscriptionRepository) :
    IProfileSubscriptionInteractor, InsertSubscriptionCallback, DeleteSubscriptionCallback,
    SubscriptionsCallback {
    private lateinit var profilePresenterCallback: ProfilePresenterCallback

    private var isSubscribed = false

    override fun getSubscriptions(myUserId: String) {
        subscriptionRepository.getByUserId(myUserId, this)
    }

    override fun addSubscription(
        subscription: Subscription,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        if (isSubscribed) {
            subscriptionRepository.insert(subscription, this)
        } else {
            subscriptionRepository.delete(subscription, this)
        }
    }

    override fun returnCreatedCallback(obj: Subscription) {
        profilePresenterCallback.showSubscribed()
    }

    override fun returnDeletedCallback(obj: Subscription) {
        profilePresenterCallback.showUnsubscribed()
    }

    override fun returnList(objects: List<Subscription>) {
        isSubscribed = objects.isEmpty()
    }

}