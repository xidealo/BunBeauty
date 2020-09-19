package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.App
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.gone
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.premium.PremiumFragmentCodeInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.premium.PremiumFragmentServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerActivityComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.AppModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.FirebaseModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.InteractorModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.RepositoryModule
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.PremiumFragmentPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.fragments.PremiumElementFragmentView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_premium.*
import javax.inject.Inject

class PremiumFragment : MvpAppCompatFragment(), PremiumElementFragmentView {

    private lateinit var service: Service

    @InjectPresenter
    lateinit var premiumFragmentPresenter: PremiumFragmentPresenter

    @Inject
    lateinit var premiumFragmentCodeInteractor: PremiumFragmentCodeInteractor

    @Inject
    lateinit var premiumFragmentServiceInteractor: PremiumFragmentServiceInteractor

    @ProvidePresenter
    internal fun provideElementPresenter(): PremiumFragmentPresenter {

        DaggerActivityComponent
            .builder()
            .appComponent((activity!!.application as App).appComponent)
            .build().inject(this)

        return PremiumFragmentPresenter(
            premiumFragmentCodeInteractor,
            premiumFragmentServiceInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            service = it.getParcelable(Service.SERVICE)!!
        }
        premiumFragmentPresenter.checkPremium(service)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_premium, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragment_premium_btn_set.setOnClickListener {
            premiumFragmentPresenter.setPremium(
                fragment_premium_et_code.text.toString().toLowerCase().trim(),
                service
            )
            fragment_premium_btn_set.showLoading()
        }
    }

    override fun showError(error: String) {
        fragment_premium_et_code.error = error
        fragment_premium_et_code.requestFocus()
        fragment_premium_btn_set.hideLoading()
    }

    override fun showMessage(message: String) {
        Snackbar.make(fragment_premium_ll_header, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(context!!, R.color.mainBlue))
            .setActionTextColor(ContextCompat.getColor(context!!, R.color.white)).show()
    }

    override fun setWithPremium(premiumDate: Long) {
        fragment_premium_tv_no.visibility = View.GONE
        fragment_premium_tv_yes.visibility = View.VISIBLE
        fragment_premium_tv_yes.isEnabled = false
        fragment_premium_tv_label.text = "Премиум до ${
            WorkWithTimeApi.getDateInFormatYMD(
                premiumDate
            )
        }"
    }

    override fun hideBottom() {
        fragment_premium_ll_bottom.gone()
    }

    companion object {
        @JvmStatic
        fun newInstance(service: Service) =
            PremiumFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Service.SERVICE, service)
                }
            }
    }
}
