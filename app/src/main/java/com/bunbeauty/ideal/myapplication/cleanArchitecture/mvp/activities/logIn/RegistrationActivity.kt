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
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IRegistrationUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.FirebaseModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.InteractorModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.intarfaces.IAdapterSpinner
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn.RegistrationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.RegistrationView
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithViewApi
import kotlinx.android.synthetic.main.activity_registration.*
import javax.inject.Inject

class RegistrationActivity : MvpAppCompatActivity(), RegistrationView, IAdapterSpinner {

    @Inject
    lateinit var registrationUserInteractor: IRegistrationUserInteractor

    @InjectPresenter
    lateinit var registrationPresenter: RegistrationPresenter

    @ProvidePresenter
    internal fun provideRegistrationPresenter(): RegistrationPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .build()
            .inject(this)

        return RegistrationPresenter(registrationUserInteractor)
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
            registrationPresenter.registerUser(
                WorkWithStringsApi.firstCapitalSymbol(nameRegistrationInput.text.toString().trim()),
                WorkWithStringsApi.firstCapitalSymbol(
                    surnameRegistrationInput.text.toString().trim()
                ),
                WorkWithStringsApi.firstCapitalSymbol(cityRegistrationSpinner.text.toString()),
                phoneRegistrationInput.text.toString()
            )
        }

    }

    override fun fillPhoneInput(phone: String) = phoneRegistrationInput.setText(phone)

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

    override fun showSuccessfulRegistration() {
        Toast.makeText(this, "Пользователь зарегестирован", Toast.LENGTH_LONG).show()
    }

    override fun goToProfile(user: User) {
        val intent = Intent(this, ProfileActivity::class.java).apply {
            putExtra(User.USER, user)
            putExtra(REGISTRATION_ACTIVITY, "registration")
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    companion object{
        const val REGISTRATION_ACTIVITY = "registration activity"
    }
}