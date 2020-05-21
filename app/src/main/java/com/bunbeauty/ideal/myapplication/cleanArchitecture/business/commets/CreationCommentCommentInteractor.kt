package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.iComments.ICreationCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.comment.InsertCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Comment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.CommentRepository

class CreationCommentCommentInteractor(
    private val commentRepository: CommentRepository,
    private val intent: Intent
) : ICreationCommentCommentInteractor, InsertCommentCallback {

    private lateinit var creationCommentPresenterCallback: CreationCommentPresenterCallback

    fun createComment(
        comment: Comment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    ) {
        this.creationCommentPresenterCallback = creationCommentPresenterCallback
        comment.userId = (intent.getSerializableExtra(User.USER) as User).id
        commentRepository.insert(comment, this)
    }

    override fun returnCreatedCallback(obj: Comment) {
        creationCommentPresenterCallback.showCommentCreated()
    }

}