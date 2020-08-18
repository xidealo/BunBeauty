package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service


interface EditServiceView:MvpView{
    fun updatePhotoFeed()
    fun showEditService(service: Service)
    fun goToService(service: Service)
    fun goToProfile(service: Service)
    fun showLoading()
    fun hideLoading()
    fun setNameEditServiceInputError(error:String)
    fun showMessage(message:String)

}