package com.bunbeauty.ideal.myapplication.clean_architecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.ServiceElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

class ServiceAdapter : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    private val mainScreenData: ArrayList<MainScreenData> = arrayListOf()

    fun setData(mainScreenData: ArrayList<MainScreenData>) {
        this.mainScreenData.clear()
        this.mainScreenData.addAll(mainScreenData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ServiceViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.element_service
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)

        return ServiceViewHolder(view, context)
    }

    override fun onBindViewHolder(serviceViewHolder: ServiceViewHolder, i: Int) {
        serviceViewHolder.bind(mainScreenData[i].service, mainScreenData[i].user)
    }

    override fun getItemCount(): Int {
        return mainScreenData.size
    }

    inner class ServiceViewHolder(private val view: View, private val context: Context) :
        RecyclerView.ViewHolder(view) {

        fun bind(service: Service, user: User) {
            val serviceElement = ServiceElement(service, user, view, context)
            serviceElement.createElement()
        }
    }


}
