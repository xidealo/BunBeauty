package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.AddingServiceActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CodeFirebase {
    fun getByCode(code:String){
        val query = FirebaseDatabase.getInstance().getReference(AddingServiceActivity.CODES).orderByChild(AddingServiceActivity.CODE).equalTo(code)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(codesSnapshot: DataSnapshot) {
                if (codesSnapshot.childrenCount == 0L) {
                    //showWrongCode()
                } else {
                    val userSnapshot = codesSnapshot.children.iterator().next()
                    val count = userSnapshot.child(AddingServiceActivity.COUNT).value as Int
                    if (count > 0) {
                        //  setPremium()

                        val codeId = userSnapshot.key!!
                        //other method
                        val myRef = FirebaseDatabase.getInstance()
                                .getReference(AddingServiceActivity.CODES)
                                .child(codeId)
                        val items = HashMap<String, Any>()
                        items[AddingServiceActivity.COUNT] = count - 1
                        myRef.updateChildren(items)
                    } else {
                        //showOldCode()
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}