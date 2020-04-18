package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ICodeCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Code
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.CreationServiceActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CodeFirebase {

    fun update(code:Code){
        val myRef = FirebaseDatabase.getInstance()
                .getReference(CreationServiceActivity.CODES)
                .child(code.id)
        val items = HashMap<String, Any>()
        items[CreationServiceActivity.COUNT] =  code.count.toInt()
        myRef.updateChildren(items)
    }

    fun getByCode(codeString: String, callback: ICodeCallback) {
        val query = FirebaseDatabase.getInstance().getReference(CreationServiceActivity.CODES).orderByChild(CreationServiceActivity.CODE).equalTo(codeString)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(codesSnapshot: DataSnapshot) {
                val code = Code()

                if (codesSnapshot.childrenCount == 0L) {
                    code.codeStatus = Code.WRONG_CODE
                    callback.returnCode(code)
                    return
                }

                val userSnapshot = codesSnapshot.children.iterator().next()
                val count = userSnapshot.child(Code.COUNT).value as Long

                if (count > 0L) {
                    code.id = userSnapshot.key!!
                    code.code = codeString
                    code.count = userSnapshot.child(Code.COUNT).value.toString()
                    code.codeStatus = Code.PREMIUM_ACTIVATED
                    callback.returnCode(code)
                } else {
                    code.codeStatus = Code.OLD_CODE
                    callback.returnCode(code)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}