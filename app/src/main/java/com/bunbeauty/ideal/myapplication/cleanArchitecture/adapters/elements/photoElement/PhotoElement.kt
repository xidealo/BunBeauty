package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.photoElement

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.squareup.picasso.Picasso

class PhotoElement(
    private val iPhotoElement: IPhotoElement,
    private val width: Int,
    private val height: Int
) {
    private lateinit var photoPhotoElementImage: ImageView
    private lateinit var currentPhoto: Photo

    fun createPhoto(photo: Photo, view: View) {
        photoPhotoElementImage = view.findViewById(R.id.photoPhotoElementImage)
        photoPhotoElementImage.setOnClickListener {
            iPhotoElement.openPhoto()
        }

        currentPhoto = photo
        setPhoto(photo)
    }

    private fun setPhoto(photo: Photo) {
        if (photo.link.isEmpty()) {
            Picasso.get()
                .load(photo.uri)
                .resize(width, height)
                .centerCrop()
                .into(photoPhotoElementImage)
        } else {
            Picasso.get()
                .load(photo.link)
                .resize(width, height)
                .centerCrop()
                .into(photoPhotoElementImage)
        }
    }
}