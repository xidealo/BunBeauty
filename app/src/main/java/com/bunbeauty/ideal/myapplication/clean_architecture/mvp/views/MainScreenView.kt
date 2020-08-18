package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views

import android.widget.Button
import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.MainScreenData
import com.google.android.material.chip.Chip

interface MainScreenView : MvpView {
    fun showEmptyScreen()
    fun createCategoryFeed(categories: MutableSet<String>)
    fun createMainScreen()
    fun disableCategoryBtn(button: Button)
    fun enableCategoryButton(button: Button)
    fun showTags()
    fun hideTags()
    fun clearTags()
    fun createTags(category: String, selectedTagsArray: ArrayList<String>)
    fun showLoading()
    fun hideLoading()
    fun showMainScreen(mainScreenData: ArrayList<MainScreenData>)
    fun enableTag(tagText: Chip)
    fun disableTag(tagText: Chip)
    fun hideCategory()
    fun showCategory()
}