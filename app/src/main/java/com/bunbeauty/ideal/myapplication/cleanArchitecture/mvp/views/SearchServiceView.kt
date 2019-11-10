package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView

interface SearchServiceView: MvpView {
    fun attentionNothingFound()
    fun attentionBadConnection()
    fun showLoading()
    fun hideLoading()
    fun buildPanels()
}