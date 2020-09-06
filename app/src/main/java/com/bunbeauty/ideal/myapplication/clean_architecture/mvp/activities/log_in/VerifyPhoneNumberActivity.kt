package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.WorkWithViewApi
import com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.log_in.VerifyPhonePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in.VerifyPhoneView
import com.google.android.material.snackbar.Snackbar
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
        showViewsOnScreen()

        verifyPhonePresenter.sendCode()
    }

    private fun configViews() {
        phoneVerifyPhoneInput.setText(verifyPhonePresenter.getPhoneNumber())

        activity_verify_phone_number_btn_log_in.setOnClickListener {
            WorkWithViewApi.hideKeyboard(this)
            verifyPhonePresenter.checkCode(codeVerifyPhoneInput.text.toString())
        }
        resendCodeVerifyPhoneText.setOnClickListener {
            WorkWithViewApi.hideKeyboard(this)
            verifyPhonePresenter.resendCode()
        }
        changePhoneVerifyPhoneText.setOnClickListener {
            WorkWithViewApi.hideKeyboard(this)
            goBackToAuthorization()
        }
    }

    override fun hideViewsOnScreen() {
        activity_verify_phone_number_btn_log_in.visibility = View.GONE
        activity_verify_phone_number_pb_loading.visibility = View.VISIBLE
    }

    override fun showViewsOnScreen() {
        activity_verify_phone_number_btn_log_in.visibility = View.VISIBLE
        activity_verify_phone_number_pb_loading.visibility = View.GONE
    }

    override fun showMessage(message: String) {
        Snackbar.make(verifyPhoneNumberLayout, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.mainBlue))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
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
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}