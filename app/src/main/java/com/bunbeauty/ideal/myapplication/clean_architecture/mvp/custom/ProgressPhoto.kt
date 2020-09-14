package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.custom

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.gone
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.visible
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

class ProgressPhoto @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {

    private val photo = createPhoto(context)
    private val progressBar = createProgressBar(context)
    private val errorTextView = createTextView(context)

    init {
        addView(photo)
        addView(progressBar)
        addView(errorTextView)

        showLoading()
    }

    private fun createPhoto(context: Context): SubsamplingScaleImageView {
        val subsamplingScaleImageView = SubsamplingScaleImageView(context)

        subsamplingScaleImageView.layoutParams =
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

        return subsamplingScaleImageView
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
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.mainBlue))

        return progressBar
    }

    private fun createTextView(context: Context): TextView {
        val textView = TextView(context)

        textView.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }
        textView.text = "Ошибка загрузки"
        textView.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        )

        return textView
    }

    fun showLoading() {
        photo.gone()
        errorTextView.gone()
        progressBar.visible()
    }

    fun showPhoto(imageSource: ImageSource) {
        photo.visible()
        photo.setImage(imageSource)
        errorTextView.gone()
        progressBar.gone()
    }

    fun showError() {
        photo.gone()
        errorTextView.visible()
        progressBar.gone()
    }

    fun getPixels(dip: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, resources.displayMetrics)
            .toInt()
    }
}