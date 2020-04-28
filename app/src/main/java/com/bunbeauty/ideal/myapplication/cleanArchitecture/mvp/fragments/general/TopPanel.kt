package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.helpApi.CircularTransformation
import com.squareup.picasso.Picasso

class TopPanel : Panel() {

    lateinit var title: String
    lateinit var buttonTask: ButtonTask
    lateinit var photoLink: String

    private lateinit var backText: TextView

    private lateinit var titleText: TextView
    private lateinit var logoImage: ImageView

    private lateinit var editText: TextView
    private lateinit var searchText: TextView
    private lateinit var avatarImage: ImageView
    private lateinit var avatarLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            title = it.getString(TITLE)!!
            buttonTask = it.getSerializable(BUTTON_TASK)!! as ButtonTask

            if (it.getSerializable(PHOTO_LINK) != null) {
                photoLink = it.getString(PHOTO_LINK)!!
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.top_panel_k, container, false)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.backTopPanelText -> {
                activity!!.onBackPressed()
                activity!!.overridePendingTransition(0, 0)
            }
            else -> {
                (activity!! as ITopPanel).iconClick()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view)
        configBackIcon()
        configTitle()
        configMultiIcon()
    }

    private fun init(view: View) {
        backText = view.findViewById(R.id.backTopPanelText)

        titleText = view.findViewById(R.id.titleTopPanelText)
        logoImage = view.findViewById(R.id.logoTopPanelImage)

        editText = view.findViewById(R.id.editTopPanelText)
        searchText = view.findViewById(R.id.searchTopPanelText)
        avatarImage = view.findViewById(R.id.avatarTopPanelImage)
        avatarLayout = view.findViewById(R.id.avatarTopPanelLayout)

        editText.setOnClickListener(this)
        searchText.setOnClickListener(this)
        avatarLayout.setOnClickListener(this)
    }

    private fun configBackIcon() {
        if (super.getActivity()!!.isTaskRoot) {
            backText.visibility = View.INVISIBLE
        } else {
            backText.setOnClickListener(this)
        }
    }

    private fun configTitle() {
        if (title.isEmpty()) {
            logoImage.visibility = View.VISIBLE
            titleText.visibility = View.GONE
        } else {
            titleText.text = title
        }
    }

    private fun configMultiIcon() {
        when (buttonTask) {
            ButtonTask.EDIT -> {
                showEditIcon()
            }
            ButtonTask.SEARCH -> {
                showSearchIcon()
            }
            ButtonTask.GO_TO_PROFILE -> {
                setAvatar()
            }
            ButtonTask.NONE -> {
                hideIcon()
            }
        }
    }

    private fun showEditIcon() {
        editText.visibility = View.VISIBLE
        searchText.visibility = View.GONE
        avatarLayout.visibility = View.GONE
    }

    private fun showSearchIcon() {
        editText.visibility = View.GONE
        searchText.visibility = View.VISIBLE
        avatarLayout.visibility = View.GONE
    }

    private fun setAvatar() {
        editText.visibility = View.GONE
        searchText.visibility = View.GONE
        avatarLayout.visibility = View.VISIBLE

    /*    val width = resources.getDimensionPixelSize(R.dimen.photo_width)
        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        Picasso.get()
            .load(photoLink)
            .resize(width, height)
            .centerCrop()
            .transform(CircularTransformation())
            .into(avatarImage)*/
    }

    private fun hideIcon() {
        editText.visibility = View.INVISIBLE
        searchText.visibility = View.GONE
        avatarLayout.visibility = View.GONE
    }

    companion object {
        private const val TITLE = "title"
        private const val BUTTON_TASK = "button task"
        private const val PHOTO_LINK = "photo link"

        @JvmStatic
        fun newInstance(title: String, buttonTask: ButtonTask) =
            TopPanel().apply {
                arguments = Bundle().apply {
                    putString(TITLE, title)
                    putSerializable(BUTTON_TASK, buttonTask)
                }
            }

        @JvmStatic
        fun newInstance(title: String, buttonTask: ButtonTask, photoLink: String) =
            TopPanel().apply {
                arguments = Bundle().apply {
                    putString(TITLE, title)
                    putSerializable(BUTTON_TASK, buttonTask)
                    putSerializable(PHOTO_LINK, photoLink)
                }
            }
    }

}