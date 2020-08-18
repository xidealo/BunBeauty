package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.photo

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers.BaseGetListCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo

interface PhotosCallback : BaseGetListCallback<Photo>