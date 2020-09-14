package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.ProfileOrderAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import kotlinx.android.synthetic.main.fragment_orders.*

class OrdersFragment : MvpAppCompatFragment() {

    private var profileOrderAdapter: ProfileOrderAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            profileOrderAdapter =
                it.getParcelable<ProfileOrderAdapter>(ProfileOrderAdapter.PROFILE_ORDER_ADAPTER) as ProfileOrderAdapter
        }
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
        profileOrderAdapter?.updateItems(orderList)
    }

    companion object {
        @JvmStatic
        fun newInstance(
            profileOrderAdapter: ProfileOrderAdapter
        ) =
            OrdersFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(
                        ProfileOrderAdapter.PROFILE_ORDER_ADAPTER,
                        profileOrderAdapter
                    )
                }
            }
    }
}