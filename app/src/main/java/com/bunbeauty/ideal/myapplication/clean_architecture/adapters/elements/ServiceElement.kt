package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.service.ServiceActivity
import com.bunbeauty.ideal.myapplication.help_api.WorkWithViewApi
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso

class ServiceElement(
    private val service: Service,
    private val user: User,
    private val view: View,
    private val context: Context
) {

    private lateinit var nameUserText: TextView
    private lateinit var cityText: TextView
    private lateinit var nameServiceText: TextView
    private lateinit var costText: TextView
    private lateinit var avatarImage: ImageView
    private lateinit var ratingBar: RatingBar
    private lateinit var layout: MaterialCardView

    fun createElement() {
        onViewCreated(view)
    }

    private fun onViewCreated(view: View) {

        layout = view.findViewById(R.id.foundServiceElementLayout)
        nameUserText = view.findViewById(R.id.userNameFoundServiceElementText)
        cityText = view.findViewById(R.id.cityFoundServiceElementText)
        nameServiceText = view.findViewById(R.id.serviceNameFoundServiceElementText)
        costText = view.findViewById(R.id.costFoundServiceElementText)
        ratingBar = view.findViewById(R.id.ratingBarFondServiceElement)
        val isPremium = WorkWithTimeApi.checkPremium(service.premiumDate)

        if (isPremium) {
            setPremiumBackground()
            ratingBar.progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow))
        } else {
            setDefaultBackground()
            ratingBar.progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow))
        }

        avatarImage = view.findViewById(R.id.avatarFoundServiceElementImage)

        layout.setOnClickListener {
            goToService()
        }
        setData()
    }

    private fun setData() {
        //устанавливаем сокращения названий и имен в зависимости от размера экрана
        if (isMoreFiveInch()) {
            nameUserText.text = WorkWithStringsApi.cutString(user.name, 9)
            nameServiceText.text = WorkWithStringsApi.cutString(service.name.toUpperCase(), 14)
        } else {
            nameUserText.text =
                WorkWithStringsApi.doubleCapitalSymbols(WorkWithStringsApi.cutString(user.name, 9))
            nameServiceText.text = WorkWithStringsApi.cutString(service.name.toUpperCase(), 18)
        }
        cityText.text = WorkWithStringsApi.firstCapitalSymbol(user.city)
        costText.text = "Цена \n${service.cost}"
        ratingBar.rating = service.rating

        showAvatar()
    }

    private fun showAvatar() {
        val width = context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width)
        val height = context.resources.getDimensionPixelSize(R.dimen.photo_avatar_height)

        Picasso.get()
            .load(user.photoLink)
            .resize(width, height)
            .centerCrop()
            .transform(CircularTransformation())
            .into(avatarImage)
    }

    private fun goToService() {
        val intent = Intent(context, ServiceActivity::class.java)
        intent.putExtra(Service.SERVICE, service)
        intent.putExtra(User.USER, user)
        context.startActivity(intent)
    }

    private fun setPremiumBackground() {
        layout.setBackgroundResource(R.drawable.block_text_premium)
        nameUserText.setBackgroundColor(Color.parseColor("#f6db40"))
        cityText.setBackgroundColor(Color.parseColor("#f6db40"))
        nameServiceText.setBackgroundColor(Color.parseColor("#f6db40"))
        costText.setBackgroundColor(Color.parseColor("#f6db40"))
        ratingBar.setBackgroundColor(Color.parseColor("#f6db40"))
    }

    private fun setDefaultBackground() {
        layout.setBackgroundResource(R.drawable.block_text)
        nameUserText.setBackgroundColor(Color.WHITE)
        cityText.setBackgroundColor(Color.WHITE)
        nameServiceText.setBackgroundColor(Color.WHITE)
        costText.setBackgroundColor(Color.WHITE)
        ratingBar.setBackgroundColor(Color.WHITE)
    }

    private fun isMoreFiveInch() = WorkWithViewApi.getInches(context) < 5

}