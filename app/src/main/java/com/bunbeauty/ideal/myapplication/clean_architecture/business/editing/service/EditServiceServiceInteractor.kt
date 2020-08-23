package com.bunbeauty.ideal.myapplication.clean_architecture.business.editing.service


import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.service.EditServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.DeleteServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.UpdateServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceRepository

class EditServiceServiceInteractor(
    private val intent: Intent,
    private val serviceRepository: IServiceRepository
) : UpdateServiceCallback, DeleteServiceCallback, IEditServiceServiceInteractor {

    private lateinit var gottenService: Service
    private lateinit var editServicePresenterCallback: EditServicePresenterCallback

    override fun getGottenService() = gottenService

    override fun getService(editServicePresenterCallback: EditServicePresenterCallback) {
        gottenService = intent.getSerializableExtra(Service.SERVICE) as Service
        editServicePresenterCallback.showEditService(gottenService)
    }

    override fun update(
        service: Service,
        editServicePresenterCallback: EditServicePresenterCallback
    ) {
        this.editServicePresenterCallback = editServicePresenterCallback
        if (isNameCorrect(service.name, editServicePresenterCallback)) {
            serviceRepository.update(service, this)
        }
    }

    override fun returnUpdatedCallback(obj: Service) {
        editServicePresenterCallback.saveTags(obj)
        editServicePresenterCallback.goToService(gottenService)
    }

    override fun delete(
        service: Service,
        editServicePresenterCallback: EditServicePresenterCallback
    ) {
        this.editServicePresenterCallback = editServicePresenterCallback
        serviceRepository.delete(service, this)
    }

    override fun returnDeletedCallback(obj: Service) {
        editServicePresenterCallback.goToProfile(obj)
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


}
