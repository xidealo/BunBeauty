package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.WorkWithViewApi
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.CircularTransformation
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.cutStringWithDots
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.firstCapitalSymbol
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.service.ServiceActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.element_service.view.*
import java.util.*

class ServiceElement(
    service: Service,
    user: User,
    weight: Float,
    view: View,
    context: Context
) {
    init {
        if (weight == 1f) {
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
            view.element_service_master_name_tv.text =
                user.name.cutStringWithDots(9)
            view.element_service_service_name_tv.text =
                service.name.cutStringWithDots(14).toUpperCase(Locale.ROOT)
        } else {
            view.element_service_master_name_tv.text =
                user.name.cutStringWithDots(9).firstCapitalSymbol()
            view.element_service_service_name_tv.text =
                service.name.cutStringWithDots(18).toUpperCase(Locale.ROOT)
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