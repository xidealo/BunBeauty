package com.bunbeauty.ideal.myapplication.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.adapters.foundElements.FoundServiceProfileElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

import java.util.ArrayList

class ServiceProfileAdapter(private val serviceList: ArrayList<Service>, private val serviceOwner: User)
    : RecyclerView.Adapter<ServiceProfileAdapter.ServiceProfileViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ServiceProfileViewHolder {
        context = viewGroup.context

        //This class allows you to create views from XML file
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val view = layoutInflater.inflate(R.layout.found_service_profile_element,
                viewGroup, false)
        return ServiceProfileViewHolder(view)
    }

    override fun onBindViewHolder(serviceProfileViewHolder: ServiceProfileViewHolder, i: Int) {
        serviceProfileViewHolder.bind(serviceList[i])
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    inner class ServiceProfileViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(service: Service) {
            val foundServiceProfileElement = FoundServiceProfileElement(service, serviceOwner, context)
            foundServiceProfileElement.createElement(view)
        }
    }

    companion object {
        private val TAG = "DBInf"
    }
}
