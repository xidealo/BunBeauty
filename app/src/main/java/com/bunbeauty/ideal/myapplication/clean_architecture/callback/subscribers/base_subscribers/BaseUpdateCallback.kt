package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers

interface BaseUpdateCallback<T> {
    fun returnUpdatedCallback(obj: T)
}