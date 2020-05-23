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
import com.bunbeauty.ideal.myapplication.cleanArchitecture.Tag.UI_TAG
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.intarfaces.IAdapterSpinner
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn.AuthorizationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.AuthorizationView
import com.bunbeauty.ideal.myapplication.other.CountryCodes
import javax.inject.Inject

class AuthorizationActivity : MvpAppCompatActivity(), View.OnClickListener,
    AuthorizationView, IAdapterSpinner {

    private lateinit var verifyBtn: Button
    private lateinit var titleAuthorizationText: TextView
    private lateinit var phoneInput: EditText
    private lateinit var codeSpinner: Spinner
    private lateinit var progressBar: ProgressBar

    @Inject
    lateinit var authorizationInteractor: AuthorizationInteractor

    @InjectPresenter
    lateinit var authorizationPresenter: AuthorizationPresenter

    @ProvidePresenter
    internal fun provideAuthorizationPresenter(): AuthorizationPresenter {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(application, intent))
            .build().inject(this)

        return AuthorizationPresenter(authorizationInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        initView()

        authorizationPresenter.defaultAuthorize()
    }

    private fun initView() {
        phoneInput = findViewById(R.id.phoneAuthorizationInput)
        titleAuthorizationText = findViewById(R.id.titleAuthorizationText)
        progressBar = findViewById(R.id.loadingAuthorizationProgressBar)

        codeSpinner = findViewById(R.id.codeAuthorizationSpinner)
        setAdapter(
            arrayListOf(*resources.getStringArray(R.array.countryCode)),
            codeSpinner,
            this,
            R.layout.element_login_spinner
        )

        verifyBtn = findViewById(R.id.verifyAuthorizationBtn)
        verifyBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.verifyAuthorizationBtn) {
            authorizationPresenter.authorize(
                CountryCodes.codes[codeSpinner.selectedItemPosition] + phoneInput.text.toString()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        enableVerifyBtn(true)
    }

    override fun hideViewsOnScreen() {
        Log.d(UI_TAG, "hideViewsOfScreen")
        progressBar.visibility = View.VISIBLE
        codeSpinner.visibility = View.GONE
        phoneInput.visibility = View.GONE
        verifyBtn.visibility = View.GONE
        titleAuthorizationText.visibility = View.GONE
    }

    override fun showViewsOnScreen() {
        Log.d(UI_TAG, "showViewsOnScreen: ")
        progressBar.visibility = View.GONE
        codeSpinner.visibility = View.VISIBLE
        titleAuthorizationText.visibility = View.VISIBLE
        phoneInput.visibility = View.VISIBLE
        verifyBtn.visibility = View.VISIBLE
    }

    override fun showPhoneError(error: String) {
        Log.d(UI_TAG, "setPhoneError: ")
        phoneInput.error = error
        phoneInput.requestFocus()
    }

    override fun enableVerifyBtn(status: Boolean) {
        Log.d(UI_TAG, "enableVerifyBtn: $status")
        verifyBtn.isClickable = status
    }

    override fun goToVerifyPhone(phone: String) {
        val intent = Intent(this, VerifyPhoneNumberActivity::class.java)
        intent.putExtra(User.PHONE, phone)
        this.startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun goToRegistration(phone: String) {
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.putExtra(User.PHONE, phone)
        this.startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    override fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        this.startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    override fun hideKeyboard() {
        //WorkWithViewApi.hideKeyboard(this)
    }

    override fun disableButton() {
        verifyBtn.isEnabled = false
    }

    override fun enableButton() {
        verifyBtn.isEnabled = true
    }
}