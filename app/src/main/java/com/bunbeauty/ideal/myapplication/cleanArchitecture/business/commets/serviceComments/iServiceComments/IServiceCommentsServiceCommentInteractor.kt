package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.iServiceComments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.ServiceCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment

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