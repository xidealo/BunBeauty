package com.bunbeauty.ideal.myapplication.clean_architecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.profileElements.ProfileServiceElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription

class ProfileServiceAdapter :
    RecyclerView.Adapter<ProfileServiceAdapter.ProfileServiceViewHolder>() {

    private val serviceList = mutableListOf<Service>()

    fun addItem(service: Service) {
        val foundService = serviceList.find { it.id == service.id }
        if (foundService == null) {
            serviceList.add(service)
            serviceList.sortByDescending { it.creationDate }
            notifyItemInserted(serviceList.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ProfileServiceViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        val view =
            layoutInflater.inflate(R.layout.element_profile_service, parent, false)

        return ProfileServiceViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: ProfileServiceViewHolder, i: Int) {
        holder.bind(serviceList[i])
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    inner class ProfileServiceViewHolder(private val view: View, private val context: Context) :
        RecyclerView.ViewHolder(view) {

        fun bind(service: Service) {
            val profileServiceElement = ProfileServiceElement(service, context)
            profileServiceElement.createElement(view)
        }
    }
}
