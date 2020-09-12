package com.bunbeauty.ideal.myapplication.clean_architecture.adapters

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.profileElements.ProfileOrderElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import java.io.Serializable

class ProfileOrderAdapter() : RecyclerView.Adapter<ProfileOrderAdapter.ProfileOrderViewHolder>(),
    Parcelable {

    lateinit var context: Context
    private val orderList: MutableList<Order> = ArrayList()

    constructor(parcel: Parcel) : this()

    fun updateItems(orderList: List<Order>) {
        val newOrderList = orderList.filter { order ->
            !this.orderList.any { it.id == order.id }
        }

        for (newOrder in newOrderList) {
            this.orderList.add(newOrder)
            this.orderList.sortBy { it.session.startTime }
            val newOrderIndex = this.orderList.indexOf(newOrder)
            notifyItemInserted(newOrderIndex)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileOrderViewHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.element_profile_order, parent, false)

        return ProfileOrderViewHolder(view)
    }

    override fun onBindViewHolder(holderOrder: ProfileOrderViewHolder, position: Int) {
        holderOrder.bind(orderList[position])
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    inner class ProfileOrderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(order: Order) {
            ProfileOrderElement(
                order,
                context,
                view
            )
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {}

    companion object CREATOR : Parcelable.Creator<ProfileOrderAdapter> {
        const val PROFILE_ORDER_ADAPTER = "profile order adapter"

        override fun createFromParcel(parcel: Parcel): ProfileOrderAdapter {
            return ProfileOrderAdapter(parcel)
        }

        override fun newArray(size: Int): Array<ProfileOrderAdapter?> {
            return arrayOfNulls(size)
        }
    }
}