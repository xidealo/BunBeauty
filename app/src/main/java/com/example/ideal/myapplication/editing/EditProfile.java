package com.example.ideal.myapplication.editing;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.logIn.Authorization;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    //изменяет номер телефона во всех таблицах, где используется
    private final String TAG = "DBInf";

    private static final String USER_NAME = "name";
    private static final String PASS = "password";
    private static final String USER_CITY = "city";

    private static final String DIALOGS = "dialogs";
    private static final String USERS = "users";
    private static final String WORKING_TIME = "working time";
    private static final String SERVICE = "services";

    private static final String USER_ID = "user id";

    private static final String PHONE_NUMBER = "Phone number";
    private static final String OWNER_ID = "owner id";
    private static final String FILE_NAME = "Info";
    private static final String FIRST_PHONE = "first phone";
    private static final String SECOND_PHONE = "second phone";

    private String oldPhone;
    private String phone;

    private Button editBtn;
    private Button verifyButton;
    private Button resendButton;

    private EditText nameInput;
    private EditText cityInput;
    private EditText phoneInput;
    private EditText codeInput;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private DBHelper dbHelper;
    private SharedPreferences sPref;
    private User user;

    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        nameInput = findViewById(R.id.nameEditProfileInput);
        cityInput = findViewById(R.id.cityEditProfileInput);
        phoneInput = findViewById(R.id.phoneEditProfileInput);
        codeInput = findViewById(R.id.codeEditProfileInput);

        editBtn = findViewById(R.id.editProfileEditProfileBtn);
        resendButton = findViewById(R.id.resendProfileEditProfileBtn);
        verifyButton = findViewById(R.id.verifyProfileEditProfileBtn);

        FragmentManager manager = getSupportFragmentManager();
        PanelBuilder panelBuilder = new PanelBuilder(this);
        panelBuilder.buildFooter(manager, R.id.footerEditProfileLayout);
        panelBuilder.buildHeader(manager, "Создание сервиса", R.id.headerEditProfileLayout);
        Log.d(TAG, " R.id.footerAddServiceLayout: " +  R.id.footerAddServiceLayout);

        fbAuth = FirebaseAuth.getInstance();
        user = new User();
        dbHelper = new DBHelper(this);

        oldPhone = getUserPhone();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sqlQuery = "SELECT "
                + DBHelper.KEY_NAME_USERS + ", "
                + DBHelper.KEY_CITY_USERS + ", "
                + DBHelper.KEY_USER_ID
                + " FROM " + DBHelper.TABLE_CONTACTS_USERS
                + " WHERE " + DBHelper.KEY_USER_ID + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{oldPhone});

        if (cursor.moveToFirst()) {
            int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_USERS);
            int indexCity = cursor.getColumnIndex(DBHelper.KEY_CITY_USERS);
            int indexPhone = cursor.getColumnIndex(DBHelper.KEY_USER_ID);

            nameInput.setText(cursor.getString(indexName));
            cityInput.setText(cursor.getString(indexCity));
            phoneInput.setText(cursor.getString(indexPhone));
        }
        cursor.close();

        editBtn.setOnClickListener(this);
        resendButton.setOnClickListener(this);
        verifyButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.editProfileEditProfileBtn:
                checkPhone();
                break;

            case R.id.verifyProfileEditProfileBtn:

                String code = codeInput.getText().toString();
                if (!code.trim().equals("")) {
                    // подтверждаем код и если все хорошо, создаем юзера
                    verifyCode(code);
                }
                break;

            case R.id.resendConfirmationBtn:
                resendCode();
                break;

            default:
                break;
        }
    }

    private void checkPhone() {
        int phoneLength = String.valueOf(phoneInput.getText()).length();
        if (phoneLength > 0) {
            phone = convertPhoneToNormalView(String.valueOf(phoneInput.getText()));

            user.setName(nameInput.getText().toString().toLowerCase());
            user.setCity(cityInput.getText().toString().toLowerCase());

            //можно объеденить ссылки?
            DatabaseReference reference = FirebaseDatabase
                    .getInstance()
                    .getReference(USERS).child(phone);

            // Сравниваем телефон в поле ввода и уже имеющийся
            if (phone.equals(oldPhone)) {
                // Номер не изменился

                updateInfo();
                goToProfile();
            } else {
                // Номер изменился

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //такого номера нет
                        if (dataSnapshot.getChildrenCount() == 0) {
                            sendCode(phone);
                        } else {
                            attentionThisUserAlreadyReg();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        attentionBadConnection();
                    }
                });
            }
        } else {
            attentionEmptyPhoneNumberField();
        }
    }

    private void updateInfo() {
        DatabaseReference reference = FirebaseDatabase
                .getInstance()
                .getReference(USERS).child(oldPhone);

        Map<String, Object> items = new HashMap<>();
        if (user.getName() != null) items.put(USER_NAME, user.getName());
        if (user.getCity() != null) items.put(USER_CITY, user.getCity());
        reference.updateChildren(items);

        updateInfoInLocalStorage();
    }

    private void sendCode(String phoneNumber) {

        setUpVerificationCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
    }

    public void verifyCode(String code) {
        //получаем ответ гугл
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);
        //заходим с айфоном и токеном
        signInWithPhoneAuthCredential(credential);
    }

    private void setUpVerificationCallbacks() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        //вызывается, если номер подтвержден
                        codeInput.setText("");
                        //выводит соообщение о том, что пользователь уже зарегестрирован
                        //пользователь уже проверен, значит зарегестрирован
                        attentionThisUserAlreadyReg();
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            attentionInvalidPhoneNumber();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Log.d(TAG, "SMS Quota exceeded.");
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        //происходит, когда отослали код
                        phoneVerificationId = verificationId;
                        resendToken = token;

                        codeInput.setVisibility(View.VISIBLE);
                        resendButton.setVisibility(View.VISIBLE);
                        verifyButton.setVisibility(View.VISIBLE);
                    }
                };
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        //входим
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //если введенный код совпадает с присланным кодом
                        if (task.isSuccessful()) {
                            updatePhone();

                            //сохраняем его в лок данные
                            savePhone();
                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                attentionThisCodeWasWrong();
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void updatePhone() {

        //я не знаю, как сделать update id, поэтому я ксоздаю нового пользователя и удаляю старого
        deleteOldPhoneNumber();
        createNewPhoneNumber();
        savePhone();
        updateOtherPlaceWithPhone();
        //добавить обновление локальной бд
        //не буду менять локальнгый бд, просто выкину его с финишом на авторизацию
        //там перезайдет и подгрузим все данные
    }

    //удаляем старый телефон в таблице юзер
    private void deleteOldPhoneNumber(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/" + oldPhone );

        myRef.removeValue();
    }

    private void createNewPhoneNumber() {
        //создаем новый номер и данные с ним в таблице юзер
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference  myRef = database.getReference("users/" + phone);
        Map<String,Object> items = new HashMap<>();
        items.put("name", user.getName());
        items.put("city", user.getCity());
        items.put("password", getUserPass());
        myRef.updateChildren(items);
    }

    private void updateOtherPlaceWithPhone() {
        updateWorkingTime();
        updateServices();
        updateDialogs();
        goToAuthorization();
    }

    private void updateWorkingTime() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        Query query = database.getReference(WORKING_TIME)
                .orderByChild(USER_ID)
                .equalTo(oldPhone);
        //находим id времени по телефону и меняем в нем телефон
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot timeSnapshot: dataSnapshot.getChildren()){

                    String timeId = timeSnapshot.getKey();

                    DatabaseReference myRef = database.getReference(WORKING_TIME).child(timeId);
                    Map<String,Object> items = new HashMap<>();
                    items.put(USER_ID, phone);
                    myRef.updateChildren(items);

                    updateWorkingTimeInLocalStorage(timeId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void updateWorkingTimeInLocalStorage(String timeId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_USER_ID, phone);

        database.update(DBHelper.TABLE_WORKING_TIME, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(timeId)});
    }

    private void updateServices() {
        //аналогично с working days
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final Query query = database.getReference(SERVICE)
                .orderByChild(USER_ID)
                .equalTo(oldPhone);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot serviceSnapshot: dataSnapshot.getChildren()){

                    String serviceId = serviceSnapshot.getKey();

                    DatabaseReference myRef = database.getReference(SERVICE).child(serviceId);
                    Map<String,Object> items = new HashMap<>();
                    items.put(USER_ID, phone);
                    myRef.updateChildren(items);

                    updateServiceInLocalStorage(serviceId);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void updateServiceInLocalStorage(String serviceId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_USER_ID, phone);

        database.update(DBHelper.TABLE_CONTACTS_SERVICES, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(serviceId)});
    }

    private void updateDialogs() {
        checkFirstPhone();
        checkSecondPhone();
    }

    private void checkFirstPhone() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        Query firstPhoneQuery = database.getReference(DIALOGS)
                .orderByChild(FIRST_PHONE)
                .equalTo(oldPhone);
        firstPhoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dialogSnapshot: dataSnapshot.getChildren()) {
                    DatabaseReference myRef = database.getReference(DIALOGS).child(dialogSnapshot.getKey());
                    Map<String, Object> items = new HashMap<>();
                    items.put(FIRST_PHONE, phone);
                    myRef.updateChildren(items);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void checkSecondPhone(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        Query firstPhoneQuery = database.getReference(DIALOGS)
                .orderByChild(SECOND_PHONE)
                .equalTo(oldPhone);
        firstPhoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dialogSnapshot: dataSnapshot.getChildren()) {
                    DatabaseReference myRef = database.getReference(DIALOGS).child(dialogSnapshot.getKey());
                    Map<String, Object> items = new HashMap<>();
                    items.put(SECOND_PHONE, phone);
                    myRef.updateChildren(items);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    //Обновление информации в БД
    private void updateInfoInLocalStorage() {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        if(user.getName()!=null) contentValues.put(DBHelper.KEY_NAME_USERS, user.getName());
        if(user.getCity()!=null) contentValues.put(DBHelper.KEY_CITY_USERS, user.getCity());

        if(contentValues.size()>0) {
            database.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                    DBHelper.KEY_USER_ID + " = ?",
                    new String[]{String.valueOf(oldPhone)});
        }
    }

    public void resendCode() {

        String phoneNumber = phoneInput.getText().toString();

        setUpVerificationCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
    }

    private String getUserPhone() {
        sPref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        return  sPref.getString(PHONE_NUMBER, "-");
    }

    private String convertPhoneToNormalView(String phone) {
        if(phone.charAt(0)=='8'){
            phone = "+7" + phone.substring(1);
        }
        return phone;
    }

    private void attentionThisUserAlreadyReg(){
        Toast.makeText(
                this,
                "Данный пользователь уже зарегестрирован.",
                Toast.LENGTH_SHORT).show();
    }

    private void attentionInvalidPhoneNumber(){
        Toast.makeText(
                this,
                "Неправильный номер",
                Toast.LENGTH_SHORT).show();
    }

    private void attentionEmptyPhoneNumberField() {
        Toast.makeText(
                this,
                "Поле с номером телефона не зполнено",
                Toast.LENGTH_SHORT).show();
    }

    private void attentionThisCodeWasWrong(){
        Toast.makeText(
                this,
                "Код введен неверно",
                Toast.LENGTH_SHORT).show();
    }

    private void savePhone() {
        sPref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(PHONE_NUMBER, phone);
        editor.putString(OWNER_ID, phone);
        editor.apply();
    }

    private String getUserPass() {
        sPref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        return  sPref.getString(PASS, "-");
    }

    private void goToAuthorization(){
        Intent intent = new Intent(EditProfile.this, Authorization.class);
        startActivity(intent);
        finish();
    }

    private void goToProfile() {
        super.onBackPressed();
    }

    private void attentionBadConnection() {
        Toast.makeText(this,"Плохое соединение",Toast.LENGTH_SHORT).show();
    }
}