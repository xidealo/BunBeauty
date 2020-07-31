package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.davemorrissey.labs.subscaleview.ImageSource
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_photo_dialog.*

class PhotoDialogActivity : MvpAppCompatActivity() {

    private lateinit var photosList: ArrayList<Photo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_dialog)
        photosList = intent.getParcelableArrayListExtra(Photo.PHOTO) ?: ArrayList()
        val openedPhotoLinkOrUri = intent.getStringExtra(Photo.LINK) ?: ""

        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.d("", "onPrepareLoad")
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                Log.d("", e?.printStackTrace().toString())
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap != null)
                    photoPhotoDialogImage.setImage(ImageSource.bitmap(bitmap))

                loadingPhotoDialogProgressBar.visibility = View.GONE
            }

        }
        showPhoto(photosList, openedPhotoLinkOrUri, target)
    }

    fun showPhoto(photos: ArrayList<Photo>, openedPhotoLinkOrUri: String, target: Target) {
        if (photos.isNotEmpty()) {
            val openedLinkPhoto = photos.find { it.link == openedPhotoLinkOrUri }
            if (openedLinkPhoto == null) {
                val openedUriPhoto = photos.find { it.uri == openedPhotoLinkOrUri }
                if (openedUriPhoto != null)
                    Picasso.get()
                        .load(openedUriPhoto.uri)
                        .into(target)
            } else {
                Picasso.get()
                    .load(openedLinkPhoto.link)
                    .into(target)
            }
        }
    }
}