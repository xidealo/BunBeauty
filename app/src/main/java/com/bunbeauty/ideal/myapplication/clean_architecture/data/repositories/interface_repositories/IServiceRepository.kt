package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

interface IServiceRepository {
    fun insert(service: Service, insertServiceCallback: InsertServiceCallback)
    fun delete(service: Service, deleteServiceCallback: DeleteServiceCallback)
    fun update(service: Service, updateServiceCallback: UpdateServiceCallback)
    fun updatePremium(
        service: Service,
        durationPremium: Long,
        updateServiceCallback: UpdateServiceCallback
    )

    fun get(getServicesCallback: GetServicesCallback)

    fun getByUserId(
        userId: String,
        getServicesCallback: GetServicesCallback,
        isFirstEnter: Boolean
    )

    fun getById(
        serviceId: String,
        userId: String,
        isFirstEnter: Boolean,
        getServiceCallback: GetServiceCallback
    )
}