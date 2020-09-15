package com.bunbeauty.ideal.myapplication.clean_architecture.domain.search_service

import com.bunbeauty.ideal.myapplication.clean_architecture.domain.search_service.i_search_service.IMainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.GetServicesCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceRepository

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
                cacheServiceList.maxByOrNull { it.cost }?.cost ?: 0,
                cacheServiceList.maxByOrNull { it.countOfRates }?.countOfRates ?: 0
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