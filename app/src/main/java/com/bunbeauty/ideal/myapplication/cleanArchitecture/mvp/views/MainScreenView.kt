package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import android.widget.Button
import com.arellomobile.mvp.MvpView

interface MainScreenView: MvpView {
    fun showTags()
    fun createCategoryFeed(categories: MutableSet<String>)
    fun disableCategoryBtn(button: Button)
    fun enableCategoryButton(category: String, button: Button)
    fun hideTags()
    fun showLoading()
    fun hideLoading()
    fun showMainScreen(mainScreenData:ArrayList<ArrayList<Any>>)
    fun createTags(category:String)
}