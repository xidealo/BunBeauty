package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.fragments

import com.arellomobile.mvp.MvpView

interface PremiumElementFragmentView: MvpView {
    fun showError(error:String)
    fun setWithPremium(premiumDate: Long)
    fun showPremiumActivated()
    fun hideBottom()
    fun hidePremium()
}