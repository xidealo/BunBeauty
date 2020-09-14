package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.profileElements.ProfileServiceElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

class ProfileServiceAdapter() :
    RecyclerView.Adapter<ProfileServiceAdapter.ProfileServiceViewHolder>(), Parcelable {

    private val serviceList = mutableListOf<Service>()

    constructor(parcel: Parcel) : this()

    fun updateItems(serviceList: List<Service>) {
        val newServiceList = serviceList.filter { service ->
            !this.serviceList.any { it.id == service.id }
        }
        for (newService in newServiceList) {
            this.serviceList.add(newService)
            this.serviceList.sortByDescending { it.creationDate }
            notifyItemInserted(0)
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
            ProfileServiceElement(service, context, view)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProfileServiceAdapter> {
        const val PROFILE_SERVICE_ADAPTER = "profile service adapter"

        override fun createFromParcel(parcel: Parcel): ProfileServiceAdapter {
            return ProfileServiceAdapter(parcel)
        }

        override fun newArray(size: Int): Array<ProfileServiceAdapter?> {
            return arrayOfNulls(size)
        }
    }
}
