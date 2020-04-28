package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface ServiceView: MvpView {
    fun showPhotos(photos: List<Photo>)
    fun showPremium(service: Service)
    fun showService(user: User, service: Service)
    fun showLoading()
    fun hideLoading()
}