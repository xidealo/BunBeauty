package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs.ISubscriptionsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UpdateUsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.UserRepository

class SubscriptionsUserInteractor(
    private val intent: Intent,
    private val userRepository: UserRepository
) : ISubscriptionsUserInteractor, UserCallback, UpdateUsersCallback {

    private lateinit var subscriptionsPresenterCallback: SubscriptionsPresenterCallback

    private var cacheUsers = mutableListOf<User>()
    private var subscriptionsCount = 0
    private var currentSubscriptionsCount = 0

    override fun getUsersLink() = cacheUsers

    override fun deleteUser(
        subscriptionId: String,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    ) {
        this.subscriptionsPresenterCallback = subscriptionsPresenterCallback
        val user = cacheUsers.find { it.id == subscriptionId }!!
        cacheUsers.remove(user)
        user.subscribersCount = user.subscribersCount - 1
        userRepository.update(user, this)
    }

    override fun createSubscriptionScreen(subscriptionsPresenterCallback: SubscriptionsPresenterCallback) {
        val user = intent.getSerializableExtra(User.USER) as User
        subscriptionsPresenterCallback.getSubscriptions(user)
    }

    override fun getUsersBySubscription(
        subscriptions: List<Subscription>,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    ) {
        this.subscriptionsPresenterCallback = subscriptionsPresenterCallback
        subscriptionsCount = subscriptions.size

        if (subscriptions.isEmpty()) {
            subscriptionsPresenterCallback.showEmptySubscriptions()
            return
        }

        for (subscription in subscriptions) {
            userRepository.getById(subscription.subscriptionId, this, true)
        }
    }

    override fun returnGottenObject(element: User?) {
        if (element == null) return

        currentSubscriptionsCount++
        cacheUsers.add(element)

        if (currentSubscriptionsCount == subscriptionsCount) {
            subscriptionsPresenterCallback.fillSubscriptions(cacheUsers)
        }
    }

    override fun returnUpdatedCallback(obj: User) {
        //subscriptionsPresenterCallback.showSubscriptions()
    }

}
