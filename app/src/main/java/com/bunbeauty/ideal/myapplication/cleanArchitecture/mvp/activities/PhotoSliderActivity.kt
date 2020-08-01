package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities

import android.os.Bundle
import android.view.View
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.PhotoPagerAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.photo.PhotoSlideCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import kotlinx.android.synthetic.main.activity_photo_slider.*

class PhotoSliderActivity : MvpAppCompatActivity(), PhotoSlideCallback {

    private lateinit var photosList: ArrayList<Photo>
    private lateinit var photoPagerAdapter: PhotoPagerAdapter

    private var photoPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_slider)
        photosList = intent.getParcelableArrayListExtra(Photo.PHOTO) ?: ArrayList()
        val openedPhotoLink = intent.getStringExtra(Photo.LINK) ?: ""
        photoPosition = photosList.indexOf(photosList.find { it.link == openedPhotoLink })

        photoPagerAdapter = PhotoPagerAdapter(this)
        photosPhotoSliderPager.adapter = photoPagerAdapter
        photoPagerAdapter.setItems(photosList, this)

        photosPhotoSliderPager.setCurrentItem(
            photoPosition,
            false
        )
    }

    override fun loadedPhoto(position: Int) {
        loadingPhotoSliderProgressBar.visibility = View.GONE
    }

    override fun startLoad() {
        loadingPhotoSliderProgressBar.visibility = View.VISIBLE
    }
}