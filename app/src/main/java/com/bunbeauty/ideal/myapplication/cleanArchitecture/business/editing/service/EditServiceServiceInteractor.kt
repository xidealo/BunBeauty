package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.service


import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.EditServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.DeleteServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.UpdateServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IServiceRepository

class EditServiceServiceInteractor(
    private val intent: Intent,
    private val serviceRepository: IServiceRepository
) : UpdateServiceCallback, DeleteServiceCallback, IEditServiceServiceInteractor {
    private lateinit var cacheService: Service
    private lateinit var editServicePresenterCallback: EditServicePresenterCallback

    override fun getCacheService() = cacheService

    override fun createEditServiceScreen(editServicePresenterCallback: EditServicePresenterCallback) {
        cacheService = intent.getSerializableExtra(Service.SERVICE) as Service
        editServicePresenterCallback.showEditService(cacheService)
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

    override fun returnUpdatedCallback(obj: Service) {
        editServicePresenterCallback.saveTags(obj)
        editServicePresenterCallback.goToService(cacheService)
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
