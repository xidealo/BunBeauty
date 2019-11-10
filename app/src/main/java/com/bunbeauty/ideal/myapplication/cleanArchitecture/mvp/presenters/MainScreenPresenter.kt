package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.MainScreenView

@InjectViewState
class MainScreenPresenter(private val mainScreenInteractor: MainScreenInteractor) : MvpPresenter<MainScreenView>(),
        MainScreenCallback {

    fun createMainScreen() {
        viewState.showLoading()
        mainScreenInteractor.getMainScreenData(this)
    }
    fun createMainScreenWithCategory(category: String) {
        mainScreenInteractor.selectedTagsArray.clear()
        viewState.showLoading()
        viewState.hideTags()
        viewState.createTags(category,mainScreenInteractor.selectedTagsArray)
        viewState.showTags()

        mainScreenInteractor.getMainScreenData(category,this)
    }

    fun createMainScreenWithTag(tagText: TextView){
        if (mainScreenInteractor.selectedTagsArray.contains(tagText.text.toString())) {
            //disable
            viewState.disableTag(tagText)
            mainScreenInteractor.selectedTagsArray.remove(tagText.text.toString())
        } else {
            //enable
            viewState.enableTag(tagText)
            mainScreenInteractor.selectedTagsArray.add(tagText.text.toString())
        }

        if (mainScreenInteractor.selectedTagsArray.size == 0) {
            createMainScreenWithCategory(mainScreenInteractor.selectedCategory)
        }else{
            mainScreenInteractor.getMainScreenData(mainScreenInteractor.selectedTagsArray, this)
        }
        //create
    }
    override fun returnMainScreenDataWithCreateCategory(mainScreenData: ArrayList<ArrayList<Any>>) {
        viewState.hideLoading()
        viewState.showMainScreen(mainScreenData)
        viewState.createCategoryFeed(mainScreenInteractor.getCategories(mainScreenData))
    }

    override fun returnMainScreenData(mainScreenData: ArrayList<ArrayList<Any>>) {
        viewState.hideLoading()
        viewState.showMainScreen(mainScreenData)
    }

    fun isSelectedCategory(category: String): Boolean {
        if (category == mainScreenInteractor.selectedCategory) {
            mainScreenInteractor.selectedCategory = ""
            return true
        }
        mainScreenInteractor.selectedCategory = category
        return false
    }

    fun setTagsState(visibility: Int) {
        if (visibility == View.VISIBLE) {
            viewState.hideTags()
        } else {
            if(visibility != View.GONE){
                viewState.createTags(mainScreenInteractor.selectedCategory, mainScreenInteractor.selectedTagsArray)
                viewState.showTags()
            }
        }
    }

    fun disableCategoryBtns(categoriesBtns: ArrayList<Button>) {
        for (categoriesBtn in categoriesBtns) {
            viewState.disableCategoryBtn(categoriesBtn)
        }
    }

    fun clearCategory(categoriesBtns: ArrayList<Button>) {
        viewState.showLoading()
        mainScreenInteractor.selectedTagsArray.clear()
        for (btn in categoriesBtns) {
            if (mainScreenInteractor.selectedCategory == btn.text.toString()) {
                mainScreenInteractor.selectedCategory = ""
                viewState.disableCategoryBtn(btn)
                viewState.hideTags()
                break
            }
        }
    }
}