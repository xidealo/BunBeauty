package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service_comment.DeleteServiceCommentCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service_comment.InsertServiceCommentCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service_comment.ServiceCommentsCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service_comment.UpdateServiceCommentCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment

interface IServiceCommentRepository {

    fun insert(
        serviceComment: ServiceComment,
        insertServiceCommentCallback: InsertServiceCommentCallback
    )

    fun delete(
        serviceComment: ServiceComment,
        deleteServiceCommentCallback: DeleteServiceCommentCallback
    )

    fun update(
        serviceComment: ServiceComment,
        updateServiceCommentCallback: UpdateServiceCommentCallback
    )

    fun get(serviceCommentsCallback: ServiceCommentsCallback)

    fun getByServiceId(
        userId: String,
        serviceId: String,
        serviceCommentsCallback: ServiceCommentsCallback
    )
}