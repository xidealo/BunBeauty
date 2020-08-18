package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.comments

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment

interface ServiceCommentsView : MvpView {
    fun showLoading()
    fun hideLoading()
    fun updateServiceComments(serviceComments: List<ServiceComment>)
    fun showServiceComments()
    fun hideServiceComments()
    fun showEmptyScreen()
    fun hideEmptyScreen()
}