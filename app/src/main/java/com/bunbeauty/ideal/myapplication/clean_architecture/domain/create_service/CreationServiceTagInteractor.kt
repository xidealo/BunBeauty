package com.bunbeauty.ideal.myapplication.clean_architecture.domain.create_service

import com.bunbeauty.ideal.myapplication.clean_architecture.domain.create_service.iCreateService.ICreationServiceTagInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.tag.InsertTagCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.ITagRepository

class CreationServiceTagInteractor(private val tagRepository: ITagRepository) :
    ICreationServiceTagInteractor, InsertTagCallback {

    override fun addTags(service: Service) {
        for (tag in service.tags) {
            tag.serviceId = service.id
            tagRepository.insert(tag, this)
        }
    }

    override fun returnCreatedCallback(obj: Tag) {}
}