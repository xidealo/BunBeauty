package com.bunbeauty.ideal.myapplication.clean_architecture.callback.service

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface ServicePresenterCallback {
    fun showService(service: Service)
    fun showPremium()
    fun createOwnServiceTopPanel()
    fun createAlienServiceTopPanel(user: User)
    fun showPhotos(photoList: List<Photo>)
    fun goToEditService(service: Service)
    fun goToProfile(user: User)
    fun getServicePhotos(service: Service)
    fun setTitle(title: String)
    fun getUser(userId: String)
    fun hideSessionButton()
    fun showSessionButton()
}