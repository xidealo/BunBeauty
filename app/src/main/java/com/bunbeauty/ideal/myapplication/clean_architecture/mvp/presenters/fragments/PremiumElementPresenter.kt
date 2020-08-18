package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.fragments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.fragments.premium.PremiumElementCodeInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.fragments.premium.PremiumElementServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.CheckPremiumPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.fragments.PremiumElementFragmentView

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

    override fun showPremiumActivated(service: Service) {
        viewState.showPremiumActivated()
        viewState.setWithPremium(service.premiumDate)
        viewState.hideBottom()
    }

    override fun activatePremium() {
        premiumElementServiceInteractor.activatePremium(
            premiumElementServiceInteractor.service,
            this
        )

    }
}