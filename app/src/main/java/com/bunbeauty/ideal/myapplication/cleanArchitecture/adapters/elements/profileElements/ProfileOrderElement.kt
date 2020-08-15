package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.profileElements

import android.content.Context
import android.content.Intent
import android.view.View
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.service.ServiceActivity
import kotlinx.android.synthetic.main.element_order.view.*

class ProfileOrderElement(private val order: Order, private val context: Context) {

    fun createElement(view: View) {
        view.serviceNameProfileOrderElementText.text = WorkWithStringsApi.firstCapitalSymbol(
            WorkWithStringsApi.cutString(order.serviceName, 26)
        )
        view.durationProfileOrderElementText.text = order.session.toString()
        view.profileOrderElementLayout.setOnClickListener {
            goToService()
        }
    }

    private fun goToService() {
        val intent = Intent(context, ServiceActivity::class.java).apply {
            this.putExtra(Service.SERVICE_ID, order.serviceId)
        }
        context.startActivity(intent)
    }
}