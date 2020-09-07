package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.service.ServiceActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.WorkWithViewApi
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.firstCapitalSymbol
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.element_service.view.*

class ServiceElement(
    service: Service,
    user: User,
    view: View,
    context: Context
) {
    init {
        val isPremium = WorkWithTimeApi.checkPremium(service.premiumDate)
        if (isPremium) {
            setPremiumColor(view, context)
        } else {
            setDefaultBackground(view, context)
        }

        setData(view, service, user, context)

        view.element_service_mcv.setOnClickListener {
            goToService(user, service, context)
        }
    }

    private fun setData(view: View, service: Service, user: User, context: Context) {
        if (isMoreFiveInch(context)) {
            view.element_service_master_name_tv.text = WorkWithStringsApi.cutStringWithDots(user.name, 9)
            view.element_service_service_name_tv.text =
                WorkWithStringsApi.cutStringWithDots(service.name.toUpperCase(), 14)
        } else {
            view.element_service_master_name_tv.text =
                WorkWithStringsApi.doubleCapitalSymbols(WorkWithStringsApi.cutStringWithDots(user.name, 9))
            view.element_service_service_name_tv.text =
                WorkWithStringsApi.cutStringWithDots(service.name.toUpperCase(), 18)
        }
        view.element_service_city_tv.text = user.city.firstCapitalSymbol()
        view.element_service_cost_tv.text = "${service.cost} ₽"
        view.element_service_rating_rb.rating = service.rating

        showAvatar(user, view, context)
    }

    private fun showAvatar(user: User, view: View, context: Context) {
        val width = context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width)
        val height = context.resources.getDimensionPixelSize(R.dimen.photo_avatar_height)

        Picasso.get()
            .load(user.photoLink)
            .resize(width, height)
            .centerCrop()
            .transform(CircularTransformation())
            .into(view.element_service_avatar_iv)
    }

    private fun goToService(user: User, service: Service, context: Context) {
        val intent = Intent(context, ServiceActivity::class.java)
        intent.putExtra(Service.SERVICE, service)
        intent.putExtra(User.USER, user)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(0, 0)
    }

    private fun setPremiumColor(view: View, context: Context) {
        view.element_service_ll.setBackgroundResource(R.color.yellow)
        view.element_service_rating_rb.supportProgressTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.mainBlue))
    }

    private fun setDefaultBackground(view: View, context: Context) {
        view.element_service_ll.setBackgroundResource(R.color.white)
        view.element_service_rating_rb.supportProgressTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow))
    }

    private fun isMoreFiveInch(context: Context) = WorkWithViewApi.getInches(context) < 5
}