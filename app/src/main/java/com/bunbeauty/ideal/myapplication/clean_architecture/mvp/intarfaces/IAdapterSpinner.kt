package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.intarfaces

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import com.android.ideal.myapplication.R

interface IAdapterSpinner {

    fun setAdapter(unit: ArrayList<String>, spinner: Spinner, context: Context) {
        setAdapter(
            unit,
            spinner,
            context,
            R.layout.support_simple_spinner_dropdown_item
        )
    }

    fun setAdapter(unit: ArrayList<String>, spinner: AutoCompleteTextView, context: Context) {
        val adapter = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, unit)
        spinner.setAdapter(adapter)
    }

    fun setAdapter(
        unit: ArrayList<String>,
        spinner: Spinner,
        context: Context,
        itemLayoutId: Int
    ) {
        val adapter = ArrayAdapter(context, itemLayoutId, unit)
        spinner.adapter = adapter
    }
}