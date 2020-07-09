package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.foundElements.FoundServiceElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import java.util.*

class ServiceAdapter(private val mainScreenData: ArrayList<MainScreenData>) :
    RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ServiceViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.found_service_element
        //Класс, который позволяет создавать представления из xml файла
        val layoutInflater = LayoutInflater.from(context)
        // откуда, куда, необходимо ли помещать в родителя
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
            val foundServiceElement = FoundServiceElement(service, user, view, context)
            foundServiceElement.createElement()
        }
    }
}
