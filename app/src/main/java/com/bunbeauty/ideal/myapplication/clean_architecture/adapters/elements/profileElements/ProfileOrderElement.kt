package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.profileElements

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.service.ServiceActivity
import kotlinx.android.synthetic.main.element_profile_order.view.*

class ProfileOrderElement(
    order: Order,
    context: Context,
    view: View
) {

    init {
        view.serviceNameProfileOrderElementText.text = WorkWithStringsApi.firstCapitalSymbol(
            WorkWithStringsApi.cutStringWithDots(order.serviceName, 26)
        )
        view.durationProfileOrderElementText.text = order.session.toString()
        view.profileOrderElementLayout.setOnClickListener {
            goToService(order, context)
        }
    }

    private fun goToService(order: Order, context: Context) {
        val intent = Intent(context, ServiceActivity::class.java).apply {
            putExtra(Order.SERVICE_ID, order.serviceId)
            putExtra(Order.MASTER_ID, order.masterId)
        }
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(0, 0)
    }
}