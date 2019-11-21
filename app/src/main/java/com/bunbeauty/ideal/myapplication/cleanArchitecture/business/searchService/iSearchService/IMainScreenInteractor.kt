package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IMainScreenCallback

interface IMainScreenInteractor {
    fun getUserId():String
    fun getMainScreenData(mainScreenCallback: IMainScreenCallback)
    fun getMainScreenData(category: String, mainScreenCallback: IMainScreenCallback)
    fun getMainScreenDataByUserName(city:String, userName: String, mainScreenCallback: IMainScreenCallback)
    fun getMainScreenDataByServiceName(city:String, serviceName: String, mainScreenCallback: IMainScreenCallback)
    fun getMainScreenData(selectedTagsArray: ArrayList<String>, mainScreenCallback: IMainScreenCallback)

    fun getUsersByCity(city:String)
    fun getServicesByUserId(id:String)
    fun getServicesByUserIdAndServiceName(id: String, serviceName: String)
    fun getCategories(mainScreenData: ArrayList<ArrayList<Any>>): MutableSet<String>
    fun convertCacheDataToMainScreenData(cacheMainScreenData: ArrayList<ArrayList<Any>>) : ArrayList<ArrayList<Any>>
    fun convertCacheDataToMainScreenData(category: String,cacheMainScreenData: ArrayList<ArrayList<Any>>) : ArrayList<ArrayList<Any>>
    fun convertCacheDataToMainScreenData(selectedTagsArray: ArrayList<String>, cacheMainScreenData: ArrayList<ArrayList<Any>>) : ArrayList<ArrayList<Any>>

    suspend fun setListenerCountOfReturnServices(countOfUsers:Int)
}