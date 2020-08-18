package com.bunbeauty.ideal.myapplication.clean_architecture.adapters

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.arellomobile.mvp.MvpAppCompatFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile.OrdersFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile.ServicesFragment

class ProfilePagerAdapter(
    fragmentManager: FragmentManager,
    behavior: Int,
    private val ordersFragment: OrdersFragment,
    private val servicesFragment: ServicesFragment
) : FragmentPagerAdapter(fragmentManager, behavior) {

    override fun getItem(position: Int): MvpAppCompatFragment {
        return when (position) {
            0 -> ordersFragment
            1 -> servicesFragment
            else -> servicesFragment
        }
    }

    override fun getCount(): Int {
        return 2
    }
}