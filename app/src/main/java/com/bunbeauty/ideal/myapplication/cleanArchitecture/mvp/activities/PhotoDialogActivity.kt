package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.davemorrissey.labs.subscaleview.ImageSource
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_photo_dialog.*

class PhotoDialogActivity : AppCompatActivity() {

    private lateinit var photosList: ArrayList<Photo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_dialog)
        photosList = intent.getParcelableArrayListExtra(Photo.PHOTO) ?: ArrayList()

        if (photosList.isNotEmpty())
            Picasso.get()
                .load(photosList.first().uri)
                .into(object : Target {

                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                        photoPhotoDialogImage.setImage(ImageSource.bitmap(bitmap))
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                })
    }
}