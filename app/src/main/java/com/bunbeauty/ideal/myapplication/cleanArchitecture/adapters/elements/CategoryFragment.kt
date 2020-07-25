package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.paris.extensions.style
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.intarfaces.IAdapterSpinner
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : MvpAppCompatFragment(), IAdapterSpinner {

    private val tagsArray: ArrayList<String> = ArrayList()

    fun getTags(): ArrayList<Tag> {
        val tags: ArrayList<Tag> = ArrayList()

        for (tag in tagsArray)
            tags.add(Tag(userId = User.getMyId(), tag = tag))

        return tags
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, null)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        val categories = arrayListOf(*resources.getStringArray(R.array.choice_categories))
        setAdapter(
            categories,
            categorySpinner,
            context!!
        )

        setCategorySpinnerListener()
    }

    private fun setCategorySpinnerListener() {
        categorySpinner.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                tagsArray.clear()
                tagsMaxLayout.removeAllViews()
                if (position > 0)
                    showTags(position)
            }
    }

    private fun showTags(categoryPosition: Int) {
        val tagsArray = resources
            .obtainTypedArray(R.array.tags_references)
            .getTextArray(categoryPosition - 1)

        for (tag in tagsArray) {
            val inflater = LayoutInflater.from(context)
            val view: View = inflater.inflate(R.layout.fragment_tag, tagsMaxLayout, false)
            view.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            val tagText = view.findViewById<TextView>(R.id.tagFragmentTagText)
            tagText.text = tag

            tagText.setOnClickListener {
                tagClick(tagText)
            }

            tagsMaxLayout.addView(view)
        }
    }

    private fun tagClick(tagText: TextView) {
        val text = tagText.text.toString()
        if (tagsArray.contains(text)) {
            setUnpickedTag(tagText)
            tagsArray.remove(text)
        } else {
            setPickedTag(tagText)
            tagsArray.add(text)
        }
    }

    private fun setPickedTag(tagText: TextView): TextView {
        tagText.style(R.style.selected)
        return tagText
    }

    private fun setUnpickedTag(tagText: TextView): TextView {
        tagText.style(R.style.unselected)
        return tagText
    }

    val category: String
        get() = categorySpinner.text.toString()

}