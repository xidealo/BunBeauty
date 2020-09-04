package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.google.android.material.button.MaterialButton

class ProgressButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private var button: MaterialButton = MaterialButton(context)
    private var progressBar: ProgressBar = ProgressBar(context)

    init {
        button.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        button.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
        button.text = "ProgressButton"

        val constraintLayoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        constraintLayoutParams.startToStart = 0
        constraintLayoutParams.topToTop = 0
        constraintLayoutParams.endToEnd = 0
        constraintLayoutParams.bottomToBottom = 0
        progressBar.layoutParams = constraintLayoutParams
        progressBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.darkBlue))

        addView(progressBar)
        addView(button)
    }
}