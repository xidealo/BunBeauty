package com.bunbeauty.ideal.myapplication.clean_architecture.business.editing.service

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.tag.DeleteTagCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.tag.InsertTagCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.ITagRepository

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