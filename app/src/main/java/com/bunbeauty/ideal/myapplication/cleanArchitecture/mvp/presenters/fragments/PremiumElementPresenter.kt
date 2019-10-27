package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.fragments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.PremiumElementInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.CheckPremiumCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.fragments.PremiumElementFragmentView

@InjectViewState
class PremiumElementPresenter(private val premiumElementInteractor: PremiumElementInteractor) :
        MvpPresenter<PremiumElementFragmentView>(), CheckPremiumCallback {
    fun setPremium(code: String) {
        premiumElementInteractor.checkCode(code, this)
    }

    override fun showError(error: String) {
        viewState.showError(error)
    }

    override fun showPremiumActivated() {
        viewState.showPremiumActivated()
    }
}