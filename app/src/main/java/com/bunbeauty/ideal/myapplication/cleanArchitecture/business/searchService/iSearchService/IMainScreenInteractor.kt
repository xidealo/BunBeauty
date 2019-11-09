package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenCallback

interface IMainScreenInteractor {
    fun getUserId():String
    fun getMainScreenData(mainScreenCallback: MainScreenCallback)
    fun getMainScreenData(category: String, mainScreenCallback: MainScreenCallback)
    fun getMainScreenData(selectedTagsArray: ArrayList<String>, mainScreenCallback: MainScreenCallback)
    fun getUsersByCity(city:String)
    fun getServicesByUserId(id:String)
    fun getCategories(mainScreenData: ArrayList<ArrayList<Any>>): MutableSet<String>
    fun convertCacheDataToMainScreenData(cacheMainScreenData: ArrayList<ArrayList<Any>>) : ArrayList<ArrayList<Any>>
    fun convertCacheDataToMainScreenData(category: String,cacheMainScreenData: ArrayList<ArrayList<Any>>) : ArrayList<ArrayList<Any>>
    fun convertCacheDataToMainScreenData(selectedTagsArray: ArrayList<String>, cacheMainScreenData: ArrayList<ArrayList<Any>>) : ArrayList<ArrayList<Any>>

    suspend fun setListenerCountOfReturnServices(countOfUsers:Int)
}