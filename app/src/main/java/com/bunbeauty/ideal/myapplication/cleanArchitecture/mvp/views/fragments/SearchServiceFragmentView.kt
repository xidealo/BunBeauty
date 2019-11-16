package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.fragments

import com.arellomobile.mvp.MvpView

interface SearchServiceFragmentView: MvpView {
    fun attentionNothingFound()
    fun attentionBadConnection()
    fun showMyCity(position:Int)
}