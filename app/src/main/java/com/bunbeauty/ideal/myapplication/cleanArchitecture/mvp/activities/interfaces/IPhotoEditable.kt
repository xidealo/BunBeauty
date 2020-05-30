package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces

import android.graphics.Bitmap
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.ServicePhotoFragment

interface IPhotoEditable {
    fun choosePhoto()
    fun showPhoto(bitmap: Bitmap, filePath: String)
    fun removePhoto(servicePhotoFragment: ServicePhotoFragment, filePath: String)
}