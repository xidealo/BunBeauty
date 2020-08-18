package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.service_comments.iServiceComments

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.ServiceCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment

interface IServiceCommentsServiceCommentInteractor {
    fun getServiceCommentsLink(): List<ServiceComment>
    fun createServiceCommentsScreen(
        service: Service,
        serviceCommentsPresenterCallback: ServiceCommentsPresenterCallback
    )

    fun setUserOnServiceComment(
        user: User,
        serviceCommentsPresenterCallback: ServiceCommentsPresenterCallback
    )

}