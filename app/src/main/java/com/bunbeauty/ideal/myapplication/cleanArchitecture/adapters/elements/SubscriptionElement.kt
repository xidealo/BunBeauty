package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.SubscriptionsPresenter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.subscription_element.view.*

class SubscriptionElement(
    private val view: View,
    private val context: Context,
    private val subscriptionsPresenter: SubscriptionsPresenter,
    private val subscription: Subscription
) {

    fun createElement() {
        view.workerNameSubscriptionElementText.text =
            "${subscription.subscriptionUser.name} ${subscription.subscriptionUser.surname}"
        showAvatar(subscription.subscriptionUser, view)

        view.subscriptionElementLayout.setOnClickListener { goToProfile(subscription.subscriptionUser) }

        view.subscribeSubscriptionElementCheckBox.isChecked = true
        view.subscribeSubscriptionElementCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                unsubscribe()
            }
        }
    }

    private fun showAvatar(user: User, view: View) {
        val width = context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width)
        val height = context.resources.getDimensionPixelSize(R.dimen.photo_avatar_height)

        Picasso.get()
            .load(user.photoLink)
            .resize(width, height)
            .centerCrop()
            .transform(CircularTransformation())
            .into(view.avatarSubscriptionElementImage)
    }

    private fun unsubscribe() {
        subscriptionsPresenter.deleteSubscription(subscription)
        subscriptionsPresenter.deleteSubscriber(Subscriber(
                userId = subscription.subscriptionId,
                subscriberId = subscription.userId
            )
        )
        //subscriptionsPresenter.deleteUser(user)
    }

    private fun goToProfile(user: User) {
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra(User.USER, user)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(0, 0)
    }
}