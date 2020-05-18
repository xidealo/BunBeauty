package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn.VerifyPhonePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.VerifyPhoneView
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithViewApi
import javax.inject.Inject

class VerifyPhoneNumberActivity : MvpAppCompatActivity(), View.OnClickListener,
    VerifyPhoneView {

    private lateinit var verifyCodeBtn: Button
    private lateinit var resendCodeText: TextView
    private lateinit var codeInput: EditText
    private lateinit var changePhoneText: TextView
    private lateinit var alertCodeText: TextView
    private lateinit var progressBar: ProgressBar

    @Inject
    internal lateinit var verifyPhoneInteractor: VerifyPhoneInteractor

    @InjectPresenter
    internal lateinit var verifyPhonePresenter: VerifyPhonePresenter

    @ProvidePresenter
    internal fun provideVerifyPhonePresenter(): VerifyPhonePresenter {

        DaggerAppComponent.builder()
            .appModule(AppModule(application, intent))
            .build()
            .inject(this)
        return VerifyPhonePresenter(
            verifyPhoneInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone_number)

        init()
        showViewsOnScreen()

        verifyPhonePresenter.sendCode()
    }

    private fun init() {
        verifyCodeBtn = findViewById(R.id.verifyVerifyPhoneBtn)
        resendCodeText = findViewById(R.id.resendCodeVerifyPhoneText)
        alertCodeText = findViewById(R.id.alertCodeVerifyText)
        codeInput = findViewById(R.id.codeVerifyPhoneInput)
        changePhoneText = findViewById(R.id.changePhoneVerifyPhoneText)

        progressBar = findViewById(R.id.loadingVerifyPhoneProgressBar)

        verifyCodeBtn.setOnClickListener(this)
        resendCodeText.setOnClickListener(this)
        changePhoneText.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        WorkWithViewApi.hideKeyboard(this)
        when (v.id) {
            R.id.verifyVerifyPhoneBtn -> verifyPhonePresenter.checkCode(codeInput.text.toString())
            R.id.resendCodeVerifyPhoneText -> verifyPhonePresenter.resendCode()
            R.id.changePhoneVerifyPhoneText -> goBackToAuthorization()
        }
    }

    override fun hideViewsOnScreen() {
        verifyCodeBtn.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSendCode() {

    }

    override fun showViewsOnScreen() {
        verifyCodeBtn.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
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
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    companion object {
        private val TAG = "DBInf"
    }
}