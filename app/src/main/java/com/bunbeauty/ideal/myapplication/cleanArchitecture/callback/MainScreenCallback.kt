package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

interface MainScreenCallback {
    fun getUsersByCity(user: User)
    fun getServicesByUserId(users: List<User>)
    fun returnServicesList(serviceList: List<Service>)
    suspend fun setListenerCountOfReturnServices(countOfUsers:Int)
}