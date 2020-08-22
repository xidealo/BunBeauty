package com.bunbeauty.ideal.myapplication.clean_architecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.SubscriptionAdapter.SubscriptionViewHolder
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.SubscriptionElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.SubscriptionsPresenter

class SubscriptionAdapter : RecyclerView.Adapter<SubscriptionViewHolder>() {

    private val subscriptions: ArrayList<Subscription> = arrayListOf()
    private lateinit var subscriptionsPresenter: SubscriptionsPresenter

    fun setData(subscriptionsPresenter: SubscriptionsPresenter) {
        this.subscriptionsPresenter = subscriptionsPresenter
    }

    fun addItem(subscription: Subscription) {
        val foundSubscription = subscriptions.find { it.id == subscription.id }
        if (foundSubscription == null) {
            subscriptions.add(subscription)
            notifyItemInserted(subscriptions.size)
        }
    }

    fun removeItem(subscription: Subscription){
        val foundSubscription = subscriptions.find { it.id == subscription.id }
        if (foundSubscription != null) {
            val index = subscriptions.indexOf(foundSubscription)
            subscriptions.remove(foundSubscription)
            notifyItemRemoved(index)
        }
    }

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
            val subscriptionElement =
                SubscriptionElement(view, context, subscriptionsPresenter, subscription)
            subscriptionElement.createElement()
        }
    }
}