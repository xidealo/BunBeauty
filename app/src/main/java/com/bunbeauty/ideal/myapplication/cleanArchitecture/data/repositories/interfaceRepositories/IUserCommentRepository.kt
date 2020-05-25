package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment.DeleteUserCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment.InsertUserCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment.UpdateUserCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment.UserCommentsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

interface IUserCommentRepository {
    fun insert(userComment: UserComment, insertUserCommentCallback: InsertUserCommentCallback)
    fun delete(userComment: UserComment, deleteUserCommentCallback: DeleteUserCommentCallback)
    fun update(userComment: UserComment, updateUserCommentCallback: UpdateUserCommentCallback)
    fun get(userCommentsCallback: UserCommentsCallback)
    fun getByUserId(userId: String, userCommentsCallback: UserCommentsCallback)
}