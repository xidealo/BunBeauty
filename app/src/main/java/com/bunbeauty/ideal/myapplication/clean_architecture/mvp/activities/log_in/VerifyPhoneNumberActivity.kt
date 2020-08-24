package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.AppModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.FirebaseModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.InteractorModule
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.log_in.VerifyPhonePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in.VerifyPhoneView
import com.bunbeauty.ideal.myapplication.clean_architecture.WorkWithViewApi
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerAppComponent
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_verify_phone_number.*
import javax.inject.Inject

class VerifyPhoneNumberActivity : MvpAppCompatActivity(), VerifyPhoneView {

    @Inject
    internal lateinit var verifyPhoneInteractor: VerifyPhoneInteractor

    @InjectPresenter
    internal lateinit var verifyPhonePresenter: VerifyPhonePresenter

    @ProvidePresenter
    internal fun provideVerifyPhonePresenter(): VerifyPhonePresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .build()
            .inject(this)

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

        verifyVerifyPhoneBtn.setOnClickListener {
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
        verifyVerifyPhoneBtn.visibility = View.GONE
        loadingVerifyPhoneProgressBar.visibility = View.VISIBLE
    }

    override fun showViewsOnScreen() {
        verifyVerifyPhoneBtn.visibility = View.VISIBLE
        loadingVerifyPhoneProgressBar.visibility = View.GONE
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
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.putExtra(User.PHONE, phone)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    override fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}