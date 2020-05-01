package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService

import android.net.Uri
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.iCreateService.ICreationServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.creationService.CreationServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.InsertServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.CreationServiceActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.PhotoRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.TagRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class CreationServiceInteractor(
    private val serviceRepository: ServiceRepository,
    private val tagRepository: TagRepository,
    private val photoRepository: PhotoRepository
) : ICreationServiceInteractor, InsertServiceCallback {

    private lateinit var creationServicePresenterCallback: CreationServicePresenterCallback

    override fun addService(
        service: Service,
        tags: List<String>,
        fpathOfImages: List<String>,
        creationServicePresenterCallback: CreationServicePresenterCallback
    ) {
        this.creationServicePresenterCallback = creationServicePresenterCallback
        if (isNameCorrect(service.name) && isAddressCorrect(service.address)
            && isCategoryCorrect(service.category) && isDescriptionCorrect(service.description)
        ) {
            service.id = serviceRepository.getIdForNew(getUserId())
            serviceRepository.insert(service, this)
            addTags(tags, service)
            addImages(fpathOfImages, service)
        }
    }

    private fun addTags(tags: List<String>, service: Service) {
        for (tagText: String in tags) {
            val tag = Tag()
            tag.id = tagRepository.getIdForNew(service.userId, service.id)
            tag.tag = tagText
            tag.serviceId = service.id
            tag.userId = service.userId
            tagRepository.insert(tag)
        }
    }

    override fun addImages(fpathOfImages: List<String>, service: Service) {
        for (path in fpathOfImages) {
            val photo = Photo()
            photo.userId = service.userId
            photo.serviceId = service.id
            addImage(photo, path)
        }
    }

    private fun addImage(photo: Photo, path: String) {
        photo.id = photoRepository.getIdForNew(photo.serviceId, photo.serviceId)

        val storageReference = FirebaseStorage
            .getInstance()
            .getReference("${CreationServiceActivity.SERVICE_PHOTO}/$photo.id")

        storageReference.putFile(convertToUri(path)).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                photo.link = it.toString()
                photoRepository.insert(photo)
            }
        }
    }

    override fun returnCreatedCallback(obj: Service) {
        creationServicePresenterCallback.showServiceCreated(obj)
    }

    private fun convertToUri(data: String): Uri = Uri.parse(data)

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

    override fun getIsCategoryInputCorrect(city: String): Boolean {

        if (city == "Выбрать категорию") {
            return false
        }
        return true
    }

    override fun getIsAddressInputCorrect(address: String): Boolean = true

    override fun getIsAddressLengthThirty(address: String): Boolean = address.length < 30

    override fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

}