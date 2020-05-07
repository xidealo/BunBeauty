package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.helpApi.CircularTransformation
import com.squareup.picasso.Picasso

class SubscriptionElement(
    private val view: View,
    private val context: Context
) : View.OnClickListener {

    private lateinit var nameText: TextView
    private lateinit var subscribeText: TextView
    private lateinit var unsubscribeText: TextView
    private lateinit var avatarImage: ImageView
    private lateinit var user: User

    fun createElement(user: User) {
        onViewCreated(view)
        setData(user)
        showAvatar(user)
    }

    private fun onViewCreated(view: View) {
        nameText = view.findViewById(R.id.workerNameSubscriptionElementText)
        subscribeText = view.findViewById(R.id.subscribeSubscriptionElementText)
        avatarImage =
            view.findViewById(R.id.avatarSubscriptionElementImage)
        unsubscribeText = view.findViewById(R.id.unsubscribeSubscriptionElementText)

        val layout = view.findViewById<LinearLayout>(R.id.subscriptionElementLayout)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(10, 10, 10, 10)
        layout.layoutParams = params
    }

    private fun setData(user: User) {
        this.user = user
        nameText.text = "${user.name} ${user.surname}"
    }

    private fun showAvatar(user: User) {
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
        when (v.id) {
            R.id.subscribeSubscriptionElementText -> {
                unsubscribeText.visibility = View.VISIBLE
                subscribeText.visibility = View.GONE
                Toast.makeText(context, "Подписка отменена", Toast.LENGTH_SHORT).show()
            }
            R.id.unsubscribeSubscriptionElementText -> {
                unsubscribeText.visibility = View.GONE
                subscribeText.visibility = View.VISIBLE
                Toast.makeText(context, "Вы подписались", Toast.LENGTH_SHORT).show()
            }
            else -> goToProfile(user)
        }
    }

    private fun goToProfile(user: User) {
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra(User.USER, user)
        context.startActivity(intent)
    }
}