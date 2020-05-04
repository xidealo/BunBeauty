package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.intarfaces

import android.content.Context
import android.widget.ArrayAdapter
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

    /*fun setAdapter(
            unit: ArrayList<String>,
            spinner: Spinner,
            context: Context,
            itemLayoutId: Int,
            dropdownItemLayoutId: Int
    ) {
        val adapter = ArrayAdapter(context, itemLayoutId, unit)
        adapter.setDropDownViewResource(dropdownItemLayoutId)
        spinner.adapter = adapter
    }*/

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