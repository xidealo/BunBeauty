package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.gone
import com.bunbeauty.ideal.myapplication.clean_architecture.business.fragments.premium.PremiumElementCodeInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.fragments.premium.PremiumElementServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.AppModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.FirebaseModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.InteractorModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.RepositoryModule
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.fragments.PremiumElementPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.fragments.PremiumElementFragmentView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_premium.*
import javax.inject.Inject

//TODO переписать без логики
class PremiumFragment : MvpAppCompatFragment(), PremiumElementFragmentView {

    lateinit var service: Service

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
            .appModule(AppModule(activity!!.application))
            .interactorModule(InteractorModule(activity!!.intent))
            .repositoryModule(RepositoryModule())
            .firebaseModule(FirebaseModule())
            .build().inject(this)

        return PremiumElementPresenter(
            premiumElementCodeInteractor,
            premiumElementServiceInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            service = it.getParcelable(Service.SERVICE)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_premium, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragment_premium_btn_set.setOnClickListener{
            premiumElementPresenter.setPremium(
                fragment_premium_et_code.text.toString().toLowerCase().trim(),
                service
            )
        }
    }

    override fun showError(error: String) {
        fragment_premium_et_code.error = error
        fragment_premium_et_code.requestFocus()
    }

    override fun showPremiumActivated() {
        Snackbar.make(fragment_premium_ll_header, "Премиум активирован", Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(context!!, R.color.mainBlue))
            .setActionTextColor(ContextCompat.getColor(context!!, R.color.white)).show()
    }

    override fun setWithPremium(premiumDate: Long) {
        fragment_premium_tv_no.visibility = View.GONE
        fragment_premium_tv_yes.visibility = View.VISIBLE
        fragment_premium_tv_yes.isEnabled = false
        fragment_premium_btn_set.text = "Продлить премиум"
        fragment_premium_tv_label.text = "Премиум до ${WorkWithTimeApi.getDateInFormatYMD(
            premiumDate
        )}"
    }

    override fun hideBottom() {
        fragment_premium_ll_bottom.gone()
    }


    fun setPremium(service: Service) {
        this.service = service
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
