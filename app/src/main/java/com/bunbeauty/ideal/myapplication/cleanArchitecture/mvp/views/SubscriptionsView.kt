package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView

interface SubscriptionsView : MvpView {
    fun showSubscriptions()
    fun hideLoading()
    fun showLoading()
    fun showEmptySubscriptions()
    fun hideEmptySubscriptions()
}