package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService.IMainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.GetServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IServiceRepository

class MainScreenServiceInteractor(private val serviceRepository: IServiceRepository) :
    GetServicesCallback, IMainScreenServiceInteractor {

    var cacheServiceList = arrayListOf<Service>()
    private var currentCountOfUsers = 0
    private lateinit var mainScreenPresenterCallback: MainScreenPresenterCallback

    override fun getServicesByUserId(
        user: User,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    ) {
        this.mainScreenPresenterCallback = mainScreenPresenterCallback
        serviceRepository.getByUserId(user.id, this, true)
    }

    override fun returnList(serviceList: List<Service>) {
        currentCountOfUsers++
        cacheServiceList.addAll(serviceList)

        if (currentCountOfUsers == mainScreenPresenterCallback.getUsersSize()) {
            if (cacheServiceList.isEmpty()) {
                mainScreenPresenterCallback.showEmptyScreen()
                return
            }

            mainScreenPresenterCallback.createMainScreenData(
                cacheServiceList,
                cacheServiceList.maxBy { it.cost }!!.cost,
                cacheServiceList.maxBy { it.countOfRates }!!.countOfRates
            )
        }
    }

    override fun getCategories(services: List<Service>): MutableSet<String> {
        val setOfCategories = mutableSetOf<String>()
        for (i in services.indices) {
            setOfCategories.add((services[i]).category)
        }
        return setOfCategories
    }
}