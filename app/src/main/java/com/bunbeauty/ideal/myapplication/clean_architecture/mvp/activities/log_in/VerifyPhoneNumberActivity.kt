package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in

import android.content.Intent
import android.os.Bundle
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.log_in.VerifyPhonePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in.VerifyPhoneView
import kotlinx.android.synthetic.main.activity_verify_phone_number.*
import javax.inject.Inject

class VerifyPhoneNumberActivity : BaseActivity(), VerifyPhoneView {

    @Inject
    internal lateinit var verifyPhoneInteractor: VerifyPhoneInteractor

    @InjectPresenter
    internal lateinit var verifyPhonePresenter: VerifyPhonePresenter

    @ProvidePresenter
    internal fun provideVerifyPhonePresenter(): VerifyPhonePresenter {
        buildDagger().inject(this)
        return VerifyPhonePresenter(verifyPhoneInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone_number)

        configViews()
        hideLoading()

        verifyPhonePresenter.sendCode()
    }

    private fun configViews() {
        activity_verify_phone_number_et_phone_number.setText(verifyPhonePresenter.getPhoneNumber())

        activity_verify_phone_number_pbtn_log_in.setOnClickListener {
            hideKeyboard()
            verifyPhonePresenter.checkCode(activity_verify_phone_number_et_code.text.toString())
        }
        activity_verify_phone_number_tv_resend_code.setOnClickListener {
            hideKeyboard()
            verifyPhonePresenter.resendCode()
        }
        activity_verify_phone_number_tv_change_phone_number.setOnClickListener {
            hideKeyboard()
            goBackToAuthorization()
        }
    }

    override fun showLoading() {
        activity_verify_phone_number_pbtn_log_in.showLoading()
    }

    override fun hideLoading() {
        activity_verify_phone_number_pbtn_log_in.hideLoading()
    }

    override fun showMessage(message: String) {
        showMessage(message, activity_verify_phone_number_ll)
    }

    private fun goBackToAuthorization() {
        super.onBackPressed()
    }

    override fun goToRegistration(phone: String) {
        val intent = Intent(this, RegistrationActivity::class.java).apply {
            putExtra(User.PHONE, phone)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}