package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.intarfaces.IAdapterSpinner
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.intarfaces.SpinnerSelectable
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_category_block.*
import kotlinx.android.synthetic.main.element_tag.view.*

class CategoryFragment : MvpAppCompatFragment(), IAdapterSpinner, SpinnerSelectable {

    val selectedTagList = ArrayList<Tag>()
    var category: String = ""

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_block, null)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        val categories = arrayListOf(*resources.getStringArray(R.array.categories))
        setAdapter(categories, fragment_category_sp_category, context!!)
        setCategorySpinnerListener()
    }

    fun setCategoryFragment(category: String, tags: List<Tag>) {
        this.category = category
        selectedTagList.addAll(tags)

        fragment_category_sp_category.setText(category, false)
        showTags((fragment_category_sp_category.adapter as ArrayAdapter<String>).getPosition(category))
    }

    private fun setCategorySpinnerListener() {
        fragment_category_sp_category.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedTagList.clear()
                category = fragment_category_sp_category.text.toString()
                fragment_category_ll_tags.removeAllViews()
                if (position > -1)
                    showTags(position)
            }
    }

    private fun showTags(categoryPosition: Int) {
        val tagsArray = resources
            .obtainTypedArray(R.array.tags_references)
            .getTextArray(categoryPosition)

        for (tag in tagsArray) {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.element_tag, fragment_category_ll_tags, false)

            val tagChip = view.element_tag_chip
            tagChip.text = tag
            if (selectedTagList.any { it.tag == tag }) {
                selectTag(tagChip)
            }
            tagChip.setOnClickListener {
                tagClick(tagChip)
            }

            fragment_category_ll_tags.addView(view)
        }
    }

    private fun tagClick(tagText: Chip) {
        val text = tagText.text.toString()
        if (selectedTagList.map { it.tag }.contains(text)) {
            setUnpickedTag(tagText)
            selectedTagList.remove(selectedTagList.find { it.tag == text })
        } else {
            selectTag(tagText)
            selectedTagList.add(Tag(userId = User.getMyId(), tag = text))
        }
    }

    private fun selectTag(tagText: Chip) {
        tagText.chipBackgroundColor =
            ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.yellow))
        tagText.setTextColor(ContextCompat.getColor(context!!, R.color.black))
    }

    private fun setUnpickedTag(tagText: Chip) {
        tagText.chipBackgroundColor =
            ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.mainBlue))
        tagText.setTextColor(ContextCompat.getColor(context!!, R.color.white))
    }

    fun showCategorySpinnerError(error: String) {
        fragment_category_sp_category.error = error
    }
}