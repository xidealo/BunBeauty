package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface IServiceRepository {
    fun insert(service: Service, insertServiceCallback: InsertServiceCallback)
    fun delete(service: Service, deleteServiceCallback: DeleteServiceCallback)
    fun update(service: Service, updateServiceCallback: UpdateServiceCallback)
    fun get(getServicesCallback: GetServicesCallback)

    fun getServicesByUserId(
        userId: String,
        getServicesCallback: GetServicesCallback,
        isFirstEnter: Boolean
    )

    fun getById(
        serviceId: String,
        userId: String,
        isFirstEnter: Boolean,
        getServiceCallback : GetServiceCallback
    )
}