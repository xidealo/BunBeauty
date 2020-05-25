package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.serviceComment.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ServiceCommentFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IServiceCommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServiceCommentRepository(
    private val serviceCommentFirebase: ServiceCommentFirebase
) : BaseRepository(), IServiceCommentRepository, ServiceCommentCallback, ServiceCommentsCallback {

    private lateinit var serviceCommentCallback: ServiceCommentCallback
    private lateinit var serviceCommentsCallback: ServiceCommentsCallback

    override fun insert(
        serviceComment: ServiceComment,
        insertServiceCommentCallback: InsertServiceCommentCallback
    ) {
        launch {
            serviceComment.id =
                serviceCommentFirebase.getIdForNew(serviceComment.userId, serviceComment.serviceId)
            serviceCommentFirebase.insert(serviceComment)
            withContext(Dispatchers.Main) {
                insertServiceCommentCallback.returnCreatedCallback(serviceComment)
            }
        }
    }

    override fun delete(
        serviceComment: ServiceComment,
        deleteServiceCommentCallback: DeleteServiceCommentCallback
    ) {
        launch {

        }
    }

    override fun update(
        serviceComment: ServiceComment,
        updateServiceCommentCallback: UpdateServiceCommentCallback
    ) {
        launch {

        }
    }

    override fun get(serviceCommentsCallback: ServiceCommentsCallback) {

    }

    override fun getByServiceId(
        userId: String,
        serviceId: String,
        serviceCommentsCallback: ServiceCommentsCallback
    ) {
        this.serviceCommentsCallback = serviceCommentsCallback
        launch {

        }
    }

    override fun returnElement(element: ServiceComment) {
        serviceCommentCallback.returnElement(element)
    }

    override fun returnList(objects: List<ServiceComment>) {
        serviceCommentsCallback.returnList(objects)
    }


}