package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.service

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.tag.DeleteTagCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.tag.InsertTagCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.ITagRepository

class EditServiceTagInteractor(private val tagRepository: ITagRepository) :
    IEditServiceTagInteractor, InsertTagCallback, DeleteTagCallback {
    private var cachedServiceTags: ArrayList<Tag> = ArrayList()

    override fun setCachedServiceTags(cachedServiceTags: ArrayList<Tag>) {
        this.cachedServiceTags = cachedServiceTags
    }

    override fun saveTags(service: Service) {
        for (cacheTag in cachedServiceTags) {
            if (!service.tags.map { it.tag }.contains(cacheTag.tag)) {
                tagRepository.delete(cacheTag, this)
            }
        }

        for (tag in service.tags) {
            if (!cachedServiceTags.map { it.tag }.contains(tag.tag)) {
                tag.serviceId = service.id
                tagRepository.insert(tag, this)
            }
        }
    }

    override fun returnCreatedCallback(obj: Tag) {}

    override fun returnDeletedCallback(obj: Tag) {}

}