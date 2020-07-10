package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service


interface EditServiceView:MvpView{
    fun updatePhotoFeed()
    fun showEditService(service: Service)
    fun goToService(service: Service)
    fun goToProfile(service: Service)
    fun showLoading()
    fun hideLoading()
    fun setNameEditServiceInputError(error:String)

}