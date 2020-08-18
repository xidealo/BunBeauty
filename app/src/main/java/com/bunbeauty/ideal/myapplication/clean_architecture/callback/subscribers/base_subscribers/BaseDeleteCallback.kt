package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers
interface BaseDeleteCallback<T> {
    fun returnDeletedCallback(obj: T)
}