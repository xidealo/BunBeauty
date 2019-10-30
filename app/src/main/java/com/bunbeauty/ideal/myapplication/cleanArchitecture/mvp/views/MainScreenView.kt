package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import android.widget.Button
import com.arellomobile.mvp.MvpView

interface MainScreenView: MvpView {
    fun showTags()
    fun createCategoryFeed()
    fun disableCategoryBtn(button: Button)
    fun enableCategory(button: Button)
    fun hideTags()
    fun startLoading()
}