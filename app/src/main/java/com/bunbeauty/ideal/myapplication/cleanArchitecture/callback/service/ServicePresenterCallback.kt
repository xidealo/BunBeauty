package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface ServicePresenterCallback {
    fun showService(service: Service)
    fun showPremium(service: Service)
    fun createOwnServiceTopPanel(service: Service)
    fun createAlienServiceTopPanel(user: User, service: Service)
    fun showPhotos(photo: List<Photo>)
    fun goToEditService(service: Service)
    fun goToProfile(user: User)
    fun getServicePhotos(service: Service)
    fun setTitle(title: String)
}