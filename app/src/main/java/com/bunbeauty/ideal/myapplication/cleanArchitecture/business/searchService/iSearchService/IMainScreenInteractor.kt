package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenCallback

interface IMainScreenInteractor {
    fun getUserId():String
    fun getMainScreenData(mainScreenCallback: MainScreenCallback)
    fun getUsersByCity(city:String)
    fun getServicesByUserId(id:String)
    suspend fun setListenerCountOfReturnServices(countOfUsers:Int)
    fun getCategories(mainScreenData: ArrayList<ArrayList<Any>>): MutableSet<String>
}