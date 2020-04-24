package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenPresenterCallback

interface IMainScreenUserInteractor {
    fun getUserId():String
    fun getMainScreenDataByUserName(city:String, userName: String, mainScreenPresenterCallback: MainScreenPresenterCallback)
    fun getMainScreenDataByServiceName(city:String, serviceName: String, mainScreenPresenterCallback: MainScreenPresenterCallback)
    fun isSelectedCategory(category: String) : Boolean
    fun getUsersByCity(city:String)
    fun getCategories(mainScreenData: ArrayList<ArrayList<Any>>): MutableSet<String>
    fun convertCacheDataToMainScreenData(cacheMainScreenData: ArrayList<ArrayList<Any>>) : ArrayList<ArrayList<Any>>
    fun convertCacheDataToMainScreenData(category: String,cacheMainScreenData: ArrayList<ArrayList<Any>>) : ArrayList<ArrayList<Any>>
    fun convertCacheDataToMainScreenData(selectedTagsArray: ArrayList<String>, cacheMainScreenData: ArrayList<ArrayList<Any>>) : ArrayList<ArrayList<Any>>
}