package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import android.widget.Button
import android.widget.TextView
import com.arellomobile.mvp.MvpView

interface MainScreenView: MvpView {
    fun showTags()
    fun showMainScreenByUserName(city:String, name:String)
    fun showMainScreenByServiceName(city:String, serviceName:String)

    fun createCategoryFeed(categories: MutableSet<String>)
    fun createMainScreen()
    fun disableCategoryBtn(button: Button)
    fun enableCategoryButton(button: Button)
    fun hideTags()
    fun showLoading()
    fun hideLoading()
    fun showMainScreen(mainScreenData:ArrayList<ArrayList<Any>>)
    fun createTags(category:String,selectedTagsArray:ArrayList<String>)
    fun enableTag(tagText: TextView)
    fun disableTag(tagText: TextView)
    fun createSearchPanel()
    fun showSearchPanel()
    fun hideSearchPanel()
    fun hideCategory()
    fun showCategory()
}