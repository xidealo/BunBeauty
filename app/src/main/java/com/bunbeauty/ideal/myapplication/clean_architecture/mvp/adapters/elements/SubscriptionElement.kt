package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.CircularTransformation
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscriber
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.SubscriptionsPresenter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.element_subscription.view.*

class SubscriptionElement(
    view: View,
    context: Context,
    subscriptionsPresenter: SubscriptionsPresenter,
    subscription: Subscription
) {
    init {
        view.workerNameSubscriptionElementText.text =
            "${subscription.subscriptionUser.name} ${subscription.subscriptionUser.surname}"
        showAvatar(subscription.subscriptionUser, view, context)

        view.subscriptionElementLayout.setOnClickListener {
            goToProfile(subscription.subscriptionUser, context)
        }

        view.subscribeSubscriptionElementCheckBox.isChecked = true
        view.subscribeSubscriptionElementCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                unsubscribe(subscriptionsPresenter, subscription)
            }
        }
    }

    private fun showAvatar(user: User, view: View, context: Context) {
        val width = context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width)
        val height = context.resources.getDimensionPixelSize(R.dimen.photo_avatar_height)

        Picasso.get()
            .load(user.photoLink)
            .resize(width, height)
            .centerCrop()
            .transform(CircularTransformation())
            .into(view.avatarSubscriptionElementImage)
    }

    private fun unsubscribe(
        subscriptionsPresenter: SubscriptionsPresenter,
        subscription: Subscription
    ) {
        subscriptionsPresenter.deleteSubscription(subscription)
        subscriptionsPresenter.deleteSubscriber(
            Subscriber(
                userId = subscription.subscriptionId,
                subscriberId = subscription.userId
            )
        )
    }

    private fun goToProfile(user: User, context: Context) {
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra(User.USER, user)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(0, 0)
    }
}