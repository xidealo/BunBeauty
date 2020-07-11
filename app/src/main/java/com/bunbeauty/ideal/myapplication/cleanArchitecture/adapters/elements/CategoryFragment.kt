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
import kotlinx.android.synthetic.main.fragment_category.*
import java.util.*

class CategoryFragment : MvpAppCompatFragment(), View.OnClickListener {

    val tagsArray: ArrayList<String>
    private var categoryPosition = 0
    private var categoryIndex = 0

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
        if (!tagsArray.isEmpty()) {
            tagsBtn.setBackgroundResource(R.drawable.category_button_pressed)
            tagsBtn.setHintTextColor(Color.BLACK)
        }
        tagsBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tagsBtn -> showTags()
            else -> tagClick(v as TextView)
        }
    }

    private fun setCategorySpinnerListener() {
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var isFirst = true
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                categoryPosition = position
                if (isFirst) {
                    isFirst = false
                } else {
                    tagsBtn!!.setBackgroundResource(R.drawable.tags_button)
                    tagsBtn!!.setHintTextColor(Color.WHITE)
                    tagsArray.clear()
                }
                if (position == 0) {
                    tagsMinLayout.visibility = View.GONE
                } else {
                    tagsMinLayout.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showTags() {
        val tagsArray = resources
            .obtainTypedArray(R.array.tags_references)
            .getTextArray(categoryPosition - 1)

        for (tag in tagsArray) {
            val tagText = TextView(context)
            tagText.text = tag
            tagText.gravity = Gravity.CENTER
            tagText.typeface = ResourcesCompat.getFont(context!!, R.font.roboto_bold)
            tagText.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            tagText.setOnClickListener(this)
            tagText.setPadding(0, 16, 0, 16)

            if (tagsArray.contains(tag.toString())) {
                tagText.setBackgroundResource(R.drawable.category_button_pressed)
                tagText.setHintTextColor(Color.BLACK)
            }

            tagsMaxLayout.addView(tagText, tagsMaxLayout.childCount - 1)
        }
        tagsMinLayout.visibility = View.GONE
        tagsMaxLayout.visibility = View.VISIBLE
    }

    private fun tagClick(tagText: TextView) {
        val text = tagText.text.toString()
        if (tagsArray.contains(text)) {
            tagText.setBackgroundResource(0)
            tagsArray.remove(text)
            if (tagsArray.isEmpty()) {
                tagsBtn!!.setBackgroundResource(R.drawable.tags_button)
                tagsBtn!!.setHintTextColor(Color.WHITE)
            }
        } else {
            tagText.setBackgroundResource(R.drawable.category_button_pressed)
            tagsArray.add(text)
            tagsBtn!!.setBackgroundResource(R.drawable.category_button_pressed)
            tagsBtn!!.setHintTextColor(Color.BLACK)
        }
    }

    val category: String
        get() = categorySpinner.selectedItem.toString()

    fun setCategory(index: Int) {
        categoryIndex = index
    }

    fun addTag(tag: String) {
        tagsArray.add(tag)
    }

    init {
        tagsArray = ArrayList()
    }
}