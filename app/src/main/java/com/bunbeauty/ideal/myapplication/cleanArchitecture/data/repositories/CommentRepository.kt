package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.comment.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.CommentFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Comment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.ICommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentRepository(private val commentFirebase: CommentFirebase) : BaseRepository(),
    ICommentRepository, CommentsCallback, CommentCallback {

    private lateinit var commentsCallback: CommentsCallback
    private lateinit var commentCallback: CommentCallback

    override fun insert(comment: Comment, insertCommentCallback: InsertCommentCallback) {
        launch {
            comment.id = commentFirebase.getIdForNew(comment.userId)
            commentFirebase.insert(comment)
            withContext(Dispatchers.Main) {
                insertCommentCallback.returnCreatedCallback(comment)
            }
        }
    }

    override fun delete(comment: Comment, deleteCommentCallback: DeleteCommentCallback) {

    }

    override fun update(comment: Comment, updateCommentCallback: UpdateCommentCallback) {

    }

    override fun get(commentsCallback: CommentsCallback) {

    }

    override fun getByUserId(userId: String, commentsCallback: CommentsCallback) {
        launch {
            commentFirebase.getByUserId(userId, commentsCallback)
        }
    }

    override fun returnList(objects: List<Comment>) {
        commentsCallback.returnList(objects)
    }

    override fun returnElement(element: Comment) {
        commentCallback.returnElement(element)
    }

}