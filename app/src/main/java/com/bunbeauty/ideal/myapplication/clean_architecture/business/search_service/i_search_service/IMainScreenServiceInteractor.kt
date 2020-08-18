package com.bunbeauty.ideal.myapplication.clean_architecture.business.search_service.i_search_service

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IMainScreenServiceInteractor {
    fun getServicesByUserId(user: User, mainScreenPresenterCallback: MainScreenPresenterCallback)
    fun getCategories(mainScreenData: List<Service>): MutableSet<String>
}