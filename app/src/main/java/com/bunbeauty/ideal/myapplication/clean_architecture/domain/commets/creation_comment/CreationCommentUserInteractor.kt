package com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.creation_comment

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.creation_comment.i_creation_comment.ICreationCommentUserInteractor
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
class CreationCommentUserInteractor(private val userRepository: UserRepository
) : ICreationCommentUserInteractor, UserCallback, UpdateUsersCallback {

    private lateinit var creationCommentPresenterCallback: CreationCommentPresenterCallback
    private lateinit var cacheUserComment: UserComment
    private var user: User? = null

    override fun getUser(intent: Intent): User {
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
    override fun returnGottenObject(obj: User?) {
        if (obj == null) return

        obj.rating = calculateAvgRating(obj, cacheUserComment)
        obj.countOfRates++
        user = obj
        userRepository.update(obj, this)
    }

    override fun returnUpdatedCallback(obj: User) {
        creationCommentPresenterCallback.updateUserCommentMessage(cacheUserComment)
    }

    private fun calculateAvgRating(user: User, userComment: UserComment): Float {
        return (user.rating * user.countOfRates + userComment.rating) / (user.countOfRates + 1)
    }

}