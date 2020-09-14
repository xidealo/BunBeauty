package com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.creation_comment

import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.creation_comment.i_creation_comment.ICreationCommentServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.GetServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.UpdateServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceRepository

class CreationCommentServiceInteractor(private val serviceRepository: IServiceRepository) :
    ICreationCommentServiceInteractor, UpdateServiceCallback, GetServiceCallback {

    private lateinit var cacheServiceComment: ServiceComment
    private lateinit var creationCommentPresenterCallback: CreationCommentPresenterCallback

    override fun updateService(
        serviceComment: ServiceComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    ) {
        this.creationCommentPresenterCallback = creationCommentPresenterCallback
        cacheServiceComment = serviceComment
        serviceRepository.getById(serviceComment.serviceId, serviceComment.userId, true, this)
    }

    override fun returnGottenObject(obj: Service?) {
        if (obj == null) return

        obj.rating = calculateAvgRating(obj, cacheServiceComment)
        obj.countOfRates++
        serviceRepository.update(obj, this)
    }

    override fun returnUpdatedCallback(obj: Service) {
        creationCommentPresenterCallback.updateServiceCommentMessage(cacheServiceComment)
    }

    private fun calculateAvgRating(service: Service, serviceComment: ServiceComment): Float {
        return (service.rating * service.countOfRates + serviceComment.rating) / (service.countOfRates + 1)
    }
}