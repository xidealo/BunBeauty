package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.serviceComment.DeleteServiceCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.serviceComment.InsertServiceCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.serviceComment.ServiceCommentsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.serviceComment.UpdateServiceCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment

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