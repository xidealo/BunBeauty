package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.android.ideal.myapplication.R
import com.google.android.material.button.MaterialButton

class ProgressButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {

    private val buttonText =
        getString(context, attributeSet, R.styleable.ProgressButton_android_text)
    private val buttonWidth = getDimensionPixel(
        context,
        attributeSet,
        R.styleable.ProgressButton_android_layout_width,
        DEFAULT_BUTTON_WIDTH
    )
    private val buttonHeight = getDimensionPixel(
        context,
        attributeSet,
        R.styleable.ProgressButton_android_layout_height,
        DEFAULT_BUTTON_HEIGHT
    )
    private val buttonTypeface =
        getTypeface(context, attributeSet, R.styleable.ProgressButton_android_fontFamily)
    private val buttonIsAllCaps =
        getBoolean(context, attributeSet, R.styleable.ProgressButton_android_textAllCaps, false)
    private val marginStart = getDimensionPixel(
        context,
        attributeSet,
        R.styleable.ProgressButton_android_layout_marginStart,
        DEFAULT_BUTTON_MARGIN
    )
    private val marginEnd = getDimensionPixel(
        context,
        attributeSet,
        R.styleable.ProgressButton_android_layout_marginEnd,
        DEFAULT_BUTTON_MARGIN
    )
    private val marginTop = getDimensionPixel(
        context,
        attributeSet,
        R.styleable.ProgressButton_android_layout_marginTop,
        DEFAULT_BUTTON_MARGIN
    )
    private val marginBottom = getDimensionPixel(
        context,
        attributeSet,
        R.styleable.ProgressButton_android_layout_marginBottom,
        DEFAULT_BUTTON_MARGIN
    )
    private val buttonCornerRadius = getDimensionPixel(
        context,
        attributeSet,
        R.styleable.ProgressButton_cornerRadius,
        DEFAULT_BUTTON_CORNER_RADIUS
    )
    private val buttonBackgroundColor = getColor(
        context,
        attributeSet,
        R.styleable.ProgressButton_android_backgroundTint,
        DEFAULT_BUTTON_COLOR
    )

    private val containerLayoutParams =
        LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            setPadding(10, 10, 10, 10)
            marginStart = 0
            marginEnd = 0
        }

    private val button = createButton(context)
    private val progressBar = createProgressBar(context)

    init {
        addView(button)
        addView(progressBar)

        hideLoading()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        layoutParams = containerLayoutParams.apply {
            setMargins(marginStart, marginTop, marginEnd, marginBottom)
        }
    }

    private fun createButton(context: Context): MaterialButton {
        val button = MaterialButton(context)

        button.layoutParams = LayoutParams(buttonWidth, buttonHeight).apply {
            z = 0f
        }
        button.typeface = buttonTypeface
        button.isAllCaps = buttonIsAllCaps
        button.setBackgroundColor(buttonBackgroundColor)
        button.cornerRadius = buttonCornerRadius

        return button
    }

    private fun createProgressBar(context: Context): ProgressBar {
        val progressBar = ProgressBar(context)

        progressBar.layoutParams = LayoutParams(
            resources.getDimensionPixelSize(R.dimen.button_progress_bar_size),
            resources.getDimensionPixelSize(R.dimen.button_progress_bar_size)
        ).apply {
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)
            z = 10f
        }
        progressBar.indeterminateTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))

        return progressBar
    }

    private fun getString(context: Context, attributeSet: AttributeSet?, attributeId: Int): String {
        return if (attributeSet != null) {
            val typedArray =
                context.obtainStyledAttributes(attributeSet, R.styleable.ProgressButton)
            val text = typedArray.getString(attributeId) ?: ""
            typedArray.recycle()

            text
        } else {
            ""
        }
    }

    private fun getColor(
        context: Context,
        attributeSet: AttributeSet?,
        attributeId: Int,
        defaultColor: Int
    ): Int {
        return if (attributeSet != null) {
            val typedArray =
                context.obtainStyledAttributes(attributeSet, R.styleable.ProgressButton)
            val color = typedArray.getColor(attributeId, defaultColor)
            typedArray.recycle()

            color
        } else {
            defaultColor
        }
    }

    fun getDimensionPixel(
        context: Context,
        attributeSet: AttributeSet?,
        attributeId: Int,
        defaultDip: Float
    ): Int {
        return if (attributeSet != null) {
            val typedArray =
                context.obtainStyledAttributes(attributeSet, R.styleable.ProgressButton)
            val dimensionPixel = typedArray.getLayoutDimension(
                attributeId,
                getPixels(defaultDip)
            )
            typedArray.recycle()

            dimensionPixel
        } else {
            getPixels(defaultDip)
        }
    }

    fun getTypeface(context: Context, attributeSet: AttributeSet?, attributeId: Int): Typeface? {
        return if (attributeSet != null) {
            val typedArray =
                context.obtainStyledAttributes(attributeSet, R.styleable.ProgressButton)
            val fontId = typedArray.getResourceId(attributeId, DEFAULT_BUTTON_FONT_ID)
            val typeface = ResourcesCompat.getFont(context, fontId)
            typedArray.recycle()

            typeface
        } else {
            ResourcesCompat.getFont(context, DEFAULT_BUTTON_FONT_ID)
        }
    }

    fun getBoolean(
        context: Context,
        attributeSet: AttributeSet?,
        attributeId: Int,
        default: Boolean
    ): Boolean {
        return if (attributeSet != null) {
            val typedArray =
                context.obtainStyledAttributes(attributeSet, R.styleable.ProgressButton)
            val boolean = typedArray.getBoolean(attributeId, default)
            typedArray.recycle()

            boolean
        } else {
            default
        }
    }


    fun showLoading() {
        button.text = ""
        button.isEnabled = false
        progressBar.visibility = View.VISIBLE
    }

    fun hideLoading() {
        button.text = buttonText
        button.isEnabled = true
        progressBar.visibility = View.GONE
    }

    override fun setOnClickListener(l: OnClickListener?) {
        button.setOnClickListener(l)
    }

    fun getPixels(dip: Float): Int {
        return TypedValue.applyDimension(COMPLEX_UNIT_DIP, dip, resources.displayMetrics).toInt()
    }

    companion object {
        const val DEFAULT_BUTTON_WIDTH = 200f
        const val DEFAULT_BUTTON_HEIGHT = 56f
        const val DEFAULT_BUTTON_MARGIN = 0f
        const val DEFAULT_BUTTON_CORNER_RADIUS = 0f
        const val DEFAULT_BUTTON_COLOR = 0xaaaaaa
        const val DEFAULT_BUTTON_FONT_ID = R.font.roboto_bold
    }
}