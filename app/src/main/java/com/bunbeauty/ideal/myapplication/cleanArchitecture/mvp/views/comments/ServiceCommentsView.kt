package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment

interface ServiceCommentsView : MvpView {
    fun showLoading()
    fun hideLoading()
    fun updateServiceComments(serviceComments: List<ServiceComment>)
    fun showServiceComments()
    fun hideServiceComments()
    fun showEmptyScreen()
    fun hideEmptyScreen()
}