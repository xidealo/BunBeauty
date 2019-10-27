package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ICodeSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Code
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.AddingServiceActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CodeFirebase {

    fun update(code:Code){
        val myRef = FirebaseDatabase.getInstance()
                .getReference(AddingServiceActivity.CODES)
                .child(code.id)
        val items = HashMap<String, Any>()
        items[AddingServiceActivity.COUNT] =  code.count.toInt()
        myRef.updateChildren(items)
    }

    fun getByCode(codeString: String, callback: ICodeSubscriber) {
        val query = FirebaseDatabase.getInstance().getReference(AddingServiceActivity.CODES).orderByChild(AddingServiceActivity.CODE).equalTo(codeString)
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