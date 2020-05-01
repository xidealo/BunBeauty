package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.foundElements.FoundServiceProfileElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

class ServiceProfileAdapter(private val serviceList: MutableList<Service>, private val user: User) :
    RecyclerView.Adapter<ServiceProfileAdapter.ServiceProfileViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ServiceProfileViewHolder {
        context = viewGroup.context
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val view = layoutInflater.inflate(
            R.layout.found_service_profile_element,
            viewGroup, false
        )
        return ServiceProfileViewHolder(view)
    }

    override fun onBindViewHolder(serviceProfileViewHolder: ServiceProfileViewHolder, i: Int) {
        serviceProfileViewHolder.bind(serviceList[i], user)
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    inner class ServiceProfileViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(service: Service, user: User) {
            val foundServiceProfileElement = FoundServiceProfileElement(service, user, context)
            foundServiceProfileElement.createElement(view)
        }
    }

    companion object {
        private val TAG = "DBInf"
    }
}
