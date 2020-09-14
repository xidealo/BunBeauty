package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.fragments

import com.arellomobile.mvp.MvpView

interface PremiumElementFragmentView : MvpView {
    fun showError(error: String)
    fun setWithPremium(premiumDate: Long)
    fun showMessage(message: String)
    fun hideBottom()
}