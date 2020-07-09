package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.photoElement.IPhotoElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.photoElement.PhotoElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo

class PhotoAdapter(private val photos: List<Photo>, private val iPhotoElement: IPhotoElement) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PhotoViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.element_photo
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)

        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(serviceViewHolder: PhotoViewHolder, i: Int) {
        serviceViewHolder.bind(photos[i])
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class PhotoViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        fun bind(photo: Photo) {
            val photoElement = PhotoElement(iPhotoElement)
            photoElement.createPhoto(photo, view)
        }
    }
}