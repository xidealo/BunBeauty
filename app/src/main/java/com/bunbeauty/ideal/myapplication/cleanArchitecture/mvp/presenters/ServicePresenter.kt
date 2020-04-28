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

    override fun showService(user: User, service: Service) {
        viewState.showService(user, service)
        viewState.hideLoading()
    }

    override fun showPremium(service: Service) {
        viewState.showPremium(service)
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