package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenCallback

interface IMainScreenInteractor {
    fun getUserId():String
    fun getMyUser(mainScreenCallback: MainScreenCallback)
    fun getUsersByCity(city:String, mainScreenCallback: MainScreenCallback)
    fun getServicesByUserId(id:String, mainScreenCallback: MainScreenCallback)
}