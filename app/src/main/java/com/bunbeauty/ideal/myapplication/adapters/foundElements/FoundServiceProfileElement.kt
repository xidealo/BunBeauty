package com.bunbeauty.ideal.myapplication.adapters.foundElements

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView

import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.service.ServiceActivity

class FoundServiceProfileElement(private val service: Service,
                                 private val context: Context) : View.OnClickListener {

    fun createElement(view: View) {
        view.findViewById<TextView>(R.id.serviceNameFoundServiceProfileElementText).text =
                WorkWithStringsApi.firstCapitalSymbol(WorkWithStringsApi.cutString(service.name, 26))

        view.findViewById<RatingBar>(R.id.ratingBarFondServiceProfileElement).rating = service.rating

        view.findViewById<LinearLayout>(R.id.foundServiceProfileElementLayout)
                .setOnClickListener(this)
    }

    override fun onClick(v: View) {
        goToGuestService()
    }

    private fun goToGuestService() {
        val intent = Intent(context, ServiceActivity::class.java)
        intent.putExtra(Service.SERVICE_ID, service.id)
        intent.putExtra(Service.USER_ID, (context as ProfileActivity).getOwnerId())
        context.startActivity(intent)
    }

    companion object {
        private val TAG = "DBInf"
    }
}
