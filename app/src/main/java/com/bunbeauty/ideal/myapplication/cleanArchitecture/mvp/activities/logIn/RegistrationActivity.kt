package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn.RegistrationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.RegistrationView
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithViewApi
import javax.inject.Inject

class RegistrationActivity : MvpAppCompatActivity(), View.OnClickListener,
    RegistrationView {

    private lateinit var nameInput: EditText
    private lateinit var surnameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var citySpinner: Spinner
    private lateinit var registrationBtn:Button
    private val TAG = "DBInf"

    @Inject
    lateinit var registrationInteractor: RegistrationInteractor

    @InjectPresenter
    lateinit var registrationPresenter: RegistrationPresenter

    @ProvidePresenter
    internal fun provideRegistrationPresenter(): RegistrationPresenter {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(application, intent))
                .build().inject(this)

        return RegistrationPresenter(
            registrationInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration)
        init()
    }

    private fun init() {
        Log.d(TAG, "initView RegistrationActivity: ")
        registrationBtn = findViewById(R.id.saveRegistrationBtn)

        nameInput = findViewById(R.id.nameRegistrationInput)
        surnameInput = findViewById(R.id.surnameRegistrationInput)
        phoneInput = findViewById(R.id.phoneRegistrationInput)
        citySpinner = findViewById(R.id.citySpinnerRegistrationSpinner)
        phoneInput.setText(registrationPresenter.getMyPhoneNumber())

        registrationBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        WorkWithViewApi.hideKeyboard(this)

        when (v.id) {
            R.id.saveRegistrationBtn -> {
                registrationPresenter.registration(
                        WorkWithStringsApi.firstCapitalSymbol(nameInput.text.toString().trim()),
                        WorkWithStringsApi.firstCapitalSymbol(surnameInput.text.toString().trim()),
                        WorkWithStringsApi.firstCapitalSymbol(citySpinner.selectedItem.toString()),
                        phoneInput.text.toString()
                )
            }
        }
    }

    override fun fillPhoneInput(phone: String) = phoneInput.setText(phone)

    override fun showSuccessfulRegistration() {
        Toast.makeText(this, "Пользователь зарегестирован", Toast.LENGTH_LONG).show()
    }

    override fun disableRegistrationButton() {
        registrationBtn.isEnabled = false
    }

    override fun enableRegistrationButton() {
        registrationBtn.isEnabled = true
    }

    override fun setNameInputError(error: String) {
        nameInput.error = error
        nameInput.requestFocus()
    }

    override fun setSurnameInputError(error: String) {
        surnameInput.error = error
        surnameInput.requestFocus()
    }

    override fun showNoSelectedCity() {
        Toast.makeText(this, "Выберите город", Toast.LENGTH_LONG).show()
    }

    override fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(0,0)
    }

}