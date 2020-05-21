package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.comment.CommentsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Comment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.google.firebase.database.FirebaseDatabase

class CommentFirebase {

    fun insert(comment: Comment) {

    }


    fun getByUserId(userId: String, commentsCallback: CommentsCallback) {

    }


    fun getIdForNew(userId: String) = FirebaseDatabase.getInstance().getReference(User.USERS)
        .child(userId)
        .child(Comment.COMMENTS).push().key!!
}