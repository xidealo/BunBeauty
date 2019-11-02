package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IServiceSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service

interface IServiceRepository {
    fun insert(service: Service)
    fun delete(service: Service)
    fun update(service: Service)
    fun get()

    fun getServicesByCityActual(userCity: String)
    fun getServicesByCityAndCategory(userCity: String, category: String, selectedTagsArray: java.util.ArrayList<String>?)
    fun getServicesByUserId(userId: String, serviceSubscriber: IServiceSubscriber, isFirstEnter: Boolean)
}