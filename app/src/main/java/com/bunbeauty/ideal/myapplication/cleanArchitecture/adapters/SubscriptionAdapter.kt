package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.SubscriptionAdapter.SubscriptionViewHolder
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.SubscriptionElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

class SubscriptionAdapter(
    private val userList: List<User>
) : RecyclerView.Adapter<SubscriptionViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SubscriptionViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.subscription_element
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)
        return SubscriptionViewHolder(view, context)
    }

    override fun onBindViewHolder(
        subscriptionViewHolder: SubscriptionViewHolder,
        i: Int
    ) {
        subscriptionViewHolder.bind(userList[i])
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class SubscriptionViewHolder(private val view: View, private val context: Context) :
        RecyclerView.ViewHolder(view) {
        fun bind(user: User) {
            val subscriptionElement = SubscriptionElement(view, context)
            subscriptionElement.createElement(user)
        }
    }
}