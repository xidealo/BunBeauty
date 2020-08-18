package com.bunbeauty.ideal.myapplication.clean_architecture.mvp

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    var isEnable = true

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return isEnable && super.onInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return isEnable && super.onTouchEvent(event)
    }
}