package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.service_comments

import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.service_comments.iServiceComments.IServiceCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.ServiceCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IUserRepository

class ServiceCommentsUserInteractor(private val userRepository: IUserRepository) :
    IServiceCommentsUserInteractor, UserCallback {

    private lateinit var serviceCommentsPresenterCallback: ServiceCommentsPresenterCallback

    override fun getUsers(
        serviceComment: ServiceComment,
        serviceCommentsPresenterCallback: ServiceCommentsPresenterCallback
    ) {
        this.serviceCommentsPresenterCallback = serviceCommentsPresenterCallback
        userRepository.getById(serviceComment.ownerId, this, true)
    }

    override fun returnGottenObject(element: User?) {
        if (element == null) return

        serviceCommentsPresenterCallback.setUserOnServiceComment(element)
    }

}