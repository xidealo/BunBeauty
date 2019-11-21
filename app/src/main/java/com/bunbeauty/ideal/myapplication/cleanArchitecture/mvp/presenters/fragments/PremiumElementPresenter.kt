package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.fragments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.PremiumElementInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ICheckPremiumCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.fragments.PremiumElementFragmentView

@InjectViewState
class PremiumElementPresenter(private val premiumElementInteractor: PremiumElementInteractor) :
        MvpPresenter<PremiumElementFragmentView>(), ICheckPremiumCallback {
    fun setPremium(code: String, service: Service) {
        premiumElementInteractor.checkCode(code, this, service)
    }

    override fun showError(error: String) {
        viewState.showError(error)
    }

    override fun showPremiumActivated() {
        viewState.showPremiumActivated()
        viewState.setWithPremium()
        viewState.hideBottom()
    }
}