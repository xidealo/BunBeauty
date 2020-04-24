package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenDataInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.MainScreenView

@InjectViewState
class MainScreenPresenter(
    private val mainScreenUserInteractor: MainScreenUserInteractor,
    private val mainScreenServiceInteractor: MainScreenServiceInteractor,
    private val mainScreenDataInteractor: MainScreenDataInteractor
) : MvpPresenter<MainScreenView>(),
    MainScreenPresenterCallback {

    fun createMainScreen() {
        mainScreenDataInteractor.getMainScreenData(this)
    }

    fun createMainScreenWithCategory(category: String) {
        mainScreenUserInteractor.selectedTagsArray.clear()
        mainScreenUserInteractor.createTags(category, this)
        viewState.createTags(category, mainScreenUserInteractor.selectedTagsArray)
        viewState.hideTags()
        viewState.showTags()
        mainScreenDataInteractor.getMainScreenData(category, this)
    }

    fun createMainScreenWithSearchUserName(city: String, userName: String) {
        mainScreenUserInteractor.getMainScreenDataByUserName(city, userName, this)
    }

    fun createMainScreenWithSearchServiceName(city: String, serviceName: String) {
        mainScreenUserInteractor.getMainScreenDataByServiceName(city, serviceName, this)
    }

    fun createMainScreenWithTag(tagText: TextView) {

        if (mainScreenUserInteractor.selectedTagsArray.size == 0) {
            createMainScreenWithCategory(mainScreenUserInteractor.selectedCategory)
        } else {
            mainScreenDataInteractor.getMainScreenData(
                mainScreenUserInteractor.selectedTagsArray,
                this
            )
        }

        if (mainScreenUserInteractor.selectedTagsArray.contains(tagText.text.toString())) {
            //disable
            viewState.disableTag(tagText)
            mainScreenUserInteractor.selectedTagsArray.remove(tagText.text.toString())
        } else {
            //enable
            viewState.enableTag(tagText)
            mainScreenUserInteractor.selectedTagsArray.add(tagText.text.toString())
        }
    }

    override fun returnMainScreenDataWithCreateCategory(mainScreenData: ArrayList<ArrayList<Any>>) {
        viewState.hideLoading()
        viewState.showMainScreen(mainScreenData)
        viewState.createCategoryFeed(mainScreenUserInteractor.getCategories(mainScreenData))
    }

    override fun returnMainScreenData(mainScreenData: ArrayList<ArrayList<Any>>) {
        //если 0, то выводим, что ничего не нашел
        viewState.hideLoading()
        viewState.showMainScreen(mainScreenData)
    }

    override fun showLoading() {
        viewState.showLoading()
    }

    override fun createTags(category: String, selectedTagsArray: ArrayList<String>) {
        viewState.createTags(category, selectedTagsArray)
    }

    override fun getServicesByUserId(userId: String) {
        mainScreenServiceInteractor.getServicesByUserId(userId)
    }

    override fun getServicesByUserIdAndServiceName(userId: String, serviceName: String) {
        mainScreenServiceInteractor.getServicesByUserIdAndServiceName(
            userId,
            serviceName
        )
    }

    fun isSelectedCategory(category: String): Boolean =
        mainScreenUserInteractor.isSelectedCategory(category)

    fun setTagsState(visibility: Int) {
        if (visibility == View.VISIBLE) {
            viewState.hideTags()
        } else {
            if (visibility != View.GONE) {
                viewState.createTags(
                    mainScreenUserInteractor.selectedCategory,
                    mainScreenUserInteractor.selectedTagsArray
                )
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
        mainScreenUserInteractor.selectedTagsArray.clear()
        for (btn in categoriesBtns) {
            if (mainScreenUserInteractor.selectedCategory == btn.text.toString()) {
                mainScreenUserInteractor.selectedCategory = ""
                viewState.disableCategoryBtn(btn)
                viewState.hideTags()
                break
            }
        }
    }
}