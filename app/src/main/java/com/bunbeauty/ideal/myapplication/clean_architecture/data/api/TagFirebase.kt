package com.bunbeauty.ideal.myapplication.clean_architecture.data.api

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Tag
import com.google.firebase.database.FirebaseDatabase

class TagFirebase {
    fun insert(tag: Tag) {
        val database = FirebaseDatabase.getInstance()
        val tagRef = database
            .getReference(Service.SERVICES)
            .child(tag.userId)
            .child(tag.serviceId)
            .child(Tag.TAGS)
            .child(tag.id)

        val items = HashMap<String, Any>()
        items[Tag.TAG] = tag.tag
        tagRef.updateChildren(items)
    }

    fun delete(tag: Tag) {
        val subscriberRef = FirebaseDatabase.getInstance()
            .getReference(Service.SERVICES)
            .child(tag.userId)
            .child(tag.serviceId)
            .child(Tag.TAGS)
            .child(tag.id)

        subscriberRef.removeValue()
    }

    fun update(tag: Tag) {

    }

    fun get(): List<Tag> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getIdForNew(userId: String, serviceId: String): String {
        return FirebaseDatabase.getInstance()
            .getReference(Service.SERVICES)
            .child(userId)
            .child(serviceId)
            .child(Tag.TAGS).push().key!!
    }
}