package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.userComments

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.userComments.iUserComments.IUserCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.UserCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.UserRepository

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

