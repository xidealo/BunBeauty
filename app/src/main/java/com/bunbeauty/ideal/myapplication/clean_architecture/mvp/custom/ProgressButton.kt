package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.custom

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.google.android.material.button.MaterialButton

class ProgressButton @JvmOverloads constructor(
    context: Context,
    private val attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {

    private val button = createButton(context)
    private val progressBar = createProgressBar(context)

    init {
        addView(button)
        addView(progressBar)

        hideLoading()
    }aa

    private fun createButton(context: Context): MaterialButton {
        val button = MaterialButton(ContextThemeWrapper(context, R.style.smallButton))

        button.layoutParams =
            LayoutParams(resources.getDimensionPixelSize(R.dimen.small_button_width), WRAP_CONTENT)
        button.z = 0f
        button.cornerRadius = resources.getDimensionPixelSize(R.dimen.button_corner_radius)

        return button
    }

    private fun createProgressBar(context: Context): ProgressBar {
        val progressBar = ProgressBar(context)

        progressBar.layoutParams = LayoutParams(
            resources.getDimensionPixelSize(R.dimen.button_progress_bar_size),
            resources.getDimensionPixelSize(R.dimen.button_progress_bar_size)
        ).apply {
            gravity = Gravity.CENTER
        }
        progressBar.z = 10f
        progressBar.setPadding(8, 8, 8, 8)
        progressBar.indeterminateTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))

        return progressBar
    }

    fun showLoading() {
        button.text = ""
        progressBar.visibility = View.VISIBLE
    }

    fun hideLoading() {
        button.text = getText(context, attributeSet)
        progressBar.visibility = View.GONE
    }

    private fun getText(context: Context, attributeSet: AttributeSet?): String {
        return if (attributeSet != null) {
            val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ProgressButton)
            typedArray.getString(R.styleable.ProgressButton_android_text)!!
        } else {
            ""
        }
    }

    fun getPixels(dip: Float): Int {
        return TypedValue.applyDimension(COMPLEX_UNIT_DIP, dip, resources.displayMetrics).toInt()
    }
}