package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.userComments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.userComments.iUserComments.IUserCommentsUserCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.UserCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment.UserCommentsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.UserCommentRepository

class UserCommentsUserCommentInteractor(private val userCommentRepository: UserCommentRepository) :
    IUserCommentsUserCommentInteractor, UserCommentsCallback {

    private val cacheUserComments = mutableListOf<UserComment>()
    private var indexCacheUserComment = 0
    private lateinit var userCommentsPresenterCallback: UserCommentsPresenterCallback

    override fun getUserCommentsLink() = cacheUserComments

    override fun getUserComments(
        user: User,
        userCommentsPresenterCallback: UserCommentsPresenterCallback
    ) {
        this.userCommentsPresenterCallback = userCommentsPresenterCallback
        userCommentRepository.getByUserId(user.id, this)
    }

    override fun returnList(objects: List<UserComment>) {
        if(objects.isEmpty()) userCommentsPresenterCallback.showEmptyScreen()

        cacheUserComments.addAll(objects)

        for (userComment in objects) {
            userCommentsPresenterCallback.getUser(userComment)
        }
    }

    /**
     * set user to cache user comment
     *
     * when we set last user, we update screen
     */
    override fun setUserOnUserComment(
        user: User,
        userCommentsPresenterCallback: UserCommentsPresenterCallback
    ) {
        cacheUserComments[indexCacheUserComment].user = user
        indexCacheUserComment++

        if (indexCacheUserComment == cacheUserComments.size - 1)
            userCommentsPresenterCallback.updateUserComments()
    }
}