package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers

interface BaseChangedCallback<T> {
    fun returnChanged(element: T)
}