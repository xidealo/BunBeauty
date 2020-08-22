package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.profileElements

import android.content.Context
import android.content.Intent
import android.view.View
import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.service.ServiceActivity
import kotlinx.android.synthetic.main.element_profile_order.view.*

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
            this.putExtra(Order.SERVICE_ID, order.serviceId)
            this.putExtra(Order.MASTER_ID, order.masterId)
        }
        context.startActivity(intent)
    }
}