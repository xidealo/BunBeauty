package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters

import android.view.View
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.search_service.MainScreenDataInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.search_service.MainScreenUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.search_service.i_search_service.IMainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.MainScreenView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip

@InjectViewState
class MainScreenPresenter(
    private val mainScreenUserInteractor: MainScreenUserInteractor,
    private val mainScreenServiceInteractor: IMainScreenServiceInteractor,
    private val mainScreenDataInteractor: MainScreenDataInteractor
) : MvpPresenter<MainScreenView>(), MainScreenPresenterCallback {

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

    fun createMainScreenWithTag(chip: Chip) {
        mainScreenDataInteractor.getMainScreenData(
            chip, mainScreenDataInteractor.selectedTagsArray, this
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

    fun disableCategoryButtons(categoriesButtonList: List<MaterialButton>) {
        for (categoriesBtn in categoriesButtonList) {
            viewState.disableCategoryBtn(categoriesBtn)
        }
    }

    fun getMainScreenDataByName(newText: String?) {
        mainScreenDataInteractor.getMainScreenDataByName(newText, this)
    }
}