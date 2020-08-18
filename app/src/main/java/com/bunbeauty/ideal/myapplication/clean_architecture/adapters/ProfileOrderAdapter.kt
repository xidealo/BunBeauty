package com.bunbeauty.ideal.myapplication.clean_architecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.profileElements.ProfileOrderElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order

class ProfileOrderAdapter() : RecyclerView.Adapter<ProfileOrderAdapter.ProfileOrderViewHolder>() {

    lateinit var context: Context
    private val orderList: MutableList<Order> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileOrderViewHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.element_order, parent, false)

        return ProfileOrderViewHolder(view)
    }

    override fun onBindViewHolder(holderOrder: ProfileOrderViewHolder, position: Int) {
        holderOrder.bind(orderList[position])
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    fun updateAdapter(orderList: List<Order>) {
        this.orderList.clear()
        this.orderList.addAll(orderList)
        notifyDataSetChanged()
    }


    inner class ProfileOrderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(order: Order) {
            val orderElement =
                ProfileOrderElement(
                    order,
                    context
                )
            orderElement.createElement(view)
        }
    }
}