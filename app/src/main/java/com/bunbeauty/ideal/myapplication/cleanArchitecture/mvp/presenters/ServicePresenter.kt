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

    fun getService() {
        serviceInteractor.getService(this)
    }

    override fun showService(service: Service) {
        viewState.hideLoading()
        viewState.showService(service)
    }

    override fun getServicePhotos(service: Service) {
        servicePhotoInteractor.getServicePhotos(service, this)
    }

    override fun showPhotos(photoList: List<Photo>) {
        viewState.showPhotos(photoList)
    }

    override fun getUser(userId: String) {
        serviceUserInteractor.getUser(userId, this)
    }

    override fun createOwnServiceTopPanel() {
        viewState.createOwnServiceTopPanel(serviceInteractor.gottenService)
    }

    override fun createAlienServiceTopPanel(user: User) {
        viewState.createAlienServiceTopPanel(user, serviceInteractor.gottenService)
    }

    override fun showPremium() {
        viewState.showPremium(serviceInteractor.gottenService)
    }

    fun updateService(service: Service) {
        serviceInteractor.updateService(service, this)
    }

    override fun setTitle(title: String) {
        viewState.setTopPanelTitle(title)
    }

    fun iconClick() {
        serviceInteractor.iconClick(serviceUserInteractor.getUser(), this)
    }

    override fun goToEditService(service: Service) {
        viewState.goToEditService(service)
    }

    override fun goToProfile(user: User) {
        viewState.goToProfile(user)
    }

    fun getPhotosLink() = servicePhotoInteractor.getPhotoLinkList()

    fun getGottenService(): Service {
        return serviceInteractor.gottenService
    }
}