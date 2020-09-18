package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.ProfileOrderAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_orders.*
import javax.inject.Inject

class OrdersFragment : BaseFragment() {
    @Inject
    lateinit var profileOrderAdapter: ProfileOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildDagger().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragment_orders_rv_main.layoutManager = LinearLayoutManager(context)
        fragment_orders_rv_main.adapter = profileOrderAdapter
    }

    fun updateOrderList(orderList: List<Order>) {
        profileOrderAdapter.updateItems(orderList)
    }
}