package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs.ISubscribersInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subs.SubscribersPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.SubscribersView

@InjectViewState
class SubscribersPresenter(private val subscribersInteractor: ISubscribersInteractor) :
    MvpPresenter<SubscribersView>(), SubscribersPresenterCallback {
}