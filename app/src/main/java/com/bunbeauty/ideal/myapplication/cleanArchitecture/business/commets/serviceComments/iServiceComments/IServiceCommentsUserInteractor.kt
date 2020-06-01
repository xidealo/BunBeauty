package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.iServiceComments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.ServiceCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment

interface IServiceCommentsUserInteractor {
    fun getUsers(
        serviceComment: ServiceComment,
        serviceCommentsPresenterCallback: ServiceCommentsPresenterCallback
    )
}