package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.iCreateService.ICreationServiceTagInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.tag.InsertTagCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.ITagRepository

class CreationServiceTagInteractor(private val tagRepository: ITagRepository) :
    ICreationServiceTagInteractor, InsertTagCallback {

    override fun addTags(service: Service) {
        for (tag in service.tags) {
            tagRepository.insert(tag, this)
        }
    }

    override fun returnCreatedCallback(obj: Tag) {}
}