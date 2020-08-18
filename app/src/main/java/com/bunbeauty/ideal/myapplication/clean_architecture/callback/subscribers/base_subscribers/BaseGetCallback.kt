package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers

interface BaseGetCallback<T> {
    fun returnGottenObject(obj: T?)
}