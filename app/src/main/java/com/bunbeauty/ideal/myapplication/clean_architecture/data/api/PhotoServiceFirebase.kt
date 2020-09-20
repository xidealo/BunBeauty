package com.bunbeauty.ideal.myapplication.clean_architecture.data.api

import android.util.Log
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.photo.DeletePhotoCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.photo.PhotosCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PhotoServiceFirebase {
    private val TAG = "data_layer"

    fun insert(photo: Photo) {
        val database = FirebaseDatabase.getInstance()
        val serviceRef = database
            .getReference(Service.SERVICES)
            .child(photo.userId)
            .child(photo.serviceId)
            .child(Photo.PHOTOS_EXTRA)
            .child(photo.id)

        val items = HashMap<String, Any>()
        items[Photo.LINK] = photo.link
        serviceRef.updateChildren(items)
        Log.d(TAG, "Service adding completed")
    }

    fun delete(photo: Photo, deletePhotoCallback: DeletePhotoCallback) {
        val subscriberRef = FirebaseDatabase.getInstance()
            .getReference(Service.SERVICES)
            .child(photo.userId)
            .child(photo.serviceId)
            .child(Photo.PHOTOS_EXTRA)
            .child(photo.id)

        subscriberRef.removeValue()
        deletePhotoCallback.returnDeletedCallback(photo)
    }

    fun getByServiceId(serviceOwnerId: String, serviceId: String, photosCallback: PhotosCallback) {
        val photosRef = FirebaseDatabase.getInstance()
            .getReference(Service.SERVICES)
            .child(serviceOwnerId)
            .child(serviceId)
            .child(Photo.PHOTOS_EXTRA)

        photosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(photosSnapshot: DataSnapshot) {
                val photos = ArrayList<Photo>()

                for (photoSnapshot in photosSnapshot.children) {
                    photos.add(getPhotoFromSnapshot(photoSnapshot, serviceOwnerId, serviceId))
                }

                photosCallback.returnList(photos)
            }

            override fun onCancelled(error: DatabaseError) {
                //some error
            }
        })

    }

    private fun getPhotoFromSnapshot(
        photoSnapshot: DataSnapshot, serviceOwnerId: String,
        serviceId: String
    ): Photo {
        val photo = Photo()
        photo.id = photoSnapshot.key!!
        photo.link = photoSnapshot.child(Photo.LINK).getValue<String>(String::class.java)!!
        photo.userId = serviceOwnerId
        photo.serviceId = serviceId

        return photo
    }

    fun getIdForNew(userId: String, serviceId: String): String {
        return FirebaseDatabase.getInstance()
            .getReference(Service.SERVICES)
            .child(userId)
            .child(serviceId)
            .child(Photo.PHOTOS_EXTRA).push().key!!
    }
}