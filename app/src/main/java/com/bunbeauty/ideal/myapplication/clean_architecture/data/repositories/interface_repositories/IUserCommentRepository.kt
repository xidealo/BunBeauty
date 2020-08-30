package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user_comment.DeleteUserCommentCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user_comment.InsertUserCommentCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user_comment.UpdateUserCommentCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user_comment.UserCommentsCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment

interface IUserCommentRepository {
    fun insert(userComment: UserComment, insertUserCommentCallback: InsertUserCommentCallback)
    fun delete(userComment: UserComment, deleteUserCommentCallback: DeleteUserCommentCallback)
    fun update(userComment: UserComment, updateUserCommentCallback: UpdateUserCommentCallback)
    fun get(userCommentsCallback: UserCommentsCallback)
    fun getByUserId(userId: String, loadingLimit:Int, userCommentsCallback: UserCommentsCallback)
}