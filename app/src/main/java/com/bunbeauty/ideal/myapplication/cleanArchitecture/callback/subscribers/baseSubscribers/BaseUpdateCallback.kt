package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.baseSubscribers
interface BaseUpdateCallback<T> {
    fun returnUpdatedCallback(obj: T)
}