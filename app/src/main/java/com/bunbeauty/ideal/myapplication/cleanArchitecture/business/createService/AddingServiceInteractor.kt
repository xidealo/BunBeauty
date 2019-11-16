package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService

import android.net.Uri
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.iCreateService.IAddingServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.AddingServiceActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.PhotoRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.TagRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class AddingServiceInteractor(private val serviceRepository: ServiceRepository,
                              private val tagRepository: TagRepository,
                              private val photoRepository: PhotoRepository) : IAddingServiceInteractor {

    override fun addService(service: Service, tags: List<String>): String {
        service.id = serviceRepository.getIdForNew(getUserId())
        serviceRepository.insert(service)

        for (tagText: String in tags) {
            val tag = Tag()
            tag.id = tagRepository.getIdForNew(service.userId, service.id)
            tag.tag = tagText
            tag.serviceId = service.id
            tag.userId = service.userId
            tagRepository.insert(tag)
        }
        return service.id
    }

    override fun addImages(fpathOfImages: List<String>, service: Service) {
        for (path in fpathOfImages) {
            val photo = Photo()
            photo.serviceId = service.id
            photo.userId = service.userId
            photo.serviceId = service.id
            addImage(photo, path)
        }
    }

    private fun addImage(photo: Photo, path: String) {
        photo.id = photoRepository.getIdForNew(photo.serviceId, photo.serviceId)

        val storageReference = FirebaseStorage
                .getInstance()
                .getReference("${AddingServiceActivity.SERVICE_PHOTO}/$photo.id")

        storageReference.putFile(convertToUri(path)).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                photo.link = it.toString()
                photoRepository.insert(photo)
            }
        }.addOnFailureListener {

        }
    }

    private fun convertToUri(data:String):Uri = Uri.parse(data)

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

    override fun getIsDescriptionLengthLessTwoHundred(description: String): Boolean = description.length < 200

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