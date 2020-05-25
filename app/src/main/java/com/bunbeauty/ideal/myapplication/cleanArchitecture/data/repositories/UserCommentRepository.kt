package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.comment.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.CommentFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IUserCommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserCommentRepository(private val commentFirebase: CommentFirebase) : BaseRepository(),
    IUserCommentRepository, UserCommentsCallback, UserCommentCallback {

    private lateinit var userCommentsCallback: UserCommentsCallback
    private lateinit var userCommentCallback: UserCommentCallback

    override fun insertUserComment(
        userComment: UserComment,
        insertUserCommentCallback: InsertUserCommentCallback
    ) {
        launch {
            userComment.id = commentFirebase.getIdForNew(userComment.userId)
            commentFirebase.insert(userComment)
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
            commentFirebase.getByUserId(userId, userCommentsCallback)
        }
    }

    override fun returnList(objects: List<UserComment>) {
        userCommentsCallback.returnList(objects)
    }

    override fun returnElement(element: UserComment) {
        userCommentCallback.returnElement(element)
    }

}