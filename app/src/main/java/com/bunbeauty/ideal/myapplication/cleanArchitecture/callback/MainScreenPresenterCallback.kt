package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface MainScreenPresenterCallback {
    fun showMainScreenData(mainScreenData: ArrayList<ArrayList<MainScreenData>>)
    fun createCategoryFeed(mainScreenData: ArrayList<ArrayList<MainScreenData>>)
    fun returnMainScreenData(mainScreenData: ArrayList<ArrayList<MainScreenData>>)
    fun showLoading()
    fun createTags(category: String, selectedTagsArray: ArrayList<String>)

    fun getServicesByUserId(user: User)
    fun getServicesByUserIdAndServiceName(userId: String, serviceName: String)
    fun getUsersByCity(city: String)
    fun getUsersSize(): Int
    fun createMainScreenData()
}