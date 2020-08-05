package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.DeletePhotoCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.PhotosCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PhotoServiceFirebase {
    private val TAG = "data_layer"

    fun insert(photo: Photo) {
        val database = FirebaseDatabase.getInstance()
        val serviceRef = database
            .getReference(User.USERS)
            .child(photo.userId)
            .child(Service.SERVICES)
            .child(photo.serviceId)
            .child(Photo.PHOTOS)
            .child(photo.id)

        val items = HashMap<String, Any>()
        items[Photo.LINK] = photo.link
        serviceRef.updateChildren(items)
        Log.d(TAG, "Service adding completed")
    }

    fun delete(photo: Photo, deletePhotoCallback: DeletePhotoCallback) {
        val subscriberRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(photo.userId)
            .child(Service.SERVICES)
            .child(photo.serviceId)
            .child(Photo.PHOTOS)
            .child(photo.id)

        subscriberRef.removeValue()
        deletePhotoCallback.returnDeletedCallback(photo)
    }

    fun getByServiceId(serviceOwnerId: String, serviceId: String, photosCallback: PhotosCallback) {
        val photosRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(serviceOwnerId)
            .child(Service.SERVICES)
            .child(serviceId)
            .child(Photo.PHOTOS)

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
        return FirebaseDatabase.getInstance().getReference(User.USERS)
            .child(userId)
            .child(Service.SERVICES)
            .child(serviceId)
            .child(Photo.PHOTOS).push().key!!
    }
}