package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.photo

import android.net.Uri
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.DeletePhotoCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.PhotosCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IPhotoServiceRepository
import com.google.firebase.storage.FirebaseStorage

class PhotoInteractor(private val photoServiceRepository: IPhotoServiceRepository) :
    IPhotoInteractor, PhotosCallback, DeletePhotoCallback {

    private var photos = mutableListOf<Photo>()
    private var deletePhotos = mutableListOf<Photo>()

    private lateinit var iPhotoCallback: IPhotoCallback

    override fun getPhotosLink() = photos
    override fun getDeletePhotosLink() = deletePhotos

    override fun addPhoto(photo: Photo) {
        photos.add(photo)
    }

    override fun addPhotos(photos: List<Photo>) {
        this.photos.addAll(photos)
    }

    override fun removePhoto(photo: Photo) {
        photos.remove(photo)
        deletePhotos.add(photo)
    }

    override fun savePhotos(
        photos: List<Photo>,
        service: Service,
        iPhotoCallback: IPhotoCallback) {
        this.iPhotoCallback = iPhotoCallback
        for (photo in photos) {
            if (photo.id.isEmpty()) {
                photo.userId = service.userId
                photo.serviceId = service.id
                photo.id = photoServiceRepository.getIdForNew(photo.userId, photo.serviceId)
                addImage(Service.SERVICE_PHOTO, photo)
            }
        }
    }

    override fun savePhotos(photos: List<Photo>, user: User, iPhotoCallback: IPhotoCallback) {
        this.iPhotoCallback = iPhotoCallback
        for (photo in photos) {
            photo.userId = user.id
            photo.id = photoServiceRepository.getIdForNew(photo.userId, photo.serviceId)
            addImage(User.USER_PHOTO, photo)
        }
    }

    override fun deleteImagesFromService(photos: List<Photo>) {
        for (photo in photos) {
            deleteImageFromService(photo)
        }
    }

    override fun deletePhotosFromStorage(location: String, photos: List<Photo>) {
        for (photo in photos) {
            deletePhotoFromStorage(location, photo.id)
        }
    }

    override fun getPhotos(service: Service, iPhotoCallback: IPhotoCallback) {
        this.iPhotoCallback = iPhotoCallback
        photoServiceRepository.getByServiceId(service.id, service.userId, this)
    }

    override fun returnList(objects: List<Photo>) {
        photos.addAll(objects)
        iPhotoCallback.returnPhotos(objects)
    }

    private fun addImage(location: String, photo: Photo) {
        val storageReference = FirebaseStorage
            .getInstance()
            .getReference("$location/${photo.id}")

        storageReference.putFile(Uri.parse(photo.link)).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                when (location) {
                    Service.SERVICE_PHOTO -> {
                        photo.link = it.toString()
                        photoServiceRepository.insert(photo)
                    }
                    User.USER_PHOTO -> {
                        //особенность очищения
                        photos.clear()
                        iPhotoCallback.returnCreatedPhotoLink(it)
                    }
                }
            }
        }
    }

    private fun deleteImageFromService(photo: Photo) {
        photoServiceRepository.delete(photo, this)
    }

    override fun returnDeletedCallback(obj: Photo) {
        deletePhotoFromStorage(Service.SERVICE_PHOTO, photoId = obj.id)
    }

    private fun deletePhotoFromStorage(location: String, photoId: String) {
        val firebaseStorage = FirebaseStorage.getInstance()
        val storageReference =
            firebaseStorage.getReference("$location/$photoId")
        storageReference.delete()
    }

}