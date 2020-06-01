package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.iServiceComments.IServiceCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.ServiceCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IUserRepository

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

    override fun returnElement(element: User) {
        serviceCommentsPresenterCallback.setUserOnServiceComment(element)
    }

}