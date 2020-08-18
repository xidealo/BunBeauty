package com.bunbeauty.ideal.myapplication.clean_architecture.callback.photo

import java.io.Serializable

interface PhotoSlideCallback : Serializable {
    fun loadedPhoto(position: Int)
    fun startLoad()
}