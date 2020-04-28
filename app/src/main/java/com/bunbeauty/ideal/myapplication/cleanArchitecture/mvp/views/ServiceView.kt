package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface ServiceView: MvpView {
    fun showPhotos(photos: List<Photo>)
    fun showTopPanelForMyService(service: Service)
    fun showTopPanelForAlienService(serviceName: String, serviceOwner: User)
    fun showPremium(isPremium: Boolean)
    fun showService(service: Service)
    fun showLoading()
    fun hideLoading()
}