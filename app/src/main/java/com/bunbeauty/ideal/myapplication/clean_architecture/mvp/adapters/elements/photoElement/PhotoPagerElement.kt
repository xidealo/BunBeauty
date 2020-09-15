package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.photoElement

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.davemorrissey.labs.subscaleview.ImageSource
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_photo_pager_element.*


class PhotoPagerElement : Fragment() {

    private lateinit var photo: Photo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photo = it.getParcelable(Photo.PHOTO) ?: Photo()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_pager_element, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.d("", "onPrepareLoad")
                fragment_photo_pager_ssi_photo.showLoading()
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                Log.d("", e?.printStackTrace().toString())
                fragment_photo_pager_ssi_photo.showError()
            }

            /**
             * Can be catch if leave from [PhotoPagerElement], while it loading
             */
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap != null) {
                    try {
                        fragment_photo_pager_ssi_photo.showPhoto(ImageSource.bitmap(bitmap))
                    } catch (e: Exception) {
                        Log.e(Tag.ERROR_TAG, "${e.printStackTrace()}")
                    }
                }
            }
        }

        fragment_photo_pager_ssi_photo.tag = target
        Picasso.get()
            .load(photo.link)
            .into(target)
    }

    companion object {
        @JvmStatic
        fun newInstance(photo: Photo) =
            PhotoPagerElement().apply {
                arguments = Bundle().apply {
                    putParcelable(Photo.PHOTO, photo)
                }
            }
    }
}