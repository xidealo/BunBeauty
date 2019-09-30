package com.bunbeauty.ideal.myapplication.cleanArchitecture.presentation.logIn

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.entity.User
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithViewApi

class RegistrationActivity : AppCompatActivity(), View.OnClickListener {

    private var nameInput: EditText? = null
    private var surnameInput: EditText? = null
    private var phoneInput: EditText? = null
    private var citySpinner: Spinner? = null

    private val TAG = "DBInf"

    private var registrationInteractor: RegistrationInteractor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration)
        initView()
    }

    private fun initView() {
        Log.d(TAG, "initView RegistrationActivity: ")
        val registrationBtn = findViewById<Button>(R.id.saveDataRegistrationBtn)

        nameInput = findViewById(R.id.nameRegistrationInput)
        surnameInput = findViewById(R.id.surnameRegistrationInput)
        phoneInput = findViewById(R.id.phoneRegistrationInput)
        citySpinner = findViewById(R.id.citySpinnerRegistrationSpinner)
        //Заполняем поле телефона
        registrationInteractor = RegistrationInteractor()
        phoneInput!!.setText(registrationInteractor!!.getMyPhoneNumber(intent))

        registrationBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        WorkWithViewApi.hideKeyboard(this)

        when (v.id) {

            R.id.saveDataRegistrationBtn -> {
                val defaultPhotoLink = "https://firebasestorage." +
                        "googleapis.com/v0/b/bun-beauty.appspot.com/o/avatar%2FdefaultAva." +
                        "jpg?alt=media&token=f15dbe15-0541-46cc-8272-2578627ed311"
                val name: String
                val surname: String

                if (nameInput!!.text.toString().isNotEmpty()) {
                    if (registrationInteractor!!.nameInputCorrect(nameInput!!.text.toString())) {
                        if (registrationInteractor!!.nameLengthLessTwenty(nameInput!!.text.toString())) {
                            name = nameInput!!.text.toString()
                        } else {
                            assertNameSoLong()
                            return
                        }
                    } else {
                        nameInput!!.error = "Допустимы только буквы и тире"
                        nameInput!!.requestFocus()
                        return
                    }
                } else {
                    nameInput!!.error = "Введите своё имя"
                    nameInput!!.requestFocus()
                    return
                }

                if (!surnameInput!!.text.toString().isEmpty()) {
                    if (registrationInteractor!!.surnameInputCorrect(surnameInput!!.text.toString())) {
                        if (registrationInteractor!!.surnameLengthLessTwenty(surnameInput!!.text.toString())) {
                            surname = surnameInput!!.text.toString()
                        } else {
                            assertSurnameSoLong()
                            return
                        }
                    } else {
                        surnameInput!!.error = "Допустимы только буквы и тире"
                        surnameInput!!.requestFocus()
                        return
                    }
                } else {
                    surnameInput!!.error = "Введите свою фамилию"
                    surnameInput!!.requestFocus()
                    return
                }

                if (registrationInteractor!!.cityInputCorrect(citySpinner!!.selectedItem.toString().toLowerCase())) {
                    val fullName = "$name $surname"
                    val user = User(registrationInteractor!!.getUserId(), fullName, citySpinner!!.selectedItem.toString().toLowerCase(),
                            phoneInput!!.text.toString(), 0f, 0, defaultPhotoLink)
                    registrationInteractor!!.registration(user, this)
                    registrationInteractor!!.goToProfile(this)
                } else {
                    assertNoSelectedCity()
                    return
                }
            }
        }
    }

    private fun assertNameSoLong() {
        Toast.makeText(this, "Слишком длинное имя", Toast.LENGTH_SHORT).show()
    }

    private fun assertSurnameSoLong() {
        Toast.makeText(this, "Слишком длинная фамилия", Toast.LENGTH_SHORT).show()
    }

    private fun assertNoSelectedCity() {
        Toast.makeText(this, "Выберите город", Toast.LENGTH_SHORT).show()
    }
}