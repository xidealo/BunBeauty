package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.premium.PremiumFragmentCodeInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.premium.PremiumFragmentServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.PremiumFragmentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.fragments.PremiumElementFragmentView

@InjectViewState
class PremiumFragmentPresenter(
    private val premiumFragmentCodeInteractor: PremiumFragmentCodeInteractor,
    private val premiumFragmentServiceInteractor: PremiumFragmentServiceInteractor
) : MvpPresenter<PremiumElementFragmentView>(), PremiumFragmentPresenterCallback {
    fun setPremium(code: String, service: Service) {
        premiumFragmentCodeInteractor.checkCode(code, this)
        premiumFragmentServiceInteractor.service = service
    }

    override fun showError(error: String) {
        viewState.showError(error)
    }

    override fun showMessage(message: String) {
        viewState.showMessage(message)
    }

    override fun showPremiumActivated(service: Service) {
        viewState.setWithPremium(service.premiumDate)
        viewState.hideBottom()
    }

    override fun activatePremium() {
        premiumFragmentServiceInteractor.activatePremium(
            premiumFragmentServiceInteractor.service,
            this
        )
    }

    fun checkPremium(service: Service) {
        premiumFragmentServiceInteractor.checkPremium(service, this)
    }
}