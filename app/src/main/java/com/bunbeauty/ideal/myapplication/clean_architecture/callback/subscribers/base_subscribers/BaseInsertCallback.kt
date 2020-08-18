package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers
interface BaseInsertCallback<T> {
    fun returnCreatedCallback(obj: T)
}