package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.baseSubscribers
interface BaseDeleteCallback<T> {
    fun returnDeletedCallback(obj: T)
}