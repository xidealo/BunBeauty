package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface ServiceView: MvpView {
    fun showService(service: Service)
    fun showPhotos(photos: List<Photo>)
    fun hidePremium()
    fun showPremium(service: Service)
    fun createOwnServiceTopPanel(service: Service)
    fun createAlienServiceTopPanel(user: User, service: Service)
    fun showLoading()
    fun hideLoading()
    fun showRating(rating: Float, countOfRates: Long)
    fun goToEditService(service: Service)
    fun goToProfile(user: User)
    fun showMessage(message: String)
    fun setTopPanelTitle(title: String)
    fun showSessionButton()
    fun hideSessionButton()
}