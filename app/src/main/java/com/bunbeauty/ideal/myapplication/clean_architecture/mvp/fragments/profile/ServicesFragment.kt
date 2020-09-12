package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.ProfileServiceAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.create_service.CreationServiceActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.fragments.PremiumFragment
import kotlinx.android.synthetic.main.fragment_services.*

class ServicesFragment : MvpAppCompatFragment() {

    private var profileServiceAdapter: ProfileServiceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            profileServiceAdapter =
                it.getParcelable<ProfileServiceAdapter>(ProfileServiceAdapter.PROFILE_SERVICE_ADAPTER) as ProfileServiceAdapter
        }
    }

    var createServiceButtonVisibility = View.GONE
        set(value) {
            field = value
            fragment_services_btn_add_service?.visibility = value
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_services, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragment_services_btn_add_service.visibility = createServiceButtonVisibility
        fragment_services_btn_add_service.setOnClickListener {
            goToCreationService()
        }

        fragment_services_rv_list.layoutManager = LinearLayoutManager(context)
        fragment_services_rv_list.adapter = profileServiceAdapter
    }

    fun updateServiceList(serviceList: List<Service>) {
        profileServiceAdapter?.updateItems(serviceList)
    }

    private fun goToCreationService() {
        val intent = Intent(context, CreationServiceActivity::class.java)
        startActivity(intent)
        activity!!.overridePendingTransition(0, 0)
    }

    companion object {
        @JvmStatic
        fun newInstance(
            profileServiceAdapter: ProfileServiceAdapter
        ) =
            ServicesFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(
                        ProfileServiceAdapter.PROFILE_SERVICE_ADAPTER,
                        profileServiceAdapter
                    )
                }
            }
    }
}