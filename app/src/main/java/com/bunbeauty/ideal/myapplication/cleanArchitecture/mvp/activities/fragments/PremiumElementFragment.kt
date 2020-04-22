package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.premium.PremiumElementCodeInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.premium.PremiumElementServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.fragments.PremiumElementPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.fragments.PremiumElementFragmentView
import javax.inject.Inject

class PremiumElementFragment : MvpAppCompatFragment(), View.OnClickListener,
    PremiumElementFragmentView {

    lateinit var service: Service
    private lateinit var premiumText: TextView
    private lateinit var noPremiumText: TextView
    private lateinit var bottomLayout: LinearLayout
    private lateinit var codeText: TextView

    @InjectPresenter
    lateinit var premiumElementPresenter: PremiumElementPresenter

    @Inject
    lateinit var premiumElementCodeInteractor: PremiumElementCodeInteractor

    @Inject
    lateinit var premiumElementServiceInteractor: PremiumElementServiceInteractor

    @ProvidePresenter
    internal fun provideElementPresenter(): PremiumElementPresenter {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(activity!!.application, activity!!.intent))
            .build().inject(this)

        return PremiumElementPresenter(
            premiumElementCodeInteractor,
            premiumElementServiceInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            service = it.getSerializable(Service.SERVICE) as Service
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.premium_element, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.setPremiumPremiumElementBtn).setOnClickListener(this)
        codeText = view.findViewById(R.id.codePremiumElement)
        premiumText = view.findViewById(R.id.yesPremiumPremiumElementText)
        noPremiumText = view.findViewById(R.id.noPremiumPremiumElementText)
        bottomLayout = view.findViewById(R.id.premiumAddServiceBottomLayout)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.setPremiumPremiumElementBtn -> {
                premiumElementPresenter.setPremium(
                    codeText.text.toString().toLowerCase().trim(),
                    service
                )
            }
        }
    }

    override fun showError(error: String) {
        codeText.error = error
        codeText.requestFocus()
    }

    override fun showPremiumActivated() {
        Toast.makeText(context, "Премиум активирован", Toast.LENGTH_LONG).show()
    }

    override fun setWithPremium() {
        noPremiumText.visibility = View.GONE
        premiumText.visibility = View.VISIBLE
        premiumText.isEnabled = false
    }

    override fun hideBottom() {
        bottomLayout.visibility = View.GONE
    }

    companion object {
        private const val TAG = "DBInf"
        @JvmStatic
        fun newInstance(
            service: Service
        ) =
            PremiumElementFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(Service.SERVICE, service)
                }
            }
    }
}
