package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.baseSubscribers
interface BaseInsertCallback<T> {
    fun returnCreatedCallback(obj: T)
}