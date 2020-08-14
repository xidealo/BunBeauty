package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import android.view.View
import android.widget.TextView
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenDataInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService.IMainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.MainScreenView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip

@InjectViewState
class MainScreenPresenter(
    private val mainScreenUserInteractor: MainScreenUserInteractor,
    private val mainScreenServiceInteractor: IMainScreenServiceInteractor,
    private val mainScreenDataInteractor: MainScreenDataInteractor
) : MvpPresenter<MainScreenView>(),
    MainScreenPresenterCallback {

    fun getMainScreenDataLink() = mainScreenDataInteractor.cacheMainScreenData

    fun createMainScreen() {
        mainScreenDataInteractor.getMainScreenData(this)
    }

    fun showCurrentMainScreen() {
        mainScreenDataInteractor.showCurrentMainScreen(this)
    }

    override fun getUsersByCity(city: String) {
        mainScreenUserInteractor.getUsersByCity(city, this)
    }

    fun createMainScreenWithCategory(category: String) {
        mainScreenDataInteractor.getMainScreenData(category, this)
    }

    fun createMainScreenWithTag(tagText: Chip) {
        mainScreenDataInteractor.getMainScreenData(
            tagText, mainScreenDataInteractor.selectedTagsArray, this
        )
    }

    override fun getUsersSize(): Int = mainScreenUserInteractor.cacheUserList.size

    override fun createMainScreenData(
        services: ArrayList<Service>,
        maxCost: Long,
        maxCountOfRates: Long
    ) {
        mainScreenDataInteractor.createMainScreenData(
            mainScreenUserInteractor.cacheUserList,
            services,
            maxCost,
            maxCountOfRates
        )
    }

    override fun enableTag(tagText: Chip) {
        viewState.enableTag(tagText)
    }

    override fun disableTag(tagText: Chip) {
        viewState.disableTag(tagText)
    }

    override fun showMainScreenData(mainScreenData: ArrayList<MainScreenData>) {
        viewState.hideLoading()
        viewState.showMainScreen(mainScreenData)
    }

    override fun showEmptyScreen() {
        viewState.hideLoading()
        viewState.showEmptyScreen()
    }

    override fun createCategoryFeed(services: List<Service>) {
        viewState.createCategoryFeed(mainScreenServiceInteractor.getCategories(services))
    }

    override fun returnMainScreenData(mainScreenData: ArrayList<MainScreenData>) {
        viewState.hideLoading()
        viewState.showMainScreen(mainScreenData)
    }

    override fun showLoading() {
        viewState.showLoading()
    }

    override fun showTags() {
        viewState.showTags()
    }

    override fun hideTags() {
        viewState.hideTags()
    }

    override fun clearTags() {
        viewState.clearTags()
    }

    override fun createTags(category: String, selectedTagsArray: ArrayList<String>) {
        viewState.createTags(category, selectedTagsArray)
    }

    override fun getServicesByUserId(user: User) {
        mainScreenServiceInteractor.getServicesByUserId(user, this)
    }

    fun isSelectedCategory(category: String): Boolean =
        mainScreenDataInteractor.isSelectedCategory(category)

    fun setTagsState(visibility: Int) {
        if (visibility == View.VISIBLE) {
            viewState.hideTags()
        } else {
            if (visibility != View.GONE) {
                viewState.createTags(
                    mainScreenDataInteractor.selectedCategory,
                    mainScreenDataInteractor.selectedTagsArray
                )
                viewState.showTags()
            }
        }
    }

    fun disableCategoryBtns(categoriesBtns: ArrayList<MaterialButton>) {
        for (categoriesBtn in categoriesBtns) {
            viewState.disableCategoryBtn(categoriesBtn)
        }
    }

    fun clearCategory(categoriesBtns: ArrayList<MaterialButton>) {
        viewState.showLoading()
        mainScreenDataInteractor.selectedTagsArray.clear()
        for (btn in categoriesBtns) {
            if (mainScreenDataInteractor.selectedCategory == btn.text.toString()) {
                mainScreenDataInteractor.selectedCategory = ""
                viewState.disableCategoryBtn(btn)
                viewState.hideTags()
                break
            }
        }
    }

    fun getMainScreenDataByName(newText: String?) {
        mainScreenDataInteractor.getMainScreenDataByName(newText, this)
    }
}