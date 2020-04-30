package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IPhotoCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ServiceView
import com.google.firebase.auth.FirebaseAuth

@InjectViewState
class ServicePresenter(private val serviceInteractor: IServiceInteractor) :
    MvpPresenter<ServiceView>(),
    IPhotoCallback, ServicePresenterCallback {

    fun createServiceScreen() {
        serviceInteractor.createServiceScreen(this)
    }

    fun iconClick() {
        serviceInteractor.iconClick(this)
    }

    override fun showService(service: Service) {
        viewState.showService(service)
        viewState.hideLoading()
    }

    override fun showPremium(service: Service) {
        viewState.showPremium(service)
    }

    override fun createOwnServiceTopPanel(service: Service) {
        viewState.createOwnServiceTopPanel(service)
    }

    override fun createAlienServiceTopPanel(user: User, service: Service) {
        viewState.createAlienServiceTopPanel(user, service)
    }

    override fun goToEditService(service: Service) {
        viewState.goToEditService(service)
    }

    override fun goToProfile(user: User) {
        viewState.goToProfile(user)
    }


    /*
      fun getServicePhotos(serviceId: String, serviceOwnerId: String) {
          serviceInteractor.getServicePhotos(serviceId, serviceOwnerId, this)
      }*/

    override fun returnPhotos(photos: List<Photo>) {
        viewState.showPhotos(photos)
    }

    companion object {
        private val TAG = "DBInf"
    }

}