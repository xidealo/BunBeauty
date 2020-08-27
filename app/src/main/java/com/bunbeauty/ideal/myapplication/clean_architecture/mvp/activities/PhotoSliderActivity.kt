package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities

import android.os.Bundle
import android.view.View
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.PhotoPagerAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.photo.PhotoSlideCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_photo_slider.*

class PhotoSliderActivity : BaseActivity(), PhotoSlideCallback {

    private lateinit var photosList: ArrayList<Photo>
    private lateinit var photoPagerAdapter: PhotoPagerAdapter

    private var photoPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_slider)
        photosList = intent.getParcelableArrayListExtra(Photo.PHOTO) ?: ArrayList()
        val openedPhotoLink = intent.getStringExtra(Photo.LINK) ?: ""
        photoPosition = photosList.indexOf(photosList.find { it.link == openedPhotoLink })

        photoPagerAdapter =
            PhotoPagerAdapter(
                this
            )
        activity_photo_slider_vp_photos.adapter = photoPagerAdapter
        photoPagerAdapter.setItems(photosList, this)

        activity_photo_slider_vp_photos.setCurrentItem(
            photoPosition,
            false
        )
    }

    override fun loadedPhoto(position: Int) {
        activity_photo_slider_pb_loading.visibility = View.GONE
    }

    override fun startLoad() {
        activity_photo_slider_pb_loading.visibility = View.VISIBLE
    }
}