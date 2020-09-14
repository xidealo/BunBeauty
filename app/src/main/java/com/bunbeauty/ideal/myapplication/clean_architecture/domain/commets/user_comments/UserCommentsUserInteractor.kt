package com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.user_comments

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.user_comments.iUserComments.IUserCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.UserCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.UserRepository

class UserCommentsUserInteractor(
    private val userRepository: UserRepository,
    private val intent: Intent
) : IUserCommentsUserInteractor, UserCallback {

    override fun getCurrentUser() = intent.getSerializableExtra(User.USER) as User
    private lateinit var userCommentsPresenterCallback: UserCommentsPresenterCallback

    override fun getUsers(
        userComment: UserComment,
        userCommentsPresenterCallback: UserCommentsPresenterCallback
    ) {
        this.userCommentsPresenterCallback = userCommentsPresenterCallback
        userRepository.getById(userComment.ownerId, this, true)
    }

    override fun returnGottenObject(element: User?) {
        if (element == null) return

        userCommentsPresenterCallback.setUserOnUserComment(element)
    }
}

