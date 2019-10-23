package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService

import android.net.Uri
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.iCreateService.IAddingServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.AddingServiceActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.PhotoRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.TagRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class AddingServiceInteractor(private val serviceRepository: ServiceRepository,
                              private val tagRepository: TagRepository,
                              private val photoRepository: PhotoRepository) : IAddingServiceInteractor {
    var isPremium: Boolean = false

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

    fun addImage(photo: Photo) {
        photo.id = photoRepository.getIdForNew(photo.userId, photo.serviceId)

        val storageReference = FirebaseStorage
                .getInstance()
                .getReference("${AddingServiceActivity.SERVICE_PHOTO}/$photo.id")

        storageReference.putFile(convertToUri(photo.link)).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
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

    override fun getIsDescriptionLengthLessTwoHunded(description: String): Boolean = description.length < 200

    override fun getIsCostInputCorrect(cost: String): Boolean {
        if (!cost.matches("[\\d+]+".toRegex())) {
            return false
        }
        return true
    }

    override fun getIsCostLengthLessTen(cost: String): Boolean = cost.length < 10

    override fun getIsCategoryInputCorrect(city: String): Boolean {

        if (city == "выбрать категорию") {
            return false
        }
        return true
    }

    override fun getIsAddressInputCorrect(address: String): Boolean = true

    override fun getIsAddressLengthThirty(address: String): Boolean = address.length < 30

    override fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

}