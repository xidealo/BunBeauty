package com.bunbeauty.ideal.myapplication.clean_architecture.domain.editing.service


import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.service.EditServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.DeleteServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.UpdateServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceRepository

class EditServiceServiceInteractor(
    private val serviceRepository: IServiceRepository
) : UpdateServiceCallback, DeleteServiceCallback, IEditServiceServiceInteractor {

    private lateinit var gottenService: Service
    private lateinit var editServicePresenterCallback: EditServicePresenterCallback

    override fun getGottenService() = gottenService

    override fun getService(
        intent: Intent,
        editServicePresenterCallback: EditServicePresenterCallback
    ) {
        gottenService = intent.getParcelableExtra(Service.SERVICE)!!
        editServicePresenterCallback.showEditService(gottenService)
    }

    override fun update(
        service: Service,
        editServicePresenterCallback: EditServicePresenterCallback
    ) {
        this.editServicePresenterCallback = editServicePresenterCallback
        if (isNameCorrect(service.name, editServicePresenterCallback) &&
            isAddressCorrect(service.address, editServicePresenterCallback) &&
            isCategoryCorrect(service.category, editServicePresenterCallback) &&
            isDescriptionCorrect(service.description, editServicePresenterCallback) &&
            isDurationNotZero(service.duration, editServicePresenterCallback)
        ) {
            serviceRepository.update(service, this)
        }
    }

    override fun returnUpdatedCallback(obj: Service) {
        editServicePresenterCallback.saveTags(gottenService)
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

    private fun isNameCorrect(
        name: String,
        editServicePresenterCallback: EditServicePresenterCallback
    ): Boolean {
        if (name.isEmpty()) {
            editServicePresenterCallback.showNameInputError("Введите имя сервиса")
            return false
        }
        if (!getIsNameInputCorrect(name)) {
            editServicePresenterCallback.showNameInputError("Имя сервиса должно содержать только буквы")
            return false
        }
        if (!getIsNameLengthLessTwenty(name)) {
            editServicePresenterCallback.showNameInputError("Имя сервиса должно быть меньше 20 символов")
            return false
        }
        return true
    }

    private fun isDescriptionCorrect(
        description: String,
        editServicePresenterCallback: EditServicePresenterCallback
    ): Boolean {

        if (description.isEmpty()) {
            editServicePresenterCallback.showDescriptionInputError("Введите описание сервиса")
            return false
        }

        if (!getIsDescriptionInputCorrect(description)) {
            editServicePresenterCallback.showDescriptionInputError("")
            return false
        }

        if (!getIsDescriptionLengthLessTwoHundred(description)) {
            editServicePresenterCallback.showDescriptionInputError("Описание должно быть меньше 200 символов")
            return false
        }

        return true
    }

    private fun isDurationNotZero(
        duration: Float,
        editServicePresenterCallback: EditServicePresenterCallback
    ): Boolean {
        if (duration == 0f) {
            editServicePresenterCallback.showDurationInputError("Установите длительность услуги")
            return false
        }

        return true
    }

    private fun isCategoryCorrect(
        category: String,
        editServicePresenterCallback: EditServicePresenterCallback
    ): Boolean {
        if (!getIsCategoryInputCorrect(category)) {
            editServicePresenterCallback.showCategoryInputError("Выберите категорию")
            return false
        }
        return true
    }

    private fun isAddressCorrect(
        address: String,
        editServicePresenterCallback: EditServicePresenterCallback
    ): Boolean {

        if (address.isEmpty()) {
            editServicePresenterCallback.showAddressInputError("Введите адрес")
            return false
        }

        if (!getIsAddressInputCorrect(address)) {
            editServicePresenterCallback.showAddressInputError("")
            return false
        }
        if (!getIsAddressLengthThirty(address)) {
            editServicePresenterCallback.showAddressInputError("Адрес должен быть меньше 30 символов")
            return false
        }
        return true
    }


    private fun getIsNameInputCorrect(name: String): Boolean {
        if (!name.matches("[a-zA-ZА-Яа-я\\- ]+".toRegex())) {
            return false
        }
        return true
    }

    private fun getIsNameLengthLessTwenty(name: String): Boolean = name.length < 20

    private fun getIsDescriptionInputCorrect(description: String): Boolean {
        // можно проверку на мат добавить
        return true
    }

    private fun getIsDescriptionLengthLessTwoHundred(description: String): Boolean =
        description.length < 200

    private fun getIsCostInputCorrect(cost: String): Boolean {
        if (!cost.matches("[\\d+]+".toRegex())) {
            return false
        }
        return true
    }

    private fun getIsCostLengthLessTen(cost: String): Boolean = cost.length < 10

    private fun getIsCategoryInputCorrect(category: String): Boolean {

        if (category == "Выбрать категорию" || category.isEmpty()) {
            return false
        }

        return true
    }

    private fun getIsAddressInputCorrect(address: String): Boolean = true

    private fun getIsAddressLengthThirty(address: String): Boolean = address.length < 30

}
