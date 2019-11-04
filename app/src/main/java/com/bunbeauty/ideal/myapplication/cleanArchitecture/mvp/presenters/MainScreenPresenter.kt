package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

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
    }

    fun createMainScreenWithCategory(category: String) {

    }

}