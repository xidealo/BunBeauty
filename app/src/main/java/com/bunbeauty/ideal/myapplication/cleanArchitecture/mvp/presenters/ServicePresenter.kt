package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IPhotoCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ServiceView
import com.google.firebase.auth.FirebaseAuth

@InjectViewState
class ServicePresenter(private val serviceInteractor: ServiceInteractor) : MvpPresenter<ServiceView>(),
        IPhotoCallback {

    fun getUserId() = FirebaseAuth.getInstance().currentUser!!.uid

    fun isMyService(ownerId: String) = (getUserId() == ownerId)

    fun getService() = serviceInteractor.getService()

    fun getServiceOwner() = serviceInteractor.getServiceOwner()

    fun getServicePhotos(serviceId: String, serviceOwnerId: String) {
        serviceInteractor.getServicePhotos(serviceId, serviceOwnerId, this)
    }

    override fun returnPhotos(photos: List<Photo>) {
        viewState.showServiceInfo(photos)
    }

    fun setTopPanel(ownerId: String, ownerPhotoLink: String, serviceId: String, serviceName: String) {
        if (isMyService(ownerId)) {
            viewState.showTopPanelForMyService(serviceId, serviceName)
        } else {
            viewState.showTopPanelForAlienService(serviceName, ownerPhotoLink, ownerId)
        }
    }

    fun setPremium(ownerId: String, premiumDate: String) {
        if (isMyService(ownerId)) {
            viewState.showPremium(serviceInteractor.isPremium(premiumDate))
        }
    }

    companion object {
        private val TAG = "DBInf"
    }
}