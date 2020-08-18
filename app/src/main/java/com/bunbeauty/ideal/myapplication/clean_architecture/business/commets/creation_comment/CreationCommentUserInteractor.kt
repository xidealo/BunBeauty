package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.creation_comment

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.creation_comment.iCreationComment.ICreationCommentUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UpdateUsersCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.UserRepository

/**
 * Can happen situation when we get old user data
 * So next update it will set listener on User!
 */
class CreationCommentUserInteractor(
    private val intent: Intent,
    private val userRepository: UserRepository
) : ICreationCommentUserInteractor, UserCallback, UpdateUsersCallback {

    private lateinit var creationCommentPresenterCallback: CreationCommentPresenterCallback
    private lateinit var cacheUserComment: UserComment
    private var user: User? = null

    override fun getUser(): User {
        if (user == null) {
            user = intent.getSerializableExtra(User.USER) as User
        }

        return user ?: User()
    }

    override fun updateUser(
        user: User,
        userComment: UserComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    ) {
        this.creationCommentPresenterCallback = creationCommentPresenterCallback
        cacheUserComment = userComment
        userRepository.getById(user.id, this, true)
    }

    /**
     * for actual data
     */
    override fun returnGottenObject(element: User?) {
        if (element == null) return

        element.rating = calculateAvgRating(element, cacheUserComment)
        element.countOfRates++
        user = element
        userRepository.update(element, this)
    }

    override fun returnUpdatedCallback(obj: User) {
        creationCommentPresenterCallback.updateUserCommentMessage(cacheUserComment)
    }

    private fun calculateAvgRating(user: User, userComment: UserComment): Float {
        return (user.rating * user.countOfRates + userComment.rating) / (user.countOfRates + 1)
    }

}