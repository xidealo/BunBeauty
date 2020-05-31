package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments

import com.arellomobile.mvp.MvpView

interface ServiceCommentsView : MvpView {
    fun showLoading()
    fun hideLoading()
    fun updateServiceComments()
    fun showServiceComments()
    fun hideServiceComments()
    fun showEmptyScreen()
    fun hideEmptyScreen()
}