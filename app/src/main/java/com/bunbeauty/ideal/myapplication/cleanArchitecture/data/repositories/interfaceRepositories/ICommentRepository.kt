package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.comment.CommentsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.comment.DeleteCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.comment.InsertCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.comment.UpdateCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Comment

interface ICommentRepository {
    fun insert(comment: Comment, insertCommentCallback: InsertCommentCallback)
    fun delete(comment: Comment, deleteCommentCallback: DeleteCommentCallback)
    fun update(comment: Comment, updateCommentCallback: UpdateCommentCallback)
    fun get(commentsCallback: CommentsCallback)
    fun getByUserId(userId: String, commentsCallback: CommentsCallback)
}