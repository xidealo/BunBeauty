package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenPresenterCallback

interface IMainScreenDataInteractor {
    fun createMainScreenData()
    fun getMainScreenData(mainScreenPresenterCallback: MainScreenPresenterCallback)
    fun getMainScreenData(
        selectedTagsArray: ArrayList<String>,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    )
    fun getMainScreenData(
        category: String,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    )
}