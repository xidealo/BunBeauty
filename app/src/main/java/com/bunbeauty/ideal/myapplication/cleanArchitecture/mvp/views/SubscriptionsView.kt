package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface SubscriptionsView : MvpView {
    fun showSubscriptions(users: List<User>)
    fun hideLoading()
    fun showLoading()
    fun showEmptySubscriptions()
    fun hideEmptySubscriptions()
}