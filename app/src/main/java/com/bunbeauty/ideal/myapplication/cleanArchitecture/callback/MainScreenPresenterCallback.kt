package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

interface MainScreenPresenterCallback {
    fun returnMainScreenDataWithCreateCategory(mainScreenData:ArrayList<ArrayList<Any>>)
    fun returnMainScreenData(mainScreenData:ArrayList<ArrayList<Any>>)
    fun showLoading()
    fun createTags(category:String,selectedTagsArray:ArrayList<String>)

    fun getServicesByUserId(userId: String)
    fun getServicesByUserIdAndServiceName(userId: String, serviceName: String)
}