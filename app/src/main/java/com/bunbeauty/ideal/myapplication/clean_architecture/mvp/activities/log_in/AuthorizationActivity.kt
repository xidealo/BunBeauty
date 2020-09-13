package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.invisible
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.visible
import com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.intarfaces.IAdapterSpinner
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.log_in.AuthorizationPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in.AuthorizationView
import kotlinx.android.synthetic.main.activity_authorization.*
import javax.inject.Inject

class AuthorizationActivity : BaseActivity(), AuthorizationView, IAdapterSpinner {

    @Inject
    lateinit var authorizationInteractor: AuthorizationInteractor

    @InjectPresenter
    lateinit var authorizationPresenter: AuthorizationPresenter

    @ProvidePresenter
    internal fun provideAuthorizationPresenter(): AuthorizationPresenter {
        buildDagger().inject(this)
        return AuthorizationPresenter(authorizationInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        configViews()

        authorizationPresenter.authorizeByDefault()
    }

    private fun configViews() {
        val countryCodes = arrayListOf(*resources.getStringArray(R.array.country_codes))

        setAdapter(countryCodes, activity_authorization_sp_code, this)
        activity_authorization_sp_code.setText(countryCodes.first())
        (activity_authorization_sp_code.adapter as ArrayAdapter<String>).filter.filter("")

        activity_authorization_pbtn_log_in.setOnClickListener {
            authorizationPresenter.authorize(
                activity_authorization_sp_code.text.toString(),
                activity_authorization_et_phone.text.toString()
            )
        }
    }

    override fun hidePhoneNumberFields() {
        activity_authorization_ll_main.invisible()
    }

    override fun showPhoneNumberFields() {
        activity_authorization_ll_main.visible()
    }

    override fun hideLoading() {
        activity_authorization_pbtn_log_in.hideLoading()
    }

    override fun showLoading() {
        activity_authorization_pbtn_log_in.showLoading()
    }

    override fun showPhoneError(error: String) {
        activity_authorization_et_phone.error = error
        activity_authorization_et_phone.requestFocus()
    }

    override fun goToVerifyPhone(phone: String) {
        val intent = Intent(this, VerifyPhoneNumberActivity::class.java).apply {
            putExtra(User.PHONE, phone)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun goToRegistration(phone: String) {
        val intent = Intent(this, RegistrationActivity::class.java).apply {
            putExtra(User.PHONE, phone)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    override fun goToProfile(user: User) {
        val intent = Intent(this, ProfileActivity::class.java).apply {
            putExtra(User.USER, user)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}