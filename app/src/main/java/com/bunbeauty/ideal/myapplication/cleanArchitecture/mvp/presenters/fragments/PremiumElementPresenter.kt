package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.fragments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.PremiumElementInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.fragments.PremiumElementFragmentView

@InjectViewState
class PremiumElementPresenter(val premiumElementInteractor: PremiumElementInteractor): MvpPresenter<PremiumElementFragmentView>() {

    fun setPremium(code:String){
        premiumElementInteractor.checkCode(code)
    }
}