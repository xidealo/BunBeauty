package com.bunbeauty.ideal.myapplication.clean_architecture.business.create_service

import com.bunbeauty.ideal.myapplication.clean_architecture.business.create_service.iCreateService.ICreationServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.creation_service.CreationServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.InsertServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceRepository

class CreationServiceServiceServiceInteractor(
    private val serviceRepository: IServiceRepository
) : ICreationServiceServiceInteractor, InsertServiceCallback {

    private lateinit var creationServicePresenterCallback: CreationServicePresenterCallback

    override fun addService(
        service: Service,
        creationServicePresenterCallback: CreationServicePresenterCallback
    ) {
        this.creationServicePresenterCallback = creationServicePresenterCallback
        if (isNameCorrect(service.name) && isAddressCorrect(service.address)
            && isCategoryCorrect(service.category) && isDescriptionCorrect(service.description)
        ) {
            serviceRepository.insert(service, this)
        }
    }

    override fun returnCreatedCallback(obj: Service) {
        creationServicePresenterCallback.addPhotos(obj)
        creationServicePresenterCallback.addTags(obj)
        creationServicePresenterCallback.showServiceCreated(obj)
    }

    private fun isNameCorrect(name: String): Boolean {
        if (name.isEmpty()) {
            creationServicePresenterCallback.showNameInputError("Введите имя сервиса")
            return false
        }

        if (!getIsNameInputCorrect(name)) {
            creationServicePresenterCallback.showNameInputError("Имя сервиса должно содержать только буквы")
            return false
        }
        if (!getIsNameLengthLessTwenty(name)) {
            creationServicePresenterCallback.showNameInputError("Имя сервиса должно быть меньше 20 символов")
            return false
        }
        return true
    }

    private fun isDescriptionCorrect(description: String): Boolean {

        if (description.isEmpty()) {
            creationServicePresenterCallback.showDescriptionInputError("Введите описание сервиса")
            return false
        }

        if (!getIsDescriptionInputCorrect(description)) {
            creationServicePresenterCallback.showDescriptionInputError("")
            return false
        }

        if (!getIsDescriptionLengthLessTwoHundred(description)) {
            creationServicePresenterCallback.showDescriptionInputError("Описание должно быть меньше 200 символов")
            return false
        }

        return true
    }


    private fun isCategoryCorrect(category: String): Boolean {
        if (!getIsCategoryInputCorrect(category)) {
            creationServicePresenterCallback.showCategoryInputError("Выберите категорию")
            return false
        }
        return true
    }

    private fun isAddressCorrect(address: String): Boolean {

        if (address.isEmpty()) {
            creationServicePresenterCallback.showAddressInputError("Введите адрес")
            return false
        }

        if (!getIsAddressInputCorrect(address)) {
            creationServicePresenterCallback.showAddressInputError("")
            return false
        }
        if (!getIsAddressLengthThirty(address)) {
            creationServicePresenterCallback.showAddressInputError("Адрес должен быть меньше 30 символов")
            return false
        }
        return true
    }


    override fun getIsNameInputCorrect(name: String): Boolean {
        if (!name.matches("[a-zA-ZА-Яа-я\\- ]+".toRegex())) {
            return false
        }
        return true
    }

    override fun getIsNameLengthLessTwenty(name: String): Boolean = name.length < 20

    override fun getIsDescriptionInputCorrect(description: String): Boolean {
        // можно проверку на мат добавить
        return true
    }

    override fun getIsDescriptionLengthLessTwoHundred(description: String): Boolean =
        description.length < 200

    override fun getIsCostInputCorrect(cost: String): Boolean {
        if (!cost.matches("[\\d+]+".toRegex())) {
            return false
        }
        return true
    }

    override fun getIsCostLengthLessTen(cost: String): Boolean = cost.length < 10

    override fun getIsCategoryInputCorrect(category: String): Boolean {

        if (category == "Выбрать категорию" || category.isEmpty()) {
            return false
        }

        return true
    }

    override fun getIsAddressInputCorrect(address: String): Boolean = true

    override fun getIsAddressLengthThirty(address: String): Boolean = address.length < 30

}