package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.ServiceProfileAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.ScheduleActivity
import com.google.android.material.button.MaterialButton

class ServicesFragment : MvpAppCompatFragment(), View.OnClickListener {

    private var createBtn: MaterialButton? = null
    private var serviceRecyclerView: RecyclerView? = null

    private var serviceAdapter: ServiceProfileAdapter? = null
    private var createBtnVisibility: Int = View.VISIBLE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_services, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createBtn = view.findViewById(R.id.createServiceBtn)
        createBtn!!.visibility = createBtnVisibility

        serviceRecyclerView = view.findViewById(R.id.servicesRecycleView)
        serviceRecyclerView!!.layoutManager = LinearLayoutManager(context)
        if (serviceAdapter != null) {
            serviceRecyclerView!!.adapter = serviceAdapter
        }
    }

    fun showCreateButton() {
        if (createBtn != null) {
            createBtn!!.visibility = View.VISIBLE
            createBtn!!.setOnClickListener(this)
        } else {
            createBtnVisibility = View.VISIBLE
        }
    }

    fun hideCreateButton() {
        if (createBtn != null) {
            createBtn!!.visibility = View.GONE
        } else {
            createBtnVisibility = View.GONE
        }
    }

    fun setAdapter(services: List<Service>, user: User) {
        if (serviceRecyclerView != null) {
            serviceAdapter = ServiceProfileAdapter(services, user)
            serviceRecyclerView!!.adapter = serviceAdapter
        } else {
            serviceAdapter = ServiceProfileAdapter(services, user)
        }
    }

    fun updateAdapter() {
        serviceAdapter!!.notifyDataSetChanged()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.createServiceBtn -> {
                val intent = Intent(context, ScheduleActivity::class.java)
                startActivity(intent)
                activity!!.overridePendingTransition(0, 0)
            }
        }
    }
}