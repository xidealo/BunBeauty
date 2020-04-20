package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing

import android.os.Bundle
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity

class EditProfileActivity : MvpAppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)
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