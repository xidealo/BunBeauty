package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.intarfaces

import android.widget.ArrayAdapter
import android.widget.Spinner

interface SpinnerSelectable {

    fun setSpinnerSelection(spinner: Spinner, selectedValue: String) {
        var position = (spinner.adapter as ArrayAdapter<String>).getPosition(selectedValue)
        if (position == -1) {
            position = 0
        }
        spinner.setSelection(position)
    }

}