package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.photoElement

import android.view.View
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.gone
import kotlinx.android.synthetic.main.element_changeable_photo.view.*

class AddPhotoElement(view: View, changeablePhotoActivityElement: EditablePhotoActivity) {

    init {
        val padding = view.resources.getDimensionPixelSize(R.dimen.photo_padding)
        view.element_changeable_photo_iv_photo.setImageDrawable(
            ContextCompat.getDrawable(view.context, R.drawable.icon_image)
        )
        view.element_changeable_photo_iv_photo.setPadding(padding, padding, padding, padding)
        view.element_changeable_photo_iv_photo.setOnClickListener {
            changeablePhotoActivityElement.addPhoto()
        }

        view.element_changeable_photo_btn_delete.gone()
    }
}