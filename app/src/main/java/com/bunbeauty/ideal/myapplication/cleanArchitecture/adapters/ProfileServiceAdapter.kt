package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.profileElements.ProfileServiceElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

class ProfileServiceAdapter :
    RecyclerView.Adapter<ProfileServiceAdapter.ProfileServiceViewHolder>() {

    private val serviceList: MutableList<Service> = ArrayList()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ProfileServiceViewHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        val view =
            layoutInflater.inflate(R.layout.element_profile_service, parent, false)

        return ProfileServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileServiceViewHolder, i: Int) {
        holder.bind(serviceList[i])
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    fun updateAdapter(serviceList: List<Service>) {
        this.serviceList.clear()
        this.serviceList.addAll(serviceList)
        notifyDataSetChanged()
    }

    inner class ProfileServiceViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(service: Service) {
            val profileServiceElement = ProfileServiceElement(service, context)
            profileServiceElement.createElement(view)
        }
    }
}
