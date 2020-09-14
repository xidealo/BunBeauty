package com.bunbeauty.ideal.myapplication.clean_architecture.domain.subs

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.subs.iSubs.ISubscriptionsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UpdateUsersCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.UserRepository

class SubscriptionsUserInteractor(
    private val intent: Intent,
    private val userRepository: UserRepository
) : ISubscriptionsUserInteractor, UserCallback, UpdateUsersCallback {

    private lateinit var subscriptionsPresenterCallback: SubscriptionsPresenterCallback

    private var cacheUsers = mutableListOf<User>()

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

    override fun createSubscriptionScreen(
        loadingLimit: Int,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    ) {
        val user = intent.getSerializableExtra(User.USER) as User
        subscriptionsPresenterCallback.getSubscriptions(user, loadingLimit)
    }

    override fun getUsersBySubscription(
        subscription: Subscription,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    ) {
        this.subscriptionsPresenterCallback = subscriptionsPresenterCallback
        userRepository.getById(subscription.subscriptionId, this, true)
    }

    override fun returnGottenObject(element: User?) {
        if (element == null) return
        cacheUsers.add(element)
        subscriptionsPresenterCallback.fillSubscription(element)
    }

    override fun returnUpdatedCallback(obj: User) {
        //subscriptionsPresenterCallback.showSubscriptions()
    }

}
