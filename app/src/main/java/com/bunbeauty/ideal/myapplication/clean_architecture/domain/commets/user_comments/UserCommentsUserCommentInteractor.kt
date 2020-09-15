package com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.user_comments

import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.user_comments.iUserComments.IUserCommentsUserCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.UserCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user_comment.UserCommentsCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IUserCommentRepository

class UserCommentsUserCommentInteractor(private val userCommentRepository: IUserCommentRepository) :
    IUserCommentsUserCommentInteractor, UserCommentsCallback {

    private val cacheUserComments = mutableListOf<UserComment>()
    private var indexCacheUserComment = 0
    private lateinit var userCommentsPresenterCallback: UserCommentsPresenterCallback

    override fun getUserCommentsLink() = cacheUserComments

    override fun getUserComments(
        user: User,
        loadingLimit: Int,
        userCommentsPresenterCallback: UserCommentsPresenterCallback
    ) {
        this.userCommentsPresenterCallback = userCommentsPresenterCallback
        userCommentRepository.getByUserId(user.id, loadingLimit, this)
    }

    override fun returnList(objects: List<UserComment>) {
        if (objects.isEmpty()) userCommentsPresenterCallback.showEmptyScreen()

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
        userCommentsPresenterCallback.updateUserComment(cacheUserComments[indexCacheUserComment])
        indexCacheUserComment++
    }
}