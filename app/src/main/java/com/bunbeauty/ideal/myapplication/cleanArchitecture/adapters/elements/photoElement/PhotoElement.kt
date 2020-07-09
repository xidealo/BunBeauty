package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.photoElement

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.squareup.picasso.Picasso

class PhotoElement(
    private val iPhotoElement: IPhotoElement
) {
    private lateinit var photoPhotoElementImage: ImageView
    private lateinit var deletePhotoElementText: TextView
    private lateinit var currentPhoto: Photo

    fun createPhoto(photo: Photo, view: View) {
        photoPhotoElementImage = view.findViewById(R.id.photoPhotoElementImage)
        photoPhotoElementImage.setOnClickListener {
            iPhotoElement.openPhoto()
        }

        deletePhotoElementText = view.findViewById(R.id.deletePhotoElementText)
        deletePhotoElementText.setOnClickListener {
            iPhotoElement.deletePhoto(photo)
        }

        currentPhoto = photo
        setPhoto(photo)
    }

    private fun setPhoto(photo: Photo) {
        if (photo.link.isEmpty()) {
            Picasso.get()
                .load(photo.uri)
                .centerCrop()
                .resize(125, 125)
                .into(photoPhotoElementImage)
        } else {
            Picasso.get()
                .load(photo.link)
                .centerCrop()
                .resize(125, 125)
                .into(photoPhotoElementImage)
        }
    }
}