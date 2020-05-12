package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.baseSubscribers

interface BaseChangedCallback<T> {
    fun returnChanged(element: T)
}