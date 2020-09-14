package com.bunbeauty.ideal.myapplication.clean_architecture.domain.profile

import com.bunbeauty.ideal.myapplication.clean_architecture.domain.profile.iProfile.IProfileSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription.DeleteSubscriptionCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription.InsertSubscriptionCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription.SubscriptionsCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.ISubscriptionRepository

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

    override fun returnCreatedCallback(obj: Subscription) {
        profilePresenterCallback.showSubscribed()
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

    override fun returnDeletedCallback(obj: Subscription) {
        profilePresenterCallback.showUnsubscribed()
    }

    override fun returnList(objects: List<Subscription>) {
        cacheSubscriptions.clear()
        cacheSubscriptions.addAll(objects)
    }

}