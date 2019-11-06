package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import android.view.View
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.MainScreenView

@InjectViewState
class MainScreenPresenter(private val mainScreenInteractor: MainScreenInteractor) : MvpPresenter<MainScreenView>(),
        MainScreenCallback {

    fun createMainScreen() {
        mainScreenInteractor.getMainScreenData(this)
    }

    override fun returnMainScreenData(mainScreenData: ArrayList<ArrayList<Any>>) {
        viewState.hideLoading()
        viewState.showMainScreen(mainScreenData)
        viewState.createCategoryFeed(mainScreenInteractor.getCategories(mainScreenData))
    }

    fun categoriesClick(category: String) : String {
        // Если категория уже выбрана
        if (category == mainScreenInteractor.selectedCategory) {

        } else {
            viewState.showLoading()
            viewState.enableCategory(btn)
            //nah hide?
            viewState.hideTags()
            viewState.createTags()
            viewState.showTags()
            createMainScreenWithCategory()
        }
        return category
    }

    fun isSelectedCategory(category: String) : Boolean {
        if (category == mainScreenInteractor.selectedCategory) {
            return true
        }
        mainScreenInteractor.selectedCategory = category
        return false
    }
    fun setTagsState(visibility: Int){
        if (visibility == View.VISIBLE) {
            viewState.hideTags()
        } else {
            viewState.createTags()
            viewState.showTags()
        }
    }
    fun createMainScreenWithCategory(category: String) {

    }

}