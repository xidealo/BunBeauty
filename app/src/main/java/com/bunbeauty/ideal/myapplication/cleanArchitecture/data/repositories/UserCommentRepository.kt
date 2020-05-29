package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment.DeleteUserCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment.InsertUserCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment.UpdateUserCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment.UserCommentsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.UserCommentFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IUserCommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserCommentRepository(private val userCommentFirebase: UserCommentFirebase) :
    BaseRepository(), IUserCommentRepository {

    override fun insert(
        userComment: UserComment,
        insertUserCommentCallback: InsertUserCommentCallback
    ) {
        launch {
            userComment.id = userCommentFirebase.getIdForNew(userComment.userId)
            userCommentFirebase.insert(userComment)
            withContext(Dispatchers.Main) {
                insertUserCommentCallback.returnCreatedCallback(userComment)
            }
        }
    }

    override fun delete(
        userComment: UserComment,
        deleteUserCommentCallback: DeleteUserCommentCallback
    ) {

    }

    override fun update(
        userComment: UserComment,
        updateUserCommentCallback: UpdateUserCommentCallback
    ) {

    }

    override fun get(userCommentsCallback: UserCommentsCallback) {

    }

    override fun getByUserId(userId: String, userCommentsCallback: UserCommentsCallback) {
        launch {
            userCommentFirebase.getByUserId(userId, userCommentsCallback)
        }
    }

}