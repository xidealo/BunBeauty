package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment.ICreationCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment.InsertUserCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.UserCommentRepository

class CreationCommentUserCommentInteractor(
    private val userCommentRepository: UserCommentRepository,
    private val intent: Intent
) : ICreationCommentCommentInteractor, InsertUserCommentCallback {

    private lateinit var creationCommentPresenterCallback: CreationCommentPresenterCallback

    override fun createUserComment(
        userComment: UserComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    ) {
        this.creationCommentPresenterCallback = creationCommentPresenterCallback
        //сюда передать сообщение из него взять айди
        userComment.userId = (intent.getSerializableExtra(User.USER) as User).id
        userCommentRepository.insert(userComment, this)
    }

    override fun returnCreatedCallback(obj: UserComment) {
        creationCommentPresenterCallback.updateCommentMessage(obj)
    }

}