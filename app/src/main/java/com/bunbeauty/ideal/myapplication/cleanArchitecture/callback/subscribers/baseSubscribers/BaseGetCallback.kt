package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.baseSubscribers

interface BaseGetCallback<T> {
    fun returnGottenObject(obj: T?)
}