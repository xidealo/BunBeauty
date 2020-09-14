package com.bunbeauty.ideal.myapplication.clean_architecture.domain.search_service.i_search_service

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.MainScreenPresenterCallback

interface IMainScreenUserInteractor {
    fun getUserId(): String
    fun getMainScreenDataByUserName(
        city: String,
        userName: String,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    )

    fun getMainScreenDataByServiceName(
        city: String,
        serviceName: String,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    )

    fun getUsersByCity(
        city: String, mainScreenPresenterCallback: MainScreenPresenterCallback
    )

    fun convertCacheDataToMainScreenData(
        category: String,
        cacheMainScreenData: ArrayList<ArrayList<Any>>
    ): ArrayList<ArrayList<Any>>

    fun convertCacheDataToMainScreenData(
        selectedTagsArray: ArrayList<String>,
        cacheMainScreenData: ArrayList<ArrayList<Any>>
    ): ArrayList<ArrayList<Any>>
}