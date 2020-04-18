package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.VerifyPhonePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.VerifyPhoneView
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithViewApi
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.reflect.typeOf

class VerifyPhoneActivity : MvpAppCompatActivity(), View.OnClickListener, VerifyPhoneView {

    private val USER_PHONE = "user phone"

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
        return VerifyPhonePresenter(verifyPhoneInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_phone)
        initView()
        showViewsOnScreen()
        verifyPhonePresenter.sendCode()
    }

    private fun initView() {
        Log.d(TAG, "initView VerifyPhoneActivity: ")

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
            R.id.verifyVerifyPhoneBtn -> verifyPhonePresenter.verify(codeInput.text.toString())
            R.id.resendCodeVerifyPhoneText -> verifyPhonePresenter.resendCode(this)
            R.id.changePhoneVerifyPhoneText -> goBackToAuthorization()
        }
    }

    override fun hideViewsOnScreen() {
        verifyCodeBtn.visibility = View.GONE
        resendCodeText.visibility = View.GONE
        codeInput.visibility = View.GONE
        changePhoneText.visibility = View.GONE
        alertCodeText.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun showViewsOnScreen() {
        verifyCodeBtn.visibility = View.VISIBLE
        resendCodeText.visibility = View.VISIBLE
        codeInput.visibility = View.VISIBLE
        changePhoneText.visibility = View.VISIBLE
        alertCodeText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    override fun showSendCode() {
        Toast.makeText(this, "Код был отправлен", Toast.LENGTH_LONG).show()
    }

    override fun showWrongCode() {
        Toast.makeText(this, "Вы ввели неверный код", Toast.LENGTH_LONG).show()
    }

    override fun callbackWrongCode() {
        showViewsOnScreen()
        showWrongCode()
        codeInput.error = "Неправильный код"
        codeInput.requestFocus()
    }

    private fun goBackToAuthorization() {
        super.onBackPressed()
    }

    override fun goToRegistration(phone: String) {
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.putExtra(USER_PHONE, phone)
        startActivity(intent)
        overridePendingTransition(0,0)
        finish()
    }

    override fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0,0)
        finish()
    }

    override fun sendVerificationCode(phoneNumber: String, callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                callback)
    }

    companion object {
        private val TAG = "DBInf"
    }
}