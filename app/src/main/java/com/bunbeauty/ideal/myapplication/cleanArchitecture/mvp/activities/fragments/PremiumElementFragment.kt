package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.fragments.PremiumElementFragmentView
import com.bunbeauty.ideal.myapplication.other.IPremium

class PremiumElementFragment @SuppressLint("ValidFragment")
constructor() : MvpAppCompatFragment(), View.OnClickListener, PremiumElementFragmentView {

    private lateinit var iPremium: IPremium
    private lateinit var code: String
    private lateinit var codeText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.premium_element, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val setPremiumBtn = view.findViewById<Button>(R.id.setPremiumPremiumElementBtn)
        codeText = view.findViewById(R.id.codePremiumElement)
        setPremiumBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.setPremiumPremiumElementBtn) {
            code = codeText.text.toString().toLowerCase().trim { it <= ' ' }
            iPremium.checkCode(code)
        }
    }

    companion object {
        private val TAG = "DBInf"
    }

    /* fun addSevenDayPremium(date: String): String {
        var sysdateLong = WorkWithTimeApi.getMillisecondsStringDateWithSeconds(date)
        //86400000 - day * 7 day
        sysdateLong += (86400000 * 7).toLong()
        return WorkWithTimeApi.getDateInFormatYMDHMS(Date(sysdateLong))
    }*/

   /* fun checkCode(code: String) {
        //проверка кода
        val query = FirebaseDatabase.getInstance().getReference(AddingServiceActivity.CODES).orderByChild(AddingServiceActivity.CODE).equalTo(code)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(codesSnapshot: DataSnapshot) {
                if (codesSnapshot.childrenCount == 0L) {
                    showWrongCode()
                } else {
                    val userSnapshot = codesSnapshot.children.iterator().next()
                    val count = userSnapshot.child(AddingServiceActivity.COUNT).value as Int
                    if (count > 0) {
                        setPremium()

                        val codeId = userSnapshot.key!!

                        val myRef = FirebaseDatabase.getInstance()
                                .getReference(AddingServiceActivity.CODES)
                                .child(codeId)
                        val items = HashMap<String, Any>()
                        items[AddingServiceActivity.COUNT] = count - 1
                        myRef.updateChildren(items)
                    } else {
                        showOldCode()
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }*/

}
