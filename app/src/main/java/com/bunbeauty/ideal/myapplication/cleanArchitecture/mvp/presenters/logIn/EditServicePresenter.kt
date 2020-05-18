package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.EditServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.EditServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.EditServiceView

@InjectViewState
class EditServicePresenter(private val editServiceInteractor: EditServiceInteractor) :
    MvpPresenter<EditServiceView>(), EditServicePresenterCallback {
    fun createEditServiceScreen() {
        editServiceInteractor.createEditServiceScreen(this)
    }

    override fun showEditService(service: Service) {
        viewState.showEditService(service)
        viewState.hideLoading()
    }

    override fun goToService(service: Service) {
        viewState.goToService(service)
    }

    fun save(name: String, address: String, description: String, cost: Long) {
        val service = editServiceInteractor.cashService
        service.name = name
        service.address = address
        service.description = description
        service.cost = cost
        editServiceInteractor.save(service, this)
    }

    fun delete(){
        val service = editServiceInteractor.cashService
        editServiceInteractor.delete(service, this)
    }
    override fun goToProfile(service: Service){
        viewState.goToProfile(service)
    }

    override fun nameEditServiceInputError() {
        viewState.enableEditServiceBtn()
        viewState.setNameEditServiceInputError("Допустимы только буквы и тире")
    }

    override fun nameEditServiceInputErrorEmpty() {
        viewState.enableEditServiceBtn()
        viewState.setNameEditServiceInputError("Введите своё имя")

    }

    override fun nameEditServiceInputErrorLong() {
        viewState.enableEditServiceBtn()
        viewState.setNameEditServiceInputError("Слишком длинное имя")
    }

}