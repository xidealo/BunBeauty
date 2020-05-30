package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.SubscriptionAdapter.SubscriptionViewHolder
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.SubscriptionElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.SubscriptionsPresenter

class SubscriptionAdapter(
    private val subscriptionsPresenter: SubscriptionsPresenter,
    private val subscriptions: List<Subscription>
) : RecyclerView.Adapter<SubscriptionViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SubscriptionViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.element_subscription
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)
        return SubscriptionViewHolder(view, context)
    }

    override fun onBindViewHolder(
        subscriptionViewHolder: SubscriptionViewHolder,
        i: Int
    ) {
        subscriptionViewHolder.bind(subscriptions[i])
    }

    override fun getItemCount(): Int {
        return subscriptions.size
    }

    inner class SubscriptionViewHolder(private val view: View, private val context: Context) :
        RecyclerView.ViewHolder(view) {

        fun bind(subscription: Subscription) {
            val subscriptionElement = SubscriptionElement(view, context, subscriptionsPresenter, subscription)
            subscriptionElement.createElement()
        }
    }
}