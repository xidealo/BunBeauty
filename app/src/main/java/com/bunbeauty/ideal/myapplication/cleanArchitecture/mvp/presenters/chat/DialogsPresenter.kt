package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.DialogsInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.chat.DialogsView

@InjectViewState
class DialogsPresenter(private val dialogsInteractor: DialogsInteractor):MvpPresenter<DialogsView>() {

}