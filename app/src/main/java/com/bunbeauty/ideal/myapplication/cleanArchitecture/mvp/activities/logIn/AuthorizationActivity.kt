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
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.AuthorizationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AuthorizationView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithViewApi
import com.bunbeauty.ideal.myapplication.logIn.CountryCodes
import javax.inject.Inject

class AuthorizationActivity : MvpAppCompatActivity(), View.OnClickListener, AuthorizationView {

    private lateinit var verifyBtn: Button
    private lateinit var enterPhoneText: TextView
    private lateinit var phoneInput: EditText
    private lateinit var codeSpinner: Spinner
    private lateinit var progressBar: ProgressBar

    @Inject
    lateinit var authorizationInteractor: AuthorizationInteractor

    @InjectPresenter
    lateinit var authorizationPresenter: AuthorizationPresenter

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var userRepository: UserRepository

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
        setContentView(R.layout.authorization)

        initView()
        authorizationPresenter.authorize()
    }

    private fun initView() {
        verifyBtn = findViewById(R.id.verifyAuthBtn)
        phoneInput = findViewById(R.id.phoneAuthInput)
        enterPhoneText = findViewById(R.id.titleAuthText)
        progressBar = findViewById(R.id.progressBarAuthorization)
        codeSpinner = findViewById(R.id.codeAuthSpinner)
        verifyBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.verifyAuthBtn) {
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
        Log.d(TAG_UI, "hideViewsOfScreen")
        progressBar.visibility = View.VISIBLE
        codeSpinner.visibility = View.GONE
        phoneInput.visibility = View.GONE
        verifyBtn.visibility = View.GONE
    }

    override fun showViewsOnScreen() {
        Log.d(TAG_UI, "showViewsOnScreen: ")
        codeSpinner.visibility = View.VISIBLE
        enterPhoneText.visibility = View.VISIBLE
        phoneInput.visibility = View.VISIBLE
        verifyBtn.visibility = View.VISIBLE
    }

    override fun setPhoneError() {
        Log.d(TAG_UI, "setPhoneError: ")
        phoneInput.error = "Некорректный номер"
        phoneInput.requestFocus()
    }

    override fun enableVerifyBtn(status: Boolean) {
        Log.d(TAG_UI, "enableVerifyBtn: $status")
        verifyBtn.isClickable = status
    }

    override fun goToVerifyPhone(phone: String) {
        val intent = Intent(this, VerifyPhoneActivity::class.java)
        this.startActivity(intent)
    }

    override fun goToRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)
        this.startActivity(intent)
    }

    override fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        this.startActivity(intent)
    }

    override fun hideKeyboard() {
        WorkWithViewApi.hideKeyboard(this)
    }

    companion object {
        private val TAG_UI = "tag_ui"
    }
}