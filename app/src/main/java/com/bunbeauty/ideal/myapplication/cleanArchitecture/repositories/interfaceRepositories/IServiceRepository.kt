package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface IServiceRepository {
    fun insert(service: Service)
    fun delete(service: Service)
    fun update(service: Service)
    fun get()

    fun getServicesByCityAndCategory(userCity: String, category: String, selectedTagsArray: java.util.ArrayList<String>?)
    fun getServicesByUserId(userId: String, serviceSubscriber: IServiceCallback, isFirstEnter: Boolean)

    fun getById(serviceId: String, userId: String, serviceSubscriber: IServiceCallback)
    fun getServicesByUserIdAndServiceName(userId: String, serviceName: String, serviceSubscriber: IServiceCallback, isFirstEnter: Boolean)
}