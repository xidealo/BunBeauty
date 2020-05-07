package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment

class OrdersFragment : MvpAppCompatFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val orderRecyclerView: RecyclerView = view.findViewById(R.id.ordersRecycleView)
        orderRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun setAdapter() {

    }
}