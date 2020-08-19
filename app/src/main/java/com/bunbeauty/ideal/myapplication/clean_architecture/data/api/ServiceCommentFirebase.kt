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
            .getReference(ServiceComment.SERVICE_COMMENTS)
            .child(serviceComment.userId)
            .child(serviceComment.serviceId)
            .child(serviceComment.id)

        val items = HashMap<String, Any>()
        items[ServiceComment.RATING] = serviceComment.rating
        items[ServiceComment.REVIEW] = serviceComment.review
        items[ServiceComment.TIME] = ServerValue.TIMESTAMP
        items[ServiceComment.OWNER_ID] = serviceComment.ownerId

        myRef.updateChildren(items)
    }

    fun getByServiceId(
        userId: String,
        serviceId: String,
        serviceCommentsCallback: ServiceCommentsCallback
    ) {
        val servicesRef = FirebaseDatabase.getInstance()
            .getReference(ServiceComment.SERVICE_COMMENTS)
            .child(userId)
            .child(serviceId)

        servicesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(serviceCommentsSnapshot: DataSnapshot) {
                val serviceComments = mutableListOf<ServiceComment>()

                for (serviceCommentSnapshot in serviceCommentsSnapshot.children.reversed()) {
                    serviceComments.add(getServiceCommentFromSnapshot(serviceCommentSnapshot, userId))
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
        FirebaseDatabase.getInstance().getReference(ServiceComment.SERVICE_COMMENTS)
            .child(userId)
            .child(serviceId).push().key!!
}