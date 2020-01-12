package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface ProfileView: MvpView {
    fun showMyProfileView()
    fun showAlienProfileView()
    fun showUserInfo(user: User)
    fun showUserServices(serviceList: List<Service>, user: User)
    fun hideSubscriptions()
    fun showDialogs()
    fun showProfileInfo(name: String, city: String, phone: String)
    fun showRating(rating: Float)
    fun showWithoutRating()
    fun showAvatar(photoLink: String)
    fun showSubscribers(subscribersCount: Long)
    fun showSubscriptions(subscriptionsCount: Long)
    fun createSwitcher()
    fun showProgress()
    fun hideProgress()
}