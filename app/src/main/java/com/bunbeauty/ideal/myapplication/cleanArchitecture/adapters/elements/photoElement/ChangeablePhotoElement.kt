package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.photoElement

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.squareup.picasso.Picasso

class ChangeablePhotoElement(
    private val iChangeablePhotoElement: IChangeablePhotoElement,
    private val width: Int,
    private val height: Int
) {
    private lateinit var photoPhotoElementImage: ImageView
    private lateinit var deletePhotoElementText: TextView
    private lateinit var currentPhoto: Photo

    fun createPhoto(photo: Photo, view: View) {
        photoPhotoElementImage = view.findViewById(R.id.photoChangeablePhotoElementImage)
        photoPhotoElementImage.setOnClickListener {
            if (photo.link.isNotEmpty()) {
                iChangeablePhotoElement.openPhoto(photo.link)
            } else {
                iChangeablePhotoElement.openPhoto(photo.uri)
            }
        }

        deletePhotoElementText = view.findViewById(R.id.deleteChangeablePhotoElementText)
        deletePhotoElementText.setOnClickListener {
            iChangeablePhotoElement.deletePhoto(photo)
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