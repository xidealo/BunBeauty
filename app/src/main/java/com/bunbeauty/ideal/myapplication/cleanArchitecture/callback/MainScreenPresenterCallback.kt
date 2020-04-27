package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import android.widget.TextView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface MainScreenPresenterCallback {
    fun showMainScreenData(mainScreenData: ArrayList<MainScreenData>)
    fun showEmptyScreen()
    fun createCategoryFeed(mainScreenData: List<Service>)
    fun returnMainScreenData(mainScreenData: ArrayList<MainScreenData>)
    fun showLoading()
    fun showTags()
    fun hideTags()
    fun clearTags()
    fun createTags(category:String,selectedTagsArray:ArrayList<String>)
    fun getServicesByUserId(user: User)
    fun getServicesByUserIdAndServiceName(userId: String, serviceName: String)
    fun getUsersByCity(city: String)
    fun getUsersSize(): Int
    fun getMaxCost(): Long
    fun getMaxCountOfRates(): Long
    fun createMainScreenData()
    fun enableTag(tagText: TextView)
    fun disableTag(tagText: TextView)
}