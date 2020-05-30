package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment.UserCommentsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.google.firebase.database.*

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

        val servicesRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(userId)
            .child(UserComment.COMMENTS)

        servicesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userCommentsSnapshot: DataSnapshot) {
                val userComments = mutableListOf<UserComment>()

                for (userCommentSnapshot in userCommentsSnapshot.children.reversed()) {
                    userComments.add(getUserCommentFromSnapshot(userCommentSnapshot, userId))
                }

                userCommentsCallback.returnList(userComments)
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })

    }

    private fun getUserCommentFromSnapshot(
        userCommentSnapshot: DataSnapshot,
        userId: String
    ): UserComment {
        val userComment = UserComment()
        userComment.id = userCommentSnapshot.key!!
        userComment.rating = userCommentSnapshot.child(UserComment.RATING).value.toString().toFloat()
        userComment.review = userCommentSnapshot.child(UserComment.REVIEW).value as? String ?: ""
        userComment.ownerId = userCommentSnapshot.child(UserComment.OWNER_ID).value as? String ?: ""
        userComment.time = userCommentSnapshot.child(UserComment.TIME).value as? Long ?: 0L
        userComment.userId = userId

        return userComment
    }

    fun getIdForNew(userId: String) = FirebaseDatabase.getInstance().getReference(User.USERS)
        .child(userId)
        .child(UserComment.COMMENTS).push().key!!
}