package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service_comment.DeleteServiceCommentCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service_comment.InsertServiceCommentCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service_comment.ServiceCommentsCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service_comment.UpdateServiceCommentCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.api.ServiceCommentFirebase
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceCommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServiceCommentRepository(
    private val serviceCommentFirebase: ServiceCommentFirebase
) : BaseRepository(), IServiceCommentRepository {

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
        launch {
            serviceCommentFirebase.getByServiceId(userId, serviceId, serviceCommentsCallback)
        }
    }
}