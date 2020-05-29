package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService.IMainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.ServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IServiceRepository

class MainScreenServiceInteractor(private val serviceRepository: IServiceRepository) :
    ServicesCallback, IMainScreenServiceInteractor {

    var cacheServiceList = arrayListOf<Service>()
    var maxCost = 0L
    var maxCountOfRates = 0L

    private var currentCountOfUsers = 0
    private lateinit var mainScreenPresenterCallback: MainScreenPresenterCallback

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
            if (cacheServiceList.isEmpty()) {
                mainScreenPresenterCallback.showEmptyScreen()
                return
            }

            if (cacheServiceList.isNotEmpty()) {
                maxCost = cacheServiceList.maxBy { it.cost }!!.cost
                maxCountOfRates = cacheServiceList.maxBy { it.countOfRates }!!.countOfRates
            }
            mainScreenPresenterCallback.createMainScreenData()
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