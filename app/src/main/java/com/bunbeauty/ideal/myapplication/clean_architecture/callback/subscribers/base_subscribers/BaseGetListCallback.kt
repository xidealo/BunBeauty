package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers

interface BaseGetListCallback<T> {
    fun returnList(objects: List<T>)
}