package com.bunbeauty.ideal.myapplication.clean_architecture.data.api

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service_comment.ServiceCommentsCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.google.firebase.database.*

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

    fun getByServiceId(
        userId: String,
        serviceId: String,
        serviceCommentsCallback: ServiceCommentsCallback
    ) {
        val servicesRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(userId)
            .child(Service.SERVICE)
            .child(serviceId)
            .child(ServiceComment.COMMENTS)

        servicesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(serviceCommentsSnapshot: DataSnapshot) {
                val serviceComments = mutableListOf<ServiceComment>()

                for (serviceCommentSnapshot in serviceCommentsSnapshot.children.reversed()) {
                    serviceComments.add(getServiceCommentFromSnapshot(serviceCommentsSnapshot, userId))
                }

                serviceCommentsCallback.returnList(serviceComments)
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    private fun getServiceCommentFromSnapshot(
        serviceCommentSnapshot: DataSnapshot,
        userId: String
    ): ServiceComment {
        val serviceComment = ServiceComment()
        serviceComment.id = serviceCommentSnapshot.key!!
        serviceComment.rating =
            serviceCommentSnapshot.child(UserComment.RATING).value.toString().toFloat()
        serviceComment.review =
            serviceCommentSnapshot.child(UserComment.REVIEW).value as? String ?: ""
        serviceComment.ownerId =
            serviceCommentSnapshot.child(UserComment.OWNER_ID).value as? String ?: ""
        serviceComment.date = serviceCommentSnapshot.child(UserComment.TIME).value as? Long ?: 0L
        serviceComment.userId = userId

        return serviceComment
    }

    fun getIdForNew(userId: String, serviceId: String) =
        FirebaseDatabase.getInstance().getReference(User.USERS)
            .child(userId)
            .child(Service.SERVICES)
            .child(serviceId)
            .child(ServiceComment.COMMENTS).push().key!!
}