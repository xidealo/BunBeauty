package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.photoElement

import android.view.View
import android.widget.ImageView
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
            if (photo.link.isNotEmpty()) {
                iPhotoElement.openPhoto(photo.link)
            }
        }
        currentPhoto = photo
        setPhoto(photo)
    }

    private fun setPhoto(photo: Photo) {
        if (photo.link.isNotEmpty()) {
            Picasso.get()
                .load(photo.link)
                .resize(width, height)
                .centerCrop()
                .into(photoPhotoElementImage)
        }
    }
}