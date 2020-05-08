package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.SubscriptionsPresenter
import com.bunbeauty.ideal.myapplication.helpApi.CircularTransformation
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

class SubscriptionElement(
    private val view: View,
    private val context: Context,
    private val subscriptionsPresenter: SubscriptionsPresenter
) : View.OnClickListener {

    private lateinit var nameText: TextView
    private lateinit var unsubscribeSubscriptionElementBtn: MaterialButton
    private lateinit var avatarImage: ImageView
    private lateinit var subscriptionElementLayout: LinearLayout
    private lateinit var subscription: Subscription

    fun createElement(subscription: Subscription) {
        onViewCreated(view)
        setData(subscription)
        showAvatar(subscription.subscriptionUser)
    }

    private fun onViewCreated(view: View) {
        nameText = view.findViewById(R.id.workerNameSubscriptionElementText)
        avatarImage =
            view.findViewById(R.id.avatarSubscriptionElementImage)
        unsubscribeSubscriptionElementBtn =
            view.findViewById(R.id.unsubscribeSubscriptionElementBtn)
        unsubscribeSubscriptionElementBtn.setOnClickListener(this)
        subscriptionElementLayout = view.findViewById(R.id.subscriptionElementLayout)
        subscriptionElementLayout.setOnClickListener(this)
    }

    private fun setData(subscription: Subscription) {
        this.subscription = subscription
        nameText.text =
            "${subscription.subscriptionUser.name} ${subscription.subscriptionUser.surname}"
    }

    private fun showAvatar(user: User) {
        val width = context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width)
        val height = context.resources.getDimensionPixelSize(R.dimen.photo_avatar_height)

        Picasso.get()
            .load(user.photoLink)
            .resize(width, height)
            .centerCrop()
            .transform(CircularTransformation())
            .into(avatarImage)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.unsubscribeSubscriptionElementBtn -> {
                subscriptionsPresenter.deleteSubscription(subscription)
                subscriptionsPresenter.deleteSubscriber(
                    Subscriber(
                        userId = subscription.subscriptionId,
                        subscriberId = subscription.userId
                    )
                )
                //subscriptionsPresenter.deleteUser(user)
            }

            R.id.subscriptionElementLayout -> goToProfile(subscription.subscriptionUser)
        }
    }

    private fun goToProfile(user: User) {
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra(User.USER, user)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(0, 0)
    }
}