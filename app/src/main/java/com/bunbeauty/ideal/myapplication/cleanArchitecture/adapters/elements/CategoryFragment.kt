package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.paris.extensions.style
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.intarfaces.IAdapterSpinner
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.intarfaces.SpinnerSelectable
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : MvpAppCompatFragment(), IAdapterSpinner, SpinnerSelectable {

    private val cacheSelectedTags: ArrayList<Tag> = ArrayList()
    private val cacheUnselectedTags: ArrayList<Tag> = ArrayList()

    private var category: String = ""

    fun setCategoryFragment(category: String, tags: List<Tag>) {
        cacheSelectedTags.addAll(tags)
        this.category = category
        categorySpinner.setText(category, false)
        showTags((categorySpinner.adapter as ArrayAdapter<String>).getPosition(category))
    }

    fun getCategory() = category
    fun getSelectedTags() = cacheSelectedTags
    fun getUnselectedTags() = cacheUnselectedTags

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
                cacheSelectedTags.clear()
                category = categorySpinner.text.toString()
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

            if (cacheSelectedTags.map { it.tag }.contains(tag)) {
                setPickedTag(tagText)
            } else {
                cacheUnselectedTags.add(Tag(userId = User.getMyId(), tag = tag.toString()))
            }

            tagText.setOnClickListener {
                tagClick(tagText)
            }

            tagsMaxLayout.addView(view)
        }
    }

    private fun tagClick(tagText: TextView) {
        val text = tagText.text.toString()
        if (cacheSelectedTags.map { it.tag }.contains(text)) {
            setUnpickedTag(tagText)
            cacheSelectedTags.remove(cacheSelectedTags.find { it.tag == text })
            cacheUnselectedTags.add(Tag(userId = User.getMyId(), tag = text))
        } else {
            setPickedTag(tagText)
            cacheSelectedTags.add(Tag(userId = User.getMyId(), tag = text))
            cacheUnselectedTags.remove(cacheUnselectedTags.find { it.tag == text })
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
}