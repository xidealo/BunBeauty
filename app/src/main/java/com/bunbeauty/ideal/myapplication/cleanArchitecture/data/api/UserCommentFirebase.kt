package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment.UserCommentsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class UserCommentFirebase {

    fun insert(userComment: UserComment) {
        val myRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(userComment.userId)
            .child(UserComment.COMMENTS)
            .child(userComment.id)

        val items = HashMap<String, Any>()
        items[UserComment.RATING] = userComment.rating
        items[UserComment.REVIEW] = userComment.review
        items[UserComment.TIME] = ServerValue.TIMESTAMP
        items[UserComment.OWNER_ID] = userComment.ownerId

        myRef.updateChildren(items)
    }

    fun getByUserId(userId: String, userCommentsCallback: UserCommentsCallback) {

    }

    fun getIdForNew(userId: String) = FirebaseDatabase.getInstance().getReference(User.USERS)
        .child(userId)
        .child(UserComment.COMMENTS).push().key!!
}