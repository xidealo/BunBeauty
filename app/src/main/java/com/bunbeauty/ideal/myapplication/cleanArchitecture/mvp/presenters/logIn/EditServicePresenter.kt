package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.EditServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.EditServiceView

@InjectViewState
class EditServicePresenter(private val editServiceInteractor:EditServiceInteractor)
    :MvpPresenter<EditServiceView>(){

}