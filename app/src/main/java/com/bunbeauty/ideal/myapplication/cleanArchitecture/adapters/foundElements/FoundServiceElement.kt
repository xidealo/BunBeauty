package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.foundElements

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.service.ServiceActivity
import com.bunbeauty.ideal.myapplication.helpApi.CircularTransformation
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithViewApi
import com.squareup.picasso.Picasso

class FoundServiceElement(
    private val service: Service,
    private val user: User, private val view: View,
    private val context: Context
) : View.OnClickListener {

    private lateinit var nameUserText: TextView
    private lateinit var cityText: TextView
    private lateinit var nameServiceText: TextView
    private lateinit var costText: TextView
    private lateinit var avatarImage: ImageView
    private lateinit var ratingBar: RatingBar
    private lateinit var layout: LinearLayout

    fun createElement() {
        onViewCreated(view)
    }

    fun onViewCreated(view: View) {

        layout = view.findViewById(R.id.foundServiceElementLayout)
        nameUserText = view.findViewById(R.id.userNameFoundServiceElementText)
        cityText = view.findViewById(R.id.cityFoundServiceElementText)
        nameServiceText = view.findViewById(R.id.serviceNameFoundServiceElementText)
        costText = view.findViewById(R.id.costFoundServiceElementText)
        ratingBar = view.findViewById(R.id.ratingBarFondServiceElement)
        val isPremium = WorkWithTimeApi.checkPremium(service.premiumDate)

        if (isPremium) {
            setPremiumBackground()

            val sl = ColorStateList.valueOf(Color.parseColor("#0093B7"))
            ratingBar.progressTintList = sl
        } else {
            setDefaultBackground()
            val sl = ColorStateList.valueOf(Color.parseColor("#f6db40"))
            ratingBar.progressTintList = sl
        }

        avatarImage = view.findViewById(R.id.avatarFoundServiceElementImage)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(10, 10, 10, 10)
        layout.layoutParams = params

        layout.setOnClickListener(this)
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

    override fun onClick(v: View) {
        goToGuestService()
    }

    private fun goToGuestService() {
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

    companion object {
        private val TAG = "DBInf"
    }
}