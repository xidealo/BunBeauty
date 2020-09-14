package com.bunbeauty.ideal.myapplication.clean_architecture.domain.photo

import android.net.Uri
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.photo.DeletePhotoCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.photo.PhotosCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IPhotoServiceRepository
import com.google.firebase.storage.FirebaseStorage

class PhotoInteractor(private val photoServiceRepository: IPhotoServiceRepository) :
    IPhotoInteractor, PhotosCallback, DeletePhotoCallback {

    private var photos = arrayListOf<Photo>()
    private var deletePhotos = arrayListOf<Photo>()

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
        iPhotoCallback: IPhotoCallback
    ) {
        this.iPhotoCallback = iPhotoCallback
        for (photo in photos) {
            if (photo.id.isEmpty()) {
                photo.userId = service.userId
                photo.serviceId = service.id
                photo.id = photoServiceRepository.getIdForNew(photo.userId, photo.serviceId)
                addImage(Service.SERVICE_PHOTO, photo)
            }
        }
        deleteImagesFromService(deletePhotos)
    }

    override fun savePhotos(photos: List<Photo>, user: User, iPhotoCallback: IPhotoCallback) {
        // "last" пока нет списка фоток пользователя
        this.iPhotoCallback = iPhotoCallback
        if (photos.isNotEmpty()) {
            photos.last().userId = user.id
            photos.last().id = user.id
            addImage(User.USER_PHOTO, photos.last())
        }
    }

    override fun deleteImagesFromService(photos: List<Photo>) {
        for (photo in photos) {
            deleteImageFromService(photo)
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

    override fun deletePhotosFromStorage(location: String, photos: List<Photo>) {
        for (photo in photos) {
            deletePhotoFromStorage(location, photo.id)
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