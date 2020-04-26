package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IMainScreenServiceInteractor {
    fun getServicesByUserId(user: User, mainScreenPresenterCallback: MainScreenPresenterCallback)
    fun getServicesByUserIdAndServiceName(userId: String, serviceName: String)
    fun getCategories(mainScreenData: ArrayList<ArrayList<MainScreenData>>): MutableSet<String>
}