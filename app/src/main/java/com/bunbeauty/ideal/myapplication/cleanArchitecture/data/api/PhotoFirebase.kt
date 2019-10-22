package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.google.firebase.database.FirebaseDatabase

class PhotoFirebase {
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
        items[Photo.PHOTO_LINK] = photo.link
        serviceRef.updateChildren(items)
        Log.d(TAG, "Service adding completed")
    }

    fun getIdForNew(userId: String, serviceId:String): String {
        return FirebaseDatabase.getInstance().getReference(User.USERS)
                .child(userId)
                .child(Service.SERVICES)
                .child(serviceId)
                .child(Photo.PHOTOS).push().key!!
    }
}