package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscription.DeleteSubscriptionCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscription.InsertSubscriptionCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscription.SubscriptionsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.ISubscriptionRepository

class ProfileSubscriptionInteractor(private val subscriptionRepository: ISubscriptionRepository) :
    IProfileSubscriptionInteractor, InsertSubscriptionCallback, DeleteSubscriptionCallback,
    SubscriptionsCallback {
    private lateinit var profilePresenterCallback: ProfilePresenterCallback
    private val cacheSubscriptions = mutableListOf<Subscription>()

    override fun getSubscriptions(
        myUserId: String,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        subscriptionRepository.getByUserId(myUserId, this)
    }

    override fun addSubscription(
        subscription: Subscription,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        subscriptionRepository.insert(subscription, this)
    }

    override fun deleteSubscription(
        subscription: Subscription,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        subscriptionRepository.deleteByBySubscriptionId(
            subscription,
            this
        )
    }

    override fun returnCreatedCallback(obj: Subscription) {
        profilePresenterCallback.showSubscribed()
    }

    override fun returnDeletedCallback(obj: Subscription) {
        profilePresenterCallback.showUnsubscribed()
    }

    override fun returnList(objects: List<Subscription>) {
        cacheSubscriptions.addAll(objects)
    }

}