package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.fragments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.premium.PremiumElementCodeInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.premium.PremiumElementServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.CheckPremiumPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.fragments.PremiumElementFragmentView

@InjectViewState
class PremiumElementPresenter(
    private val premiumElementCodeInteractor: PremiumElementCodeInteractor,
    private val premiumElementServiceInteractor: PremiumElementServiceInteractor
) :
    MvpPresenter<PremiumElementFragmentView>(), CheckPremiumPresenterCallback {
    fun setPremium(code: String, service: Service) {
        premiumElementCodeInteractor.checkCode(code, this)
        premiumElementServiceInteractor.service = service
    }

    override fun showError(error: String) {
        viewState.showError(error)
    }

    override fun showPremiumActivated() {
        viewState.showPremiumActivated()
        viewState.setWithPremium()
        viewState.hideBottom()
    }

    override fun activatePremium() {
        premiumElementServiceInteractor.activatePremium(
            premiumElementServiceInteractor.service,
            this
        )

    }
}