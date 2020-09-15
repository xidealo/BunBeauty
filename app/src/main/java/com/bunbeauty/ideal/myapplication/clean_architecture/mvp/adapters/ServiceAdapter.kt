package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.ServiceElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

class ServiceAdapter : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    private var mainScreenDataList = mutableListOf<MainScreenData>()
    private lateinit var diffUtil: DiffUtil.DiffResult

    fun setData(mainScreenData: ArrayList<MainScreenData>) {
        this.mainScreenDataList.clear()
        this.mainScreenDataList.addAll(mainScreenData)
        notifyDataSetChanged()
    }

    /*fun setData(mainScreenData: ArrayList<MainScreenData>) {
        diffUtil = DiffUtil.calculateDiff(DiffUtilCallback(mainScreenDataList, mainScreenData))
        diffUtil.dispatchUpdatesTo(this)
        mainScreenDataList = mainScreenData
    }*/

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ServiceViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.element_service
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)

        return ServiceViewHolder(view, context)
    }

    override fun onBindViewHolder(serviceViewHolder: ServiceViewHolder, i: Int) {
        serviceViewHolder.bind(
            mainScreenDataList[i].service,
            mainScreenDataList[i].user,
            mainScreenDataList[i].weight
        )
    }

    override fun getItemCount() = mainScreenDataList.size

    inner class ServiceViewHolder(private val view: View, private val context: Context) :
        RecyclerView.ViewHolder(view) {

        fun bind(service: Service, user: User, weight: Float) {
            ServiceElement(service, user, weight, view, context)
        }
    }


}
