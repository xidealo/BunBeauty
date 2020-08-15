package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServiceUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ServiceView

@InjectViewState
class ServicePresenter(
    private val serviceInteractor: IServiceInteractor,
    private val servicePhotoInteractor: IServicePhotoInteractor,
    private val serviceUserInteractor: IServiceUserInteractor
) : MvpPresenter<ServiceView>(), ServicePresenterCallback {

    fun createServiceScreen() {
        serviceInteractor.createServiceScreen(this)
        serviceUserInteractor.getUser(serviceInteractor.getService().userId, this)
    }

    fun updateService(service: Service) {
        serviceInteractor.updateService(service, this)
    }

    fun iconClick() {
        serviceInteractor.iconClick(serviceUserInteractor.getUser(), this)
    }

    fun getPhotosLink() = servicePhotoInteractor.getPhotosLink()

    override fun showService(service: Service) {
        viewState.hideLoading()
        viewState.showService(service)
    }

    override fun getServicePhotos(service: Service) {
        servicePhotoInteractor.getServicePhotos(service, this)
    }

    override fun showPremium() {
        viewState.showPremium(serviceInteractor.getService())
    }

    override fun createOwnServiceTopPanel() {
        viewState.createOwnServiceTopPanel(serviceInteractor.getService())
    }

    override fun createAlienServiceTopPanel(user: User) {
        viewState.createAlienServiceTopPanel(user, serviceInteractor.getService())
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

    override fun setTitle(title: String) {
        viewState.setTopPanelTitle(title)
    }

    fun getService(): Service {
        return serviceInteractor.getService()
    }
}