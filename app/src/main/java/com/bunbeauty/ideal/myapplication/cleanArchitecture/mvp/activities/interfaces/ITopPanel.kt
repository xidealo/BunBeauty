package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces

import android.app.Activity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.CircularTransformation
import com.google.android.material.appbar.MaterialToolbar
import com.squareup.picasso.Picasso

interface   ITopPanel : IPanel, Toolbar.OnMenuItemClickListener {

    var topPanel: MaterialToolbar

    fun initTopPanel(buttonTask: ButtonTask) {
        topPanel = (panelContext as Activity).findViewById(R.id.topPanelLayout)

        configBackIcon()
        configPanel(buttonTask)
    }

    fun initTopPanel(title: String, buttonTask: ButtonTask) {
        initTopPanel(buttonTask)
        val titleText = topPanel.findViewById<TextView>(R.id.titleTopPanelText)
        titleText.text = title
    }

    fun initTopPanel(
        title: String,
        buttonTask: ButtonTask,
        photoLink: String
    ) {
        initTopPanel(title, buttonTask)

        val imageView = topPanel.findViewById<ImageView>(R.id.avatarTopPanelImage)
        setAvatar(photoLink, imageView)
    }

    private fun configBackIcon() {
        if ((panelContext as Activity).isTaskRoot) {
            topPanel.navigationIcon = null
        } else {
            topPanel.setNavigationOnClickListener {
                (panelContext as Activity).onBackPressed()
                (panelContext as Activity).overridePendingTransition(0, 0)
            }
        }
    }

    private fun configPanel(buttonTask: ButtonTask) {
        when (buttonTask) {
            ButtonTask.NONE -> {
                hideActionIcon()
                hideImageView()
            }
            ButtonTask.EDIT -> {
                configActionIcon(R.drawable.icon_edit)
            }
            ButtonTask.GO_TO_PROFILE -> {
                hideActionIcon()
                topPanel.findViewById<ImageView>(R.id.avatarTopPanelImage).setOnClickListener {
                    actionClick()
                }
            }
            ButtonTask.SEARCH -> {
                configActionIcon(R.drawable.icon_search)
            }
            ButtonTask.LOGOUT -> {
                configActionIcon(R.drawable.icon_logout)
            }
        }
    }

    private fun hideActionIcon() {
        topPanel.menu.findItem(R.id.navigation_action).isVisible = false
    }

    private fun hideImageView() {
        topPanel.findViewById<ImageView>(R.id.avatarTopPanelImage).visibility = View.GONE
    }

    private fun configActionIcon(iconId: Int) {
        topPanel.menu.findItem(R.id.navigation_action).icon = panelContext.getDrawable(iconId)
        topPanel.setOnMenuItemClickListener(this)

        hideImageView()
    }

    private fun setAvatar(photoLink: String, imageView: ImageView) {
        val width = panelContext.resources.getDimensionPixelSize(R.dimen.photo_width)
        val height = panelContext.resources.getDimensionPixelSize(R.dimen.photo_height)
        Picasso.get()
            .load(photoLink)
            .resize(width, height)
            .centerCrop()
            .transform(CircularTransformation())
            .into(imageView)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_action -> {
                actionClick()
                true
            }
            else -> {
                false
            }
        }
    }

    fun actionClick() {}
}