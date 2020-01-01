package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface IServiceRepository {
    fun insert(service: Service,iInsertServiceCallback: IInsertServiceCallback)
    fun delete(service: Service, iDeleteServiceCallback: IDeleteServiceCallback)
    fun update(service: Service, iUpdateServiceCallback: IUpdateServiceCallback)
    fun get(iServicesCallback: IServicesCallback)
    fun insertInRoom(service: Service)

    fun getServicesByCityAndCategory(userCity: String, category: String, selectedTagsArray: ArrayList<String>)
    fun getServicesByUserId(userId: String, iServicesCallback: IServicesCallback, isFirstEnter: Boolean)

    fun getById(serviceId: String, userId: String, iServiceCallback: IServiceCallback, isFirstEnter: Boolean)
    fun getServicesByUserIdAndServiceName(userId: String, serviceName: String,
                                          iServicesCallback: IServicesCallback,
                                          isFirstEnter: Boolean)
}