package com.example.ideal.myapplication.logIn;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.WorkWithViewApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.other.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity implements View.OnClickListener {

    private static final String PHONE = "phone";
    private static final String USERS = "users";

    private static final String NAME = "name";
    private static final String CITY = "city";
    private static final String PHOTO_LINK = "photo link";
    private static final String AVG_RATING = "avg rating";

    private EditText nameInput;
    private EditText surnameInput;
    private EditText cityInput;
    private EditText phoneInput;
    private String defaultPhotoLink = "https://firebasestorage." +
            "googleapis.com/v0/b/bun-beauty.appspot.com/o/avatar%2FdefaultAva." +
            "jpg?alt=media&token=f15dbe15-0541-46cc-8272-2578627ed311";
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        Button registrateBtn = findViewById(R.id.saveDataRegistrationBtn);

        nameInput = findViewById(R.id.nameRegistrationInput);
        surnameInput = findViewById(R.id.surnameRegistrationInput);
        cityInput = findViewById(R.id.cityRegistrationInput);
        phoneInput = findViewById(R.id.phoneRegistrationInput);
        //Заполняем поле телефона
        String phoneNumber = getIntent().getStringExtra(PHONE);
        phoneInput.setText(phoneNumber);

        dbHelper = new DBHelper(this);

        registrateBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        WorkWithViewApi.hideKeyboard(this);

        switch (v.getId()) {
            case R.id.saveDataRegistrationBtn:
                //получение данных с инпутов

                //проверка на незаполенные поля
                if (areInputsCorrect()) {
                    User user = new User();
                    //если имя не устанавлявается, значит выводим тоаст и выходим из кейса
                    String name = nameInput.getText().toString().toLowerCase();
                    String surname = surnameInput.getText().toString().toLowerCase();

                    if (name.length() > 20) {
                        assertNameSoLong();
                        break;
                    } else {
                        if (surname.length() > 20) {
                            assertSurnameSoLong();
                            break;
                        } else {
                            String fullName = name + " " + surname;
                            String phone = phoneInput.getText().toString();
                            user.setName(fullName);
                            user.setCity(cityInput.getText().toString().toLowerCase());
                            user.setPhone(phone);
                            registration(user);
                            //идем в профиль
                            goToProfile();
                        }
                    }
                }
                break;

            default:
                break;
        }
    }

    private void registration(User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);

        Map<String, Object> items = new HashMap<>();
        items.put(NAME, user.getName());
        items.put(AVG_RATING, 0);
        items.put(CITY, user.getCity());
        items.put(PHONE, user.getPhone());
        items.put(PHOTO_LINK, defaultPhotoLink);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user.setId(userId);
        myRef = myRef.child(userId);
        myRef.updateChildren(items);

        //заносим данные о пользователе в локальную базу данных
        putDataInLocalStorage(user);
    }

    private void putDataInLocalStorage(User user) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(DBHelper.TABLE_CONTACTS_USERS, null, null);

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, user.getId());
        contentValues.put(DBHelper.KEY_NAME_USERS, user.getName());
        contentValues.put(DBHelper.KEY_RATING_USERS, "0");
        contentValues.put(DBHelper.KEY_CITY_USERS, user.getCity());
        contentValues.put(DBHelper.KEY_PHONE_USERS, user.getPhone());

        database.insert(DBHelper.TABLE_CONTACTS_USERS, null, contentValues);

        putPhotoInLocalStorage(user);
    }

    private void putPhotoInLocalStorage(User user) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, user.getId());
        contentValues.put(DBHelper.KEY_PHOTO_LINK_PHOTOS, defaultPhotoLink);
        contentValues.put(DBHelper.KEY_OWNER_ID_PHOTOS, user.getId());

        database.insert(DBHelper.TABLE_PHOTOS, null, contentValues);
    }

    private Boolean areInputsCorrect() {
        String name = nameInput.getText().toString();
        if (name.isEmpty()) {
            nameInput.setError("Введите своё имя");
            nameInput.requestFocus();
            return false;
        }

        if (!name.matches("[a-zA-ZА-Яа-я\\-]+")) {
            nameInput.setError("Допустимы только буквы и тире");
            nameInput.requestFocus();
            return false;
        }

        String surname = surnameInput.getText().toString();
        if (surname.isEmpty()) {
            surnameInput.setError("Введите свою фамилию");
            surnameInput.requestFocus();
            return false;
        }

        if (!surname.matches("[a-zA-ZА-Яа-я\\-]+")) {
            surnameInput.setError("Допустимы только буквы и тире");
            surnameInput.requestFocus();
            return false;
        }

        String city = cityInput.getText().toString();
        if (city.isEmpty()) {
            cityInput.setError("Введите город, в которым вы живёте");
            cityInput.requestFocus();
            return false;
        }

        if (!city.matches("[a-zA-ZА-Яа-я\\-]+")) {
            cityInput.setError("Допустимы только буквы и тире");
            cityInput.requestFocus();
            return false;
        }

        return true;
    }

    private void goToProfile() {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
        finish();
    }

    private void assertNameSoLong() {
        Toast.makeText(this, "Слишком длинное имя", Toast.LENGTH_SHORT).show();
    }
    private void assertSurnameSoLong() {
        Toast.makeText(this, "Слишком длинная фамилия", Toast.LENGTH_SHORT).show();
    }
}