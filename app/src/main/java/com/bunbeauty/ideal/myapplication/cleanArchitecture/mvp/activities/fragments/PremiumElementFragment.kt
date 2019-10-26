package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.PremiumElementInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.CodeFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ServiceFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.CodeDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.fragments.PremiumElementPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.fragments.PremiumElementFragmentView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.CodeRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import javax.inject.Inject

class PremiumElementFragment @SuppressLint("ValidFragment")
constructor() : MvpAppCompatFragment(), View.OnClickListener, PremiumElementFragmentView {

    private lateinit var code: String
    private lateinit var codeText: TextView

    @InjectPresenter
    lateinit var premiumElementPresenter: PremiumElementPresenter

    @ProvidePresenter
    internal fun provideElementPresenter(): PremiumElementPresenter {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(activity!!.application, activity!!.intent))
                .build().inject(this)

        return PremiumElementPresenter(premiumElementInteractor)
    }

    @Inject
    lateinit var premiumElementInteractor: PremiumElementInteractor

    @Inject
    lateinit var codeRepository: CodeRepository
    @Inject
    lateinit var codeDao: CodeDao
    @Inject
    lateinit var codeFirebase: CodeFirebase

    @Inject
    lateinit var serviceRepository: ServiceRepository
    @Inject
    lateinit var serviceDao: ServiceDao
    @Inject
    lateinit var serviceFirebaseApi: ServiceFirebaseApi


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.premium_element, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.setPremiumPremiumElementBtn).setOnClickListener(this)
        codeText = view.findViewById(R.id.codePremiumElement)

    }

    override fun onClick(v: View) {
        if (v.id == R.id.setPremiumPremiumElementBtn) {
            code = codeText.text.toString().toLowerCase().trim { it <= ' ' }
            premiumElementPresenter.setPremium(code)
        }
    }

    companion object {
        private val TAG = "DBInf"
    }

    override fun showWrongCode() {
        Toast.makeText(context, "Неверно введен код", Toast.LENGTH_LONG).show()
    }

    override fun showOldCode() {
        Toast.makeText(context, "Код больше не действителен", Toast.LENGTH_LONG).show()
    }

    override fun showPremiumActivated() {
        Toast.makeText(context, "Премиум активирован", Toast.LENGTH_LONG).show()
    }

}
