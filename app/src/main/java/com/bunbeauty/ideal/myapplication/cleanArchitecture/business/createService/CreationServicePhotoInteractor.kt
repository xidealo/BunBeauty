package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService

import android.net.Uri
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.iCreateService.ICreationServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IPhotoRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.CreationServiceActivity
import com.google.firebase.storage.FirebaseStorage

class CreationServicePhotoInteractor(private val photoRepository: IPhotoRepository) :
    ICreationServicePhotoInteractor {

    var photos = mutableListOf<Photo>()

    override fun addPhoto(photo: Photo) {
        photos.add(photo)
    }

    override fun removePhoto(photo: Photo) {
        photos.remove(photo)
    }

    override fun returnPhotos(): List<Photo> {
        return photos
    }

    override fun addImages(service: Service) {
        for (photo in photos) {
            photo.userId = service.userId
            photo.serviceId = service.id
            photo.id = photoRepository.getIdForNew(photo.userId, photo.serviceId)
            addImage(photo)
        }
    }

    private fun addImage(photo: Photo) {
        val storageReference = FirebaseStorage
            .getInstance()
            .getReference("${CreationServiceActivity.SERVICE_PHOTO}/${photo.id}")

        storageReference.putFile(Uri.parse(photo.uri)).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                photo.link = it.toString()
                photoRepository.insert(photo)
            }
        }
    }

}