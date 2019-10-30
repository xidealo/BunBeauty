package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.MainScreenView

@InjectViewState
class MainScreenPresenter(private val mainScreenInteractor: MainScreenInteractor): MvpPresenter<MainScreenView>() {


    fun createMainScreen(category:String){
        mainScreenInteractor.getUserId()

    }
}