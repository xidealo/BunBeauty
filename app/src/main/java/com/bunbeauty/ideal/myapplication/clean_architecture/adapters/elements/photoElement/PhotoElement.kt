package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.photoElement

import android.view.View
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.element_photo.view.*

class PhotoElement(
   iPhotoElement: IPhotoElement,
    private val width: Int,
    private val height: Int,
    photo: Photo,
    view: View
) {

    init {
        view.element_photo_iv_photo.setOnClickListener {
            if (photo.link.isNotEmpty()) {
                iPhotoElement.openPhoto(photo.link)
            }
        }
        setPhoto(photo, view)
    }

    private fun setPhoto(photo: Photo, view: View) {
        if (photo.link.isNotEmpty()) {
            Picasso.get()
                .load(photo.link)
                .resize(width, height)
                .centerCrop()
                .into(view.element_photo_iv_photo)
        }
    }
}