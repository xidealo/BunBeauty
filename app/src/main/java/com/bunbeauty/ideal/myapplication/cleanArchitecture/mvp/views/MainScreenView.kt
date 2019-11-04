package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import android.widget.Button
import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

interface MainScreenView: MvpView {
    fun showTags()
    fun createCategoryFeed()
    fun disableCategoryBtn(button: Button)
    fun enableCategory(button: Button)
    fun hideTags()
    fun showLoading()
    fun hideLoading()
    fun showMainScreen(serviceList: ArrayList<Service>, userList: ArrayList<User>)
    fun createTags()
    fun clearCategory()
}