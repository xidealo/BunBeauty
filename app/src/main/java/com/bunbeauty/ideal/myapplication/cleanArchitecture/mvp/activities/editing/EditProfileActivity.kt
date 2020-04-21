package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing

import android.media.Image
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel

abstract class EditProfileActivity : MvpAppCompatActivity(), ITopPanel, IBottomPanel, View.OnClickListener {

    private lateinit var avatarEditProfileImage: ImageView
    private lateinit var logOutEditProfileBtn: Button
    private lateinit var nameEditProfileInput: EditText
    private lateinit var surnameEditProfileInput: EditText
    private lateinit var citySpinnerEditProfileSpinner: Spinner
    private lateinit var codeEditProfileSpinner: Spinner
    private lateinit var phoneEditProfileInput: EditText
    private lateinit var codeEditProfileInput: EditText
    private lateinit var verifyCodeEditProfileBtn: Button
    private lateinit var resendCodeEditProfileBtn: Button
    private lateinit var editProfileEditProfileBtn: Button

    fun onCreatу(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)
        init()
        createPanels()
    }


    private fun init(){
        avatarEditProfileImage =findViewById(R.id.avatarEditProfileImage)
        logOutEditProfileBtn =findViewById(R.id.logOutEditProfileBtn)
        nameEditProfileInput =findViewById(R.id.nameEditProfileInput)
        surnameEditProfileInput =findViewById(R.id.surnameEditProfileInput)
        citySpinnerEditProfileSpinner =findViewById(R.id.citySpinnerEditProfileSpinner)
        phoneEditProfileInput =findViewById(R.id.phoneEditProfileInput)
        codeEditProfileInput =findViewById(R.id.codeEditProfileInput)
        verifyCodeEditProfileBtn =findViewById(R.id.verifyCodeEditProfileBtn)
        resendCodeEditProfileBtn =findViewById(R.id.resendCodeEditProfileBtn)
        editProfileEditProfileBtn =findViewById(R.id.editProfileEditProfileBtn)
    }


    private fun createPanels(){
        createBottomPanel(supportFragmentManager)
        createTopPanel("Редактирование профиля", ButtonTask.NONE, supportFragmentManager)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)
    }
    override fun onClick(v: View) {
        when(v.id){
        }
    }



    /* private fun areInputsCorrect(): Boolean {
         val name = nameInput!!.text.toString()
         if (name.isEmpty()) {
             nameInput!!.error = "Введите своё имя"
             nameInput!!.requestFocus()
             return false
         }
         if (!name.matches("[a-zA-ZА-Яа-я\\-]+")) {
             nameInput!!.error = "Допустимы только буквы и тире"
             nameInput!!.requestFocus()
             return false
         }
         val surname = surnameInput!!.text.toString()
         if (surname.isEmpty()) {
             surnameInput!!.error = "Введите свою фамилию"
             surnameInput!!.requestFocus()
             return false
         }
         if (!surname.matches("[a-zA-ZА-Яа-я\\-]+")) {
             surnameInput!!.error = "Допустимы только буквы и тире"
             surnameInput!!.requestFocus()
             return false
         }
         val city = citySpinner!!.selectedItem.toString()
         if (city == "Выбрать город") {
             assertNoSelectedCity()
             return false
         }
         return true
     }*/

   /* private fun goToLogIn() {
        FirebaseAuth.getInstance().signOut()
        ListeningManager.removeAllListeners()
        stopService(Intent(this, MyService::class.java))
        //ListeningManager.removeAllListeners();
        val intent = Intent(this, AuthorizationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }*/

   /* companion object {
        private const val USERS = "users"
        private const val USER_NAME = "name"
        private const val USER_CITY = "city"
        private const val PHONE = "phone"
        private const val AVATAR = "avatar"
        private const val PHOTO_LINK = "photo link"
        private const val TOKEN = "token"
    }*/


}