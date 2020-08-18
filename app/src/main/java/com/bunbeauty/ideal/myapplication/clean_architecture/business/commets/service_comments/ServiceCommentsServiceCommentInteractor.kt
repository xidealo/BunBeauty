package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.service_comments

import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.service_comments.iServiceComments.IServiceCommentsServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.ServiceCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service_comment.ServiceCommentsCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceCommentRepository

class ServiceCommentsServiceCommentInteractor(private val serviceCommentRepository: IServiceCommentRepository) :
    IServiceCommentsServiceCommentInteractor, ServiceCommentsCallback {

    private var cacheServiceComments = mutableListOf<ServiceComment>()
    private lateinit var serviceCommentsPresenterCallback: ServiceCommentsPresenterCallback
    private var indexCacheServiceComment = 0

    override fun getServiceCommentsLink() = cacheServiceComments

    override fun createServiceCommentsScreen(
        service: Service,
        serviceCommentsPresenterCallback: ServiceCommentsPresenterCallback
    ) {
        this.serviceCommentsPresenterCallback = serviceCommentsPresenterCallback
        serviceCommentRepository.getByServiceId(service.userId, service.id, this)
    }

    override fun returnList(objects: List<ServiceComment>) {
        if (objects.isEmpty()) serviceCommentsPresenterCallback.showEmptyScreen()

        cacheServiceComments.addAll(objects)

        for (serviceComment in objects) {
            serviceCommentsPresenterCallback.getUser(serviceComment)
        }
    }

    /**
     * set user to cache service comment
     *
     * when we set last user, we update screen
     */
    override fun setUserOnServiceComment(
        user: User,
        serviceCommentsPresenterCallback: ServiceCommentsPresenterCallback
    ) {
        cacheServiceComments[indexCacheServiceComment].user = user
        indexCacheServiceComment++

        if (indexCacheServiceComment == cacheServiceComments.size - 1)
            serviceCommentsPresenterCallback.updateServiceComments(cacheServiceComments)
    }

}