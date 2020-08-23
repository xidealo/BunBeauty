package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.profileElements

import android.content.Context
import android.content.Intent
import android.view.View
import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.service.ServiceActivity
import kotlinx.android.synthetic.main.element_profile_service.view.*

class ProfileServiceElement(
    service: Service,
    context: Context,
    view: View
) {
    init {
        view.element_profile_service_tv_name.text = WorkWithStringsApi.firstCapitalSymbol(
            WorkWithStringsApi.cutString(service.name, 26)
        )
        view.element_profile_service_rb_rating.rating = service.rating
        view.element_profile_service_mcv.setOnClickListener {
            goToService(service, context)
        }
    }

    private fun goToService(service: Service, context: Context) {
        val intent = Intent(context, ServiceActivity::class.java)
        intent.putExtra(Service.SERVICE, service)
        context.startActivity(intent)
    }
}
