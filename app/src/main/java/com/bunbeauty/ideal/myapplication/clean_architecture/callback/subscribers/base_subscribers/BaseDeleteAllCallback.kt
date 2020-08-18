package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

interface BaseDeleteAllCallback {
    fun returnAllDeletedCallback(deletedCount: Int)
    fun returnDeleteCallback(obj: Service)
}