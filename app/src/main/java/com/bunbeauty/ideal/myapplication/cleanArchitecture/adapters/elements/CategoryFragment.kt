package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import kotlinx.android.synthetic.main.fragment_category.*
import java.util.*
import kotlin.collections.ArrayList

class CategoryFragment : MvpAppCompatFragment() {

    private val tagsArray: ArrayList<String> = ArrayList()
    private var categoryIndex = 0

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
        categorySpinner.setSelection(categoryIndex)
        setCategorySpinnerListener()
    }

    private fun setCategorySpinnerListener() {
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                tagsArray.clear()
                tagsMaxLayout.removeAllViews()
                if (position > 0)
                    showTags(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showTags(categoryPosition: Int) {
        val tagsArray = resources
            .obtainTypedArray(R.array.tags_references)
            .getTextArray(categoryPosition - 1)

        for (tag in tagsArray) {
            val tagText = TextView(context)
            tagText.text = tag
            tagText.typeface = ResourcesCompat.getFont(context!!, R.font.roboto_bold)
            tagText.textSize = 18f
            setUnpickedTag(tagText)

            tagText.setOnClickListener {
                tagClick(tagText)
            }
            /*  tagText.layoutParams = LinearLayout.LayoutParams(
                  LinearLayout.LayoutParams.WRAP_CONTENT,
                  LinearLayout.LayoutParams.WRAP_CONTENT
              )*/
            tagsMaxLayout.addView(tagText, tagsMaxLayout.childCount - 1)
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
        tagText.setBackgroundResource(R.drawable.category_button_pressed)
        tagText.setTextColor(Color.BLACK)
        return tagText
    }

    private fun setUnpickedTag(tagText: TextView): TextView {
        tagText.setBackgroundResource(R.drawable.tags_button)
        tagText.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
        return tagText
    }

    val category: String
        get() = categorySpinner.selectedItem.toString()

}