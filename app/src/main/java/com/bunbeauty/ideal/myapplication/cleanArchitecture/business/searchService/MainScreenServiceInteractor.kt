package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService.IMainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.IServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository

class MainScreenServiceInteractor(private val serviceRepository: ServiceRepository) :
    IServicesCallback, IMainScreenServiceInteractor {

    private var cacheServiceList = arrayListOf<Service>()
    private var createMainScreenWithCategory = true

   override fun getServicesByUserIdAndServiceName(userId: String, serviceName: String) {
        serviceRepository.getServicesByUserIdAndServiceName(userId, serviceName, this, true)
    }

   override fun getServicesByUserId(userId: String) {
        serviceRepository.getServicesByUserId(userId, this, true)
    }

    override fun returnServices(serviceList: List<Service>) {
        /*currentCountOfUsers++
        cacheServiceList.addAll(serviceList)

        if (currentCountOfUsers == countOfUsers) {
            createMainScreenData()
        }*/
    }

}