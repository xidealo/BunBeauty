package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService.IMainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.IServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository

class MainScreenServiceInteractor(private val serviceRepository: ServiceRepository) :
    IServicesCallback, IMainScreenServiceInteractor {

    var cacheServiceList = arrayListOf<Service>()
    private var currentCountOfUsers = 0
    private lateinit var mainScreenPresenterCallback: MainScreenPresenterCallback

    override fun getServicesByUserIdAndServiceName(userId: String, serviceName: String) {
        serviceRepository.getServicesByUserIdAndServiceName(userId, serviceName, this, true)
    }

    override fun getServicesByUserId(
        user: User,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    ) {
        this.mainScreenPresenterCallback = mainScreenPresenterCallback
        serviceRepository.getServicesByUserId(user.id, this, true)
    }

    override fun returnServices(serviceList: List<Service>) {
        currentCountOfUsers++
        cacheServiceList.addAll(serviceList)

        if (currentCountOfUsers == mainScreenPresenterCallback.getUsersSize()) {
            mainScreenPresenterCallback.createMainScreenData()
        }
    }

    override fun getCategories(mainScreenData: ArrayList<ArrayList<MainScreenData>>): MutableSet<String> {
        val setOfCategories = mutableSetOf<String>()
        for (i in mainScreenData.indices) {
            setOfCategories.add((mainScreenData[i][0].service).category)
        }
        return setOfCategories
    }
}