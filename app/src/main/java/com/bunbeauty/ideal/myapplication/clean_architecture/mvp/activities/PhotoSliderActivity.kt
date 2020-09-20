package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.PhotoPagerAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_photo_slider.*

class PhotoSliderActivity : BaseActivity() {

    private lateinit var photosList: ArrayList<Photo>
    private lateinit var photoPagerAdapter: PhotoPagerAdapter

    private var photoPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_slider)

        initPanel()
        photosList = intent.getParcelableArrayListExtra(Photo.PHOTOS) ?: ArrayList()
        val openedPhotoLink = intent.getStringExtra(Photo.LINK) ?: ""
        photoPosition = photosList.indexOf(photosList.find { it.link == openedPhotoLink })

        photoPagerAdapter = PhotoPagerAdapter(this)
        activity_photo_slider_vp_photos.adapter = photoPagerAdapter
        photoPagerAdapter.setItems(photosList)

        activity_photo_slider_vp_photos.setCurrentItem(photoPosition, false)
    }

    private fun initPanel() {
        initTopPanel("", ButtonTask.NONE)
        setTopPanelColor(ContextCompat.getColor(this, R.color.black))
    }

}