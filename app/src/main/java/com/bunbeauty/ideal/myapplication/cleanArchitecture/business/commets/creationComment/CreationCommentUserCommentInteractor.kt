package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment.ICreationCommentUserCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment.InsertUserCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.UserCommentRepository

class CreationCommentUserCommentInteractor(
    private val userCommentRepository: UserCommentRepository
) : ICreationCommentUserCommentInteractor, InsertUserCommentCallback {

    private lateinit var creationCommentPresenterCallback: CreationCommentPresenterCallback

    override fun createUserComment(
        userComment: UserComment,
        user: User,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    ) {
        this.creationCommentPresenterCallback = creationCommentPresenterCallback
        userComment.userId = user.id
        userCommentRepository.insert(userComment, this)
    }

    override fun returnCreatedCallback(obj: UserComment) {
        creationCommentPresenterCallback.updateUserRating(obj)
    }

}