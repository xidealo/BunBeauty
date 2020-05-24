package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.intarfaces.IAdapterSpinner
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn.RegistrationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.RegistrationView
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithViewApi
import kotlinx.android.synthetic.main.activity_registration.*
import javax.inject.Inject

class RegistrationActivity : MvpAppCompatActivity(), RegistrationView, IAdapterSpinner {

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

        return RegistrationPresenter(registrationInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        configViews()
    }

    private fun configViews() {
        phoneRegistrationInput.setText(registrationPresenter.getMyPhoneNumber())

        setAdapter(
            arrayListOf(*resources.getStringArray(R.array.cities)),
            cityRegistrationSpinner,
            this
        )
        (cityRegistrationSpinner.adapter as ArrayAdapter<String>).filter.filter("")

        saveRegistrationBtn.setOnClickListener {
            WorkWithViewApi.hideKeyboard(this)
            saveData()
        }
    }

    private fun saveData() {
        registrationPresenter.checkDataAndRegisterUser(
            WorkWithStringsApi.firstCapitalSymbol(nameRegistrationInput.text.toString().trim()),
            WorkWithStringsApi.firstCapitalSymbol(surnameRegistrationInput.text.toString().trim()),
            WorkWithStringsApi.firstCapitalSymbol(cityRegistrationSpinner.text.toString()),
            phoneRegistrationInput.text.toString()
        )
    }

    override fun fillPhoneInput(phone: String) = phoneRegistrationInput.setText(phone)

    override fun showSuccessfulRegistration() {
        Toast.makeText(this, "Пользователь зарегестирован", Toast.LENGTH_LONG).show()
    }

    override fun disableRegistrationButton() {
        saveRegistrationBtn.isEnabled = false
    }

    override fun enableRegistrationButton() {
        saveRegistrationBtn.isEnabled = true
    }

    override fun setNameInputError(error: String) {
        nameRegistrationInput.error = error
        nameRegistrationInput.requestFocus()
    }

    override fun setSurnameInputError(error: String) {
        surnameRegistrationInput.error = error
        surnameRegistrationInput.requestFocus()
    }

    override fun showNoSelectedCity() {
        Toast.makeText(this, "Выберите город", Toast.LENGTH_LONG).show()
    }

    override fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }

}