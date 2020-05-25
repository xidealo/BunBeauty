package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class ServiceCommentFirebase {

    fun insert(serviceComment: ServiceComment) {
        val myRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(serviceComment.userId)
            .child(Service.SERVICES)
            .child(serviceComment.serviceId)
            .child(UserComment.COMMENTS)
            .child(serviceComment.id)

        val items = HashMap<String, Any>()
        items[UserComment.RATING] = serviceComment.rating
        items[UserComment.REVIEW] = serviceComment.review
        items[UserComment.TIME] = ServerValue.TIMESTAMP
        items[UserComment.OWNER_ID] = serviceComment.ownerId

        myRef.updateChildren(items)
    }

    fun getIdForNew(userId: String, serviceId: String) =
        FirebaseDatabase.getInstance().getReference(User.USERS)
            .child(userId)
            .child(Service.SERVICES)
            .child(serviceId)
            .child(UserComment.COMMENTS).push().key!!
}