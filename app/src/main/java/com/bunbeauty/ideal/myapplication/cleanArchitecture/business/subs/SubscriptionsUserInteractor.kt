package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs.ISubscriptionsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.UserRepository

class SubscriptionsUserInteractor(
    private val intent: Intent,
    private val userRepository: UserRepository
) :
    ISubscriptionsUserInteractor, UsersCallback {

    private lateinit var subscriptionsPresenterCallback: SubscriptionsPresenterCallback

    private var cacheUsers = mutableListOf<User>()
    private var subscriptionsCount = 0
    private var currentSubscriptionsCount = 0

    override fun getUsersLink() = cacheUsers

    override fun deleteUser(
        user: User,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    ) {
        cacheUsers.remove(user)
        subscriptionsPresenterCallback.showSubscriptions()
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

        for (subscription in subscriptions) {
            userRepository.getById(subscription.subscriptionId, this, true)
        }
    }

    override fun returnUsers(users: List<User>) {
        currentSubscriptionsCount++
        cacheUsers.addAll(users)
        if (currentSubscriptionsCount == subscriptionsCount) {
            if (cacheUsers.isEmpty()) {
                subscriptionsPresenterCallback.showEmptySubscriptions()
            } else {
                subscriptionsPresenterCallback.showSubscriptions()
            }
        }
    }

}
