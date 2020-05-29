package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.DeleteServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.InsertServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.ServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.UpdateServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface IServiceRepository {
    fun insert(service: Service, insertServiceCallback: InsertServiceCallback)
    fun delete(service: Service, deleteServiceCallback: DeleteServiceCallback)
    fun update(service: Service, updateServiceCallback: UpdateServiceCallback)
    fun get(servicesCallback: ServicesCallback)

    fun getServicesByUserId(
        userId: String,
        servicesCallback: ServicesCallback,
        isFirstEnter: Boolean
    )

    fun getById(
        serviceId: String,
        userId: String,
        isFirstEnter: Boolean,
        servicesCallback : ServicesCallback
    )
}