package com.bunbeauty.ideal.myapplication.clean_architecture.mvp

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.GridLayout

class CustomGridLayout(context: Context, attrs: AttributeSet?) : GridLayout(context, attrs) {

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }
}