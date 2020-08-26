package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers

interface BaseDeleteAllCallback<T> {
    fun returnDeletedList(objects: List<T>)
}