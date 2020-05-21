package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.comment.CommentsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Comment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class CommentFirebase {

    fun insert(comment: Comment) {
        val myRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(comment.userId)
            .child(Comment.COMMENTS)
            .child(comment.id)

        val items = HashMap<String, Any>()
        items[Comment.RATING] = comment.rating
        items[Comment.REVIEW] = comment.review
        items[Comment.TIME] = ServerValue.TIMESTAMP
        items[Comment.OWNER_ID] = comment.ownerId

        myRef.updateChildren(items)
    }

    fun getByUserId(userId: String, commentsCallback: CommentsCallback) {

    }

    fun getIdForNew(userId: String) = FirebaseDatabase.getInstance().getReference(User.USERS)
        .child(userId)
        .child(Comment.COMMENTS).push().key!!
}