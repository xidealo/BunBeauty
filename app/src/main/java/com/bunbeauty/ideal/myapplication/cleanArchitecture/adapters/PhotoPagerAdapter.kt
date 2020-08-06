package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.photoElement.PhotoPagerElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.photo.PhotoSlideCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo

class PhotoPagerAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    private val items: ArrayList<Photo> = arrayListOf()
    private lateinit var photoSlideCallback: PhotoSlideCallback

    override fun createFragment(position: Int): Fragment =
        PhotoPagerElement.newInstance(items[position], photoSlideCallback)

    override fun getItemCount() = items.size

    fun setItems(newItems: List<Photo>, photoSlideCallback: PhotoSlideCallback) {
        items.clear()
        items.addAll(newItems)
        this.photoSlideCallback = photoSlideCallback
        notifyDataSetChanged()
    }
}