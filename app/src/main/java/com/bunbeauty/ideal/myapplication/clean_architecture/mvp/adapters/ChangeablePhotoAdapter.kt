package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.photoElement.IChangeablePhotoElement
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.photoElement.ChangeablePhotoElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo

class ChangeablePhotoAdapter :
    RecyclerView.Adapter<ChangeablePhotoAdapter.ChangeablePhotoViewHolder>() {

    private val photos: ArrayList<Photo> = arrayListOf()
    private lateinit var iChangeablePhotoElement: IChangeablePhotoElement
    private var width = 0
    private var height = 0

    fun setData(
        photos: List<Photo>,
        iChangeablePhotoElement: IChangeablePhotoElement,
        width: Int,
        height: Int
    ) {
        this.photos.clear()
        this.width = width
        this.height = height
        this.iChangeablePhotoElement = iChangeablePhotoElement
        this.photos.addAll(photos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ChangeablePhotoViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.element_changeable_photo
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)

        return ChangeablePhotoViewHolder(view)
    }

    override fun onBindViewHolder(serviceViewHolder: ChangeablePhotoViewHolder, i: Int) {
        serviceViewHolder.bind(photos[i])
    }

    override fun getItemCount() = photos.size


    inner class ChangeablePhotoViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        fun bind(photo: Photo) {
            ChangeablePhotoElement(
                iChangeablePhotoElement,
                width,
                height,
                photo,
                view
            )
        }
    }
}