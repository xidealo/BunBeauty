package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces

import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.CircularTransformation
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.part_top_panel.*
import kotlinx.android.synthetic.main.part_top_panel.view.*

interface ITopPanel : IPanel, Toolbar.OnMenuItemClickListener {

    fun initTopPanel(title: String = "", buttonTask: ButtonTask = ButtonTask.NONE) {
        configBackIcon()
        configPanel(buttonTask)
        setTitle(title)
    }

    fun setTitle(title: String) {
        panelContext.top_panel.menu_top_panel_tv_title.text = title
    }

    fun initTopPanel(
        title: String,
        buttonTask: ButtonTask,
        photoLink: String
    ) {
        initTopPanel(title, buttonTask)

        val imageView = panelContext.top_panel.menu_top_panel_iv_avatar
        setAvatar(photoLink, imageView)
    }

    private fun configBackIcon() {
        if (panelContext.isTaskRoot) {
            panelContext.top_panel.navigationIcon = null
        } else {
            panelContext.top_panel.setNavigationOnClickListener {
                panelContext.onBackPressed()
                panelContext.overridePendingTransition(0, 0)
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
                panelContext.top_panel.menu_top_panel_iv_avatar.setOnClickListener {
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
        panelContext.top_panel.menu.findItem(R.id.navigation_action).isVisible = false
    }

    private fun hideImageView() {
        panelContext.top_panel.menu_top_panel_iv_avatar.visibility = View.GONE
    }

    private fun configActionIcon(iconId: Int) {
        panelContext.top_panel.menu.findItem(R.id.navigation_action).icon =
            panelContext.getDrawable(iconId)
        panelContext.top_panel.setOnMenuItemClickListener(this)

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