package com.bunbeauty.ideal.myapplication.clean_architecture.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.photoElement.IPhotoElement
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.photoElement.PhotoElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    private val photos: ArrayList<Photo> = arrayListOf()
    private lateinit var iPhotoElement: IPhotoElement
    private var width = 0
    private var height = 0

    fun setData(photos: List<Photo>, iPhotoElement: IPhotoElement, width: Int, height: Int) {
        this.photos.clear()
        this.width = width
        this.height = height
        this.iPhotoElement = iPhotoElement
        this.photos.addAll(photos)
        notifyDataSetChanged()
    }

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
            val photoElement = PhotoElement(
                iPhotoElement,
                width,
                height
            )
            photoElement.createPhoto(photo, view)
        }
    }
}