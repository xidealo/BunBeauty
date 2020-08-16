package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.baseSubscribers

interface BaseGetListCallback<T> {
    fun returnList(objects: List<T>)
}