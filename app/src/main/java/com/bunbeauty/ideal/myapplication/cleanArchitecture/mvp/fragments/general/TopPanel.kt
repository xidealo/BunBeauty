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

class TopPanel: Panel() {

    lateinit var title: String
    lateinit var entityId: String

    private lateinit var backText: TextView
    private lateinit var titleText: TextView
    private lateinit var logoImage: ImageView
    private lateinit var multiText: TextView
    private lateinit var avatarLayout: LinearLayout

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backTopPanelText ->  super.getActivity()!!.onBackPressed()
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

        if (!super.getActivity()!!.isTaskRoot) {
            backText.setOnClickListener(this)
        }

        when(context!!.javaClass.name) {
            ProfileActivity::class.java.name -> {
                setTitleText()
                setEditText()
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
}