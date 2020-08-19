package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.creation_comment

import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.creation_comment.i_creation_comment.ICreationCommentUserCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user_comment.InsertUserCommentCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.UserCommentRepository

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