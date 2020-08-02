package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.foundElements

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.service.ServiceActivity
import com.google.android.material.card.MaterialCardView

class FoundServiceProfileElement(
    private val service: Service,
    private val user: User,
    private val context: Context
) {

    fun createElement(view: View) {
        view.findViewById<TextView>(R.id.serviceNameFoundServiceProfileElementText).text =
            WorkWithStringsApi.firstCapitalSymbol(WorkWithStringsApi.cutString(service.name, 26))

        view.findViewById<RatingBar>(R.id.ratingBarFondServiceProfileElement).rating =
            service.rating

        view.findViewById<MaterialCardView>(R.id.foundServiceProfileElementLayout)
            .setOnClickListener {
                goToService()
            }
    }

    private fun goToService() {
        val intent = Intent(context, ServiceActivity::class.java)
        intent.putExtra(Service.SERVICE, service)
        intent.putExtra(User.USER, user)
        context.startActivity(intent)
    }
}
