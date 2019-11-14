package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IEditableActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.searchService.MainScreenActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.service.ServiceActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.MainScreenView
import com.bunbeauty.ideal.myapplication.helpApi.CircularTransformation
import com.squareup.picasso.Picasso

class TopPanel : Panel() {

    lateinit var title: String
    lateinit var entityId: String
    lateinit var photoLink: String
    lateinit var ownerId: String

    private lateinit var backText: TextView
    private lateinit var titleText: TextView
    private lateinit var logoImage: ImageView
    private lateinit var searchText: TextView
    private lateinit var multiText: TextView
    private lateinit var avatarLayout: LinearLayout
    private lateinit var avatarImage: ImageView

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backTopPanelText -> super.getActivity()!!.onBackPressed()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.top_panel_k, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        backText = view.findViewById(R.id.backTopPanelText)
        titleText = view.findViewById(R.id.titleTopPanelText)
        titleText = view.findViewById(R.id.titleTopPanelText)
        logoImage = view.findViewById(R.id.logoTopPanelImage)
        multiText = view.findViewById(R.id.multiTopPanelText)
        avatarLayout = view.findViewById(R.id.avatarTopPanelLayout)
        avatarImage = view.findViewById(R.id.avatarTopPanelImage)

        if (!super.getActivity()!!.isTaskRoot) {
            backText.visibility = View.VISIBLE
            backText.setOnClickListener(this)
        }

        when (context!!.javaClass.name) {
            ProfileActivity::class.java.name -> {
                setTitleText()
                if (entityId.isNotEmpty()) {
                    setEditText()
                } else {
                    setEmptyMultiText()
                }
            }

            ServiceActivity::class.java.name -> {
                setTitleText()
                if (entityId.isNotEmpty()) {
                    setEditText()
                } else {
                    setOwnerAvatar()
                }
            }

            MainScreenActivity::class.java.name ->{
                setLogoImage()
                setSearchText()
            }
        }
    }

    private fun setTitleText() {
        titleText.text = title
        titleText.visibility = View.VISIBLE
    }

    private fun setEditText() {
        multiText.text = resources.getText(R.string.edit_ico)
        multiText.visibility = View.VISIBLE
        multiText.setOnClickListener {
            (activity as IEditableActivity).goToEditing(entityId)
        }
    }

    private fun setEmptyMultiText() {
        multiText.visibility = View.INVISIBLE
    }

    private fun setOwnerAvatar() {
        avatarLayout.visibility = View.VISIBLE
        avatarLayout.setOnClickListener {
            (activity as ServiceActivity).goToOwnerProfile(ownerId)
        }

        val width = resources.getDimensionPixelSize(R.dimen.photo_width)
        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        Picasso.get()
                .load(photoLink)
                .resize(width, height)
                .centerCrop()
                .transform(CircularTransformation())
                .into(avatarImage)
    }

    private fun setLogoImage(){
        logoImage.visibility = View.VISIBLE
    }

    private fun setSearchText(){
        multiText.text = resources.getText(R.string.search_ico)
        multiText.visibility = View.VISIBLE
        multiText.setOnClickListener {
            (activity as MainScreenView).hideTopPanel()
            (activity as MainScreenView).showSearchPanel()
            (activity as MainScreenView).hideCategory()
        }
    }

}