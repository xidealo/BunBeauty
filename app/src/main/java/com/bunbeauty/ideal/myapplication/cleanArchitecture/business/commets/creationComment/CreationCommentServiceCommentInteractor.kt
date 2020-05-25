package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment.ICreationCommentServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.serviceComment.InsertServiceCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.ServiceCommentRepository

class CreationCommentServiceCommentInteractor
    (
    private val serviceCommentRepository: ServiceCommentRepository,
    private val intent: Intent
) :
    ICreationCommentServiceCommentInteractor, InsertServiceCommentCallback {

    private lateinit var creationCommentPresenterCallback: CreationCommentPresenterCallback

    override fun createServiceComment(
        serviceComment: ServiceComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    ) {
        this.creationCommentPresenterCallback = creationCommentPresenterCallback
        serviceCommentRepository.insert(serviceComment, this)
    }

    override fun returnCreatedCallback(obj: ServiceComment) {

    }

}