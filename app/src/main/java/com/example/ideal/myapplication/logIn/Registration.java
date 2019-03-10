package com.example.ideal.myapplication.logIn;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ideal.myapplication.helpApi.WorkWithViewApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.other.Profile;
import com.example.ideal.myapplication.fragments.objects.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity implements View.OnClickListener {

    private static final String STATUS = "status";
    private static final String FILE_NAME = "Info";

    private static final String PHONE_NUMBER = "Phone number";
    private static final String REF = "users";

    private static final String NAME = "name";
    private static final String CITY = "city";

    Button registrateBtn;

    EditText nameInput;
    EditText surnameInput;
    EditText cityInput;
    EditText phoneInput;

    DBHelper dbHelper;

    User user;

    SharedPreferences sPref;    //класс для работы с записью в файлы

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        registrateBtn = findViewById(R.id.saveDataRegistrationBtn);

        nameInput = findViewById(R.id.nameRegistrationInput);
        surnameInput = findViewById(R.id.surnameRegistrationInput);
        cityInput = findViewById(R.id.cityRegistrationInput);
        phoneInput = findViewById(R.id.phoneRegistrationInput);
        //Заполняем поле телефона
        String phoneNumber = getIntent().getStringExtra(PHONE_NUMBER);
        phoneInput.setText(phoneNumber);

        dbHelper = new DBHelper(this);

        registrateBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        WorkWithViewApi.hideKeyboard(this);

        switch (v.getId()){
            case R.id.saveDataRegistrationBtn:
                //получение данных с инпутов

                //проверка на незаполенные поля
                if(areInputsCorrect()) {
                    user = new User();
                    //если имя не устанавлявается, значит выводим тоаст и выходим из кейса
                    String fullName = nameInput.getText().toString().toLowerCase() + " " + surnameInput.getText().toString().toLowerCase();
                    user.setName(fullName);
                    user.setCity(cityInput.getText().toString().toLowerCase());
                    registration(user);
                    //идем в профиль
                    goToProfile();
                }
                break;

            default:
                break;
        }
    }

    private void registration(User user) {
        String phoneNumber = phoneInput.getText().toString();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(REF).child(phoneNumber);

        Map<String,Object> items = new HashMap<>();
        items.put(NAME, user.getName());
        items.put(CITY, user.getCity());
        myRef.updateChildren(items);
        //заносим данные о пользователе в локальную базу данных
        putDataInLocalStorage(user, phoneNumber);
    }

    private void putDataInLocalStorage(User user, String phoneNumber) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(DBHelper.TABLE_CONTACTS_USERS, null, null);

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_NAME_USERS, user.getName());
        contentValues.put(DBHelper.KEY_CITY_USERS, user.getCity());
        contentValues.put(DBHelper.KEY_USER_ID, phoneNumber);

        database.insert(DBHelper.TABLE_CONTACTS_USERS,null,contentValues);
    }

    protected Boolean areInputsCorrect(){
        String name = nameInput.getText().toString();
        if(name.isEmpty()) {
            nameInput.setError("Введите своё имя");
            nameInput.requestFocus();
            return false;
        }

        if(!name.matches("[a-zA-ZА-Яа-я\\-]+")) {
            nameInput.setError("Допустимы только буквы и тире");
            nameInput.requestFocus();
            return false;
        }

        String surname = surnameInput.getText().toString();
        if(surname.isEmpty()) {
            surnameInput.setError("Введите свою фамилию");
            surnameInput.requestFocus();
            return false;
        }

        if(!surname.matches("[a-zA-ZА-Яа-я\\-]+")) {
            surnameInput.setError("Допустимы только буквы и тире");
            surnameInput.requestFocus();
            return false;
        }

        String city = cityInput.getText().toString();
        if(city.isEmpty()) {
            cityInput.setError("Введите город, в которым вы живёте");
            cityInput.requestFocus();
            return false;
        }

        if(!city.matches("[a-zA-ZА-Яа-я\\-]+")) {
            cityInput.setError("Допустимы только буквы и тире");
            cityInput.requestFocus();
            return false;
        }

        return  true;
    }

    private  void goToProfile(){
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
        finish();
    }

    private  void  goToAuthorization(){
        Intent intent = new Intent(this, Authorization.class);
        startActivity(intent);
        finish();
    }
}