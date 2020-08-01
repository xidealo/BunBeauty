package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.photo

import android.net.Uri
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.DeletePhotoCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.PhotosCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IPhotoRepository
import com.google.firebase.storage.FirebaseStorage


class PhotoInteractor(private val photoRepository: IPhotoRepository) :
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

    override fun saveImages(service: Service) {

        for (photo in photos) {
            if (photo.id.isEmpty()) {
                photo.userId = service.userId
                photo.serviceId = service.id
                photo.id = photoRepository.getIdForNew(photo.userId, photo.serviceId)
                addImage(photo)
            }
        }
    }

    override fun deleteImages() {
        for (photo in deletePhotos) {
            deleteImage(photo)
        }
    }

    override fun getPhotos(service: Service, iPhotoCallback: IPhotoCallback) {
        this.iPhotoCallback = iPhotoCallback
        photoRepository.getByServiceId(service.id, service.userId, this)
    }

    override fun returnList(objects: List<Photo>) {
        photos.addAll(objects)
        iPhotoCallback.returnPhotos(objects)
    }

    private fun addImage(photo: Photo) {
        val storageReference = FirebaseStorage
            .getInstance()
            .getReference("${Service.SERVICE_PHOTO}/${photo.id}")

        storageReference.putFile(Uri.parse(photo.link)).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                photo.link = it.toString()
                photoRepository.insert(photo)
            }
        }
    }

    private fun deleteImage(photo: Photo) {
        photoRepository.delete(photo, this)
    }

    override fun returnDeletedCallback(obj: Photo) {
        deletePhotoFromStorage(photoId = obj.id)
    }

    private fun deletePhotoFromStorage(photoId: String) {
        val firebaseStorage = FirebaseStorage.getInstance()
        val storageReference =
            firebaseStorage.getReference(Service.SERVICE_PHOTO + "/" + photoId)
        storageReference.delete()
    }

}