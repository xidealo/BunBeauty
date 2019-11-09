package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import android.view.View
import android.widget.Button
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
            viewState.createTags(mainScreenInteractor.selectedCategory)
            viewState.showTags()
        }
    }

    fun disableCategoryBtns(categoriesBtns: ArrayList<Button>) {
        for (categoriesBtn in categoriesBtns) {
            viewState.disableCategoryBtn(categoriesBtn)
        }
    }

    fun createMainScreenWithCategory(category: String, button: Button) {
        viewState.showLoading()
        viewState.hideTags()
        viewState.enableCategoryButton(button)
        //nah hide?
        viewState.createTags(category)
        viewState.showTags()

        mainScreenInteractor.getMainScreenData(category,this)
    }

    fun clearCategory(category: String, categoriesBtns: ArrayList<Button>) {
        for (btn in categoriesBtns) {
            if (category == btn.text.toString()) {
                mainScreenInteractor.selectedCategory = ""
                viewState.disableCategoryBtn(btn)
                viewState.hideTags()
                break
            }
        }
    }

}