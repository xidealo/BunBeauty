package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.photoElement

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.photo.PhotoSlideCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.DeletePhotoCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.davemorrissey.labs.subscaleview.ImageSource
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_photo_pager_element.*


class PhotoPagerElement : Fragment() {

    private lateinit var photo: Photo
    private lateinit var photoSlideCallback: PhotoSlideCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photo = it.getParcelable(Photo.PHOTO) ?: Photo()
            photoSlideCallback = it.getSerializable(Photo.PHOTO_CALLBACK) as PhotoSlideCallback
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
                photoSlideCallback.startLoad()
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                Log.d("", e?.printStackTrace().toString())
                errorPhotoPagerElementText.visibility = View.VISIBLE
                photoSlideCallback.loadedPhoto(1)
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap != null) {
                    photoPhotoPagerElementImage.setImage(ImageSource.bitmap(bitmap))
                }

                photoSlideCallback.loadedPhoto(1)
            }
        }

        photoPhotoPagerElementImage.tag = target
        Picasso.get()
            .load(photo.link)
            .into(target)
    }

    companion object {
        @JvmStatic
        fun newInstance(photo: Photo, photoSlideCallback: PhotoSlideCallback) =
            PhotoPagerElement().apply {
                arguments = Bundle().apply {
                    putParcelable(Photo.PHOTO, photo)
                    putSerializable(Photo.PHOTO_CALLBACK, photoSlideCallback)
                }
            }
    }
}