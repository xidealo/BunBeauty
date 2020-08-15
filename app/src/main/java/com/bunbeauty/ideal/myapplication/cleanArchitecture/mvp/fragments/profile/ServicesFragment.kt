package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.ProfileServiceAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.CreationServiceActivity
import kotlinx.android.synthetic.main.fragment_services.*

class ServicesFragment(private val profileServiceAdapter: ProfileServiceAdapter) : MvpAppCompatFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_services, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createServiceBtn.visibility = View.GONE
        createServiceBtn.setOnClickListener {
            goToCreationService()
        }

        servicesRecycleView.layoutManager = LinearLayoutManager(context)
        servicesRecycleView.adapter = profileServiceAdapter
    }

    fun showCreateButton() {
        createServiceBtn?.visibility = View.VISIBLE
    }

    fun hideCreateButton() {
        createServiceBtn?.visibility = View.GONE
    }

    fun updateServiceList(serviceList: List<Service>) {
        profileServiceAdapter.updateAdapter(serviceList)
    }

    private fun goToCreationService() {
        val intent = Intent(context, CreationServiceActivity::class.java)
        startActivity(intent)
        activity!!.overridePendingTransition(0, 0)
    }
}