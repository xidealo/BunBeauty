package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.photoElement.IPhotoElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.photoElement.ChangeablePhotoElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo

class ChangeablePhotoAdapter(
    private val photos: List<Photo>,
    private val iPhotoElement: IPhotoElement,
    private val width: Int,
    private val height: Int
) : RecyclerView.Adapter<ChangeablePhotoAdapter.ChangeablePhotoViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ChangeablePhotoViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.changeable_element_photo
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)

        return ChangeablePhotoViewHolder(view)
    }

    override fun onBindViewHolder(serviceViewHolder: ChangeablePhotoViewHolder, i: Int) {
        serviceViewHolder.bind(photos[i])
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class ChangeablePhotoViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        fun bind(photo: Photo) {
            val photoElement = ChangeablePhotoElement(
                iPhotoElement,
                width,
                height
            )
            photoElement.createPhoto(photo, view)
        }
    }
}