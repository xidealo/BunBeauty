package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService

import android.net.Uri
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.iCreateService.ICreationServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IPhotoRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.CreationServiceActivity
import com.google.firebase.storage.FirebaseStorage

class CreationServicePhotoInteractor(private val photoRepository: IPhotoRepository) : ICreationServicePhotoInteractor {

    override fun addImages(service: Service) {
        for (path in service.photosPath) {
            val photo = Photo()
            photo.userId = service.userId
            photo.serviceId = service.id
            addImage(photo, path)
        }
    }

    private fun addImage(photo: Photo, path: String) {
        val storageReference = FirebaseStorage
            .getInstance()
            .getReference("${CreationServiceActivity.SERVICE_PHOTO}/$photo.id")

        storageReference.putFile(Uri.parse(path)).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                photo.link = it.toString()
                photoRepository.insert(photo)
            }
        }
    }

}