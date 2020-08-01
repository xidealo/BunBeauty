package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.photo

import java.io.Serializable

interface PhotoSlideCallback : Serializable {
    fun loadedPhoto(position: Int)
    fun startLoad()
}