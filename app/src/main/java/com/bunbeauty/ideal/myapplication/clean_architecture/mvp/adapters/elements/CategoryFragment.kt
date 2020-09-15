package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class CategoryFragment : MvpAppCompatFragment(), IAdapterSpinner, SpinnerSelectable {

    private val cacheSelectedTags: ArrayList<Tag> = ArrayList()
    private val cacheUnselectedTags: ArrayList<Tag> = ArrayList()

    private var category: String = ""

    fun setCategoryFragment(category: String, tags: List<Tag>) {
        cacheSelectedTags.addAll(tags)
        this.category = category
        fragment_category_sp_category.setText(category, false)
        showTags((fragment_category_sp_category.adapter as ArrayAdapter<String>).getPosition(category))
    }

    fun getCategory() = category
    fun getSelectedTags() = cacheSelectedTags
    fun getUnselectedTags() = cacheUnselectedTags

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
        setAdapter(
            categories,
            fragment_category_sp_category,
            context!!
        )
        setCategorySpinnerListener()
    }

    private fun setCategorySpinnerListener() {
        fragment_category_sp_category.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                cacheSelectedTags.clear()
                category = fragment_category_sp_category.text.toString()
                tagsMaxLayout.removeAllViews()
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
            val view: View = inflater.inflate(R.layout.fragment_tag, tagsMaxLayout, false)
            view.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            val tagText = view.findViewById<Chip>(R.id.tagFragmentTagChip)
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

    private fun tagClick(tagText: Chip) {
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

    private fun setPickedTag(tagText: Chip) {
        tagText.chipBackgroundColor =
            ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.yellow))
        tagText.setTextColor(ContextCompat.getColor(context!!, R.color.black))
    }

    private fun setUnpickedTag(tagText: Chip) {
        tagText.chipBackgroundColor =
            ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.mainBlue))
        tagText.setTextColor(ContextCompat.getColor(context!!, R.color.white))
    }
}