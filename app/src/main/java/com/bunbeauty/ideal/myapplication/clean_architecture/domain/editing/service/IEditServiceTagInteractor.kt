package com.bunbeauty.ideal.myapplication.clean_architecture.domain.editing.service

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Tag

interface IEditServiceTagInteractor {
    fun setCachedServiceTags(cachedServiceTags: ArrayList<Tag>)
    fun saveTags(service: Service)
}