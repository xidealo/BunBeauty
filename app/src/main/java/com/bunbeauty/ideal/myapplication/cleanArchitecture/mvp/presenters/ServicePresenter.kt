package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServiceUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ServiceView

@InjectViewState
class ServicePresenter(
    private val serviceServiceInteractor: IServiceServiceInteractor,
    private val servicePhotoInteractor: IServicePhotoInteractor,
    private val serviceUserInteractor: IServiceUserInteractor
) :
    MvpPresenter<ServiceView>(), ServicePresenterCallback {

    fun createServiceScreen() {
        serviceServiceInteractor.createServiceScreen(serviceUserInteractor.getUser(),this)
    }

    fun iconClick() {
        serviceServiceInteractor.iconClick(serviceUserInteractor.getUser(), this)
    }

    override fun showService(service: Service) {
        viewState.hideLoading()
        viewState.showService(service)
        servicePhotoInteractor.getServicePhotos(service, this)
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

    override fun showPhotos(photos: List<Photo>) {
        viewState.showPhotos(photos)
    }

    override fun goToEditService(service: Service) {
        viewState.goToEditService(service)
    }

    override fun goToProfile(user: User) {
        viewState.goToProfile(user)
    }

    fun updateService(service: Service) {
        serviceServiceInteractor.updateService(service, this)
    }

    /*
      fun getServicePhotos(serviceId: String, serviceOwnerId: String) {
          serviceInteractor.getServicePhotos(serviceId, serviceOwnerId, this)
      }*/

}