package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.service

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag

interface IEditServiceTagInteractor {
    fun setCachedServiceTags(cachedServiceTags: ArrayList<Tag>)
    fun saveTags(service: Service)
}