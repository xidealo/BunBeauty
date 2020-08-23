package com.bunbeauty.ideal.myapplication.clean_architecture.data.api

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.code.GetCodeCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.code.UpdateCodeCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Code
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.create_service.CreationServiceActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CodeFirebase {

    fun update(code: Code, codeCallback: UpdateCodeCallback) {
        val myRef = FirebaseDatabase.getInstance()
            .getReference(CODES)
            .child(code.id)
        val items = HashMap<String, Any>()
        items[COUNT] = code.count
        myRef.updateChildren(items)
        codeCallback.returnUpdatedCallback(code)
    }

    fun getByCode(codeString: String, callback: GetCodeCallback) {
        val query = FirebaseDatabase.getInstance().getReference(CODES)
            .orderByChild(CODE).equalTo(codeString)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(codesSnapshot: DataSnapshot) {
                val code = Code()

                if (codesSnapshot.childrenCount == 0L) {
                    code.codeStatus = Code.WRONG_CODE
                    callback.returnList(listOf(code))
                    return
                }

                val userSnapshot = codesSnapshot.children.iterator().next()
                val count = userSnapshot.child(Code.COUNT).value as Long

                if (count > 0L) {
                    code.id = userSnapshot.key!!
                    code.code = codeString
                    code.count = userSnapshot.child(Code.COUNT).value.toString().toInt()
                    code.codeStatus = Code.PREMIUM_ACTIVATED
                } else {
                    code.codeStatus = Code.OLD_CODE
                }
                callback.returnList(listOf(code))
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    companion object {
        const val CODES = "codes"
        const val CODE = "code"
        const val COUNT = "count"
    }
}