package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.fragments

import com.arellomobile.mvp.MvpView

interface PremiumElementFragmentView: MvpView {
    fun showError(error:String)
    fun showPremiumActivated()
    fun setWithPremium()
    fun hideBottom()
}