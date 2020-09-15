package com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.service_comments.iServiceComments

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.ServiceCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IServiceCommentsServiceCommentInteractor {
    fun createServiceCommentsScreen(
        service: Service,
        loadingLimit:Int,
        serviceCommentsPresenterCallback: ServiceCommentsPresenterCallback
    )

    fun setUserOnServiceComment(
        user: User,
        serviceCommentsPresenterCallback: ServiceCommentsPresenterCallback
    )

}