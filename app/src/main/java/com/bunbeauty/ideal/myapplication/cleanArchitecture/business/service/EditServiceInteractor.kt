package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service


import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.EditServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.DeleteServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.UpdateServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.ServiceRepository

class EditServiceInteractor(
    private val intent: Intent,
    private val serviceRepository: ServiceRepository
) : UpdateServiceCallback, DeleteServiceCallback {
    lateinit var cashService: Service
    private lateinit var editServicePresenterCallback: EditServicePresenterCallback


    fun createEditServiceScreen(editServicePresenterCallback: EditServicePresenterCallback) {
        cashService = intent.getSerializableExtra(Service.SERVICE) as Service
        editServicePresenterCallback.showEditService(cashService)
    }

    fun save(
        service: Service,
        editServicePresenterCallback: EditServicePresenterCallback

    ) {
        this.editServicePresenterCallback = editServicePresenterCallback
        if (isNameCorrect(service.name, editServicePresenterCallback)) {
            serviceRepository.update(service, this)
        }
    }

    fun delete(
        service: Service,
        editServicePresenterCallback: EditServicePresenterCallback
    ) {
        this.editServicePresenterCallback = editServicePresenterCallback
        serviceRepository.delete(service, this)
    }
    fun returnDeleteCallback(obj: Service){
        editServicePresenterCallback.goToProfile(cashService)
    }

    override fun returnUpdatedCallback(obj: Service) {
        editServicePresenterCallback.goToService(cashService)
    }

    fun getIsNameInputCorrect(name: String): Boolean {
        if (!name.matches("[a-zA-ZА-Яа-я\\-]+".toRegex())) {
            return false
        }
        return true
    }

    fun getIsNameLengthLessTwenty(name: String): Boolean {
        if (name.length > 20) {
            return false
        }
        return true
    }

    private fun isNameCorrect(
        name: String,
        editServicePresenterCallback: EditServicePresenterCallback
    ): Boolean {
        if (name.isEmpty()) {
            editServicePresenterCallback.nameEditServiceInputErrorEmpty()
            return false
        }

        if (!getIsNameInputCorrect(name)) {
            editServicePresenterCallback.nameEditServiceInputError()
            return false
        }

        if (!getIsNameLengthLessTwenty(name)) {
            editServicePresenterCallback.nameEditServiceInputErrorLong()
            return false
        }
        return true
    }

    override fun returnDeletedCallback(obj: Service) {
        editServicePresenterCallback.goToService(cashService)
    }
}
