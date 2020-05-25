package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.UserCommentFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IUserCommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserCommentRepository(private val userCommentFirebase: UserCommentFirebase) :
    BaseRepository(),
    IUserCommentRepository, UserCommentsCallback, UserCommentCallback {

    private lateinit var userCommentsCallback: UserCommentsCallback
    private lateinit var userCommentCallback: UserCommentCallback

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
        this.userCommentsCallback = userCommentsCallback
        launch {
            userCommentFirebase.getByUserId(userId, userCommentsCallback)
        }
    }

    override fun returnList(objects: List<UserComment>) {
        userCommentsCallback.returnList(objects)
    }

    override fun returnElement(element: UserComment) {
        userCommentCallback.returnElement(element)
    }

}