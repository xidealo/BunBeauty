package com.bunbeauty.ideal.myapplication.clean_architecture.domain.search_service.i_search_service

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.google.android.material.chip.Chip

interface IMainScreenDataInteractor {
    fun createMainScreenData(
        cacheUserList: ArrayList<User>,
        cacheServiceList: ArrayList<Service>,
        maxCost: Long,
        maxCountOfRates: Long
    )

    fun getMainScreenData(mainScreenPresenterCallback: MainScreenPresenterCallback)

    fun showCurrentMainScreen(mainScreenPresenterCallback: MainScreenPresenterCallback)

    fun getMainScreenData(
        tagText: Chip,
        selectedTagsArray: ArrayList<String>,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    )

    fun getMainScreenData(
        category: String,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    )

    fun isSelectedCategory(category: String): Boolean
}