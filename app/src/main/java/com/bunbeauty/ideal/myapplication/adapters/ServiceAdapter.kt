package com.bunbeauty.ideal.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.adapters.foundElements.FoundServiceElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import java.util.*

class ServiceAdapter(private val numberItems: Int, private val mainScreenData:ArrayList<ArrayList<Any>>) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    private var context: Context? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ServiceViewHolder {
        context = viewGroup.context
        val layoutIdForListItem = R.layout.found_service_element
        //Класс, который позволяет создавать представления из xml файла
        val layoutInflater = LayoutInflater.from(context)
        // откуда, куда, необходимо ли помещать в родителя
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)

        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(serviceViewHolder: ServiceViewHolder, i: Int) {
        serviceViewHolder.bind(mainScreenData[i][0] as Service, mainScreenData[i][1]as User)
    }

    override fun getItemCount(): Int {
        return numberItems
    }

    inner class ServiceViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(service: Service, user: User) {
            val foundServiceElement = FoundServiceElement(service, user, view, context)
            foundServiceElement.createElement()
        }
    }

    companion object {

        private val TAG = "DBInf"
    }
}
