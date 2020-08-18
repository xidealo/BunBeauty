package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.service_comments.iServiceComments

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.ServiceCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment

interface IServiceCommentsUserInteractor {
    fun getUsers(
        serviceComment: ServiceComment,
        serviceCommentsPresenterCallback: ServiceCommentsPresenterCallback
    )
}