package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.photoElement.AddPhotoElement
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.photoElement.ChangeablePhotoElement
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.photoElement.EditablePhotoActivity

class ChangeablePhotoAdapter :
    RecyclerView.Adapter<ChangeablePhotoAdapter.ChangeablePhotoViewHolder>() {

    private val photos: ArrayList<Photo> = arrayListOf()
    lateinit var editablePhotoActivity: EditablePhotoActivity
    private var width = 0
    private var height = 0

    fun setData(
        photos: List<Photo>,
        changeablePhotoActivityElement: EditablePhotoActivity,
        width: Int,
        height: Int
    ) {
        this.photos.clear()
        this.photos.addAll(photos)
        this.width = width
        this.height = height
        this.editablePhotoActivity = changeablePhotoActivityElement
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ChangeablePhotoViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.element_changeable_photo
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)

        return ChangeablePhotoViewHolder(view)
    }

    override fun onBindViewHolder(changeablePhotoViewHolder: ChangeablePhotoViewHolder, i: Int) {
        if (i == 0) {
            changeablePhotoViewHolder.firstBind()
        } else {
            changeablePhotoViewHolder.bind(photos[i - 1])
        }
    }

    override fun getItemCount() = photos.size + 1


    inner class ChangeablePhotoViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        fun firstBind() {
            AddPhotoElement(view, editablePhotoActivity)
        }

        fun bind(photo: Photo) {
            ChangeablePhotoElement(
                editablePhotoActivity,
                width,
                height,
                photo,
                view
            )
        }
    }
}