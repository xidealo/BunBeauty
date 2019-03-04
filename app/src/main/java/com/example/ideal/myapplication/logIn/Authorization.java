package com.example.ideal.myapplication.logIn;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.WorkWithViewApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.other.Profile;
import com.example.ideal.myapplication.reviews.DownloadServiceData;
import com.example.ideal.myapplication.test.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;


public class Authorization extends AppCompatActivity implements View.OnClickListener {

    private static final String FILE_NAME = "Info";
    private static final String PHONE_NUMBER = "Phone number";
    private static final String PASS = "password";

    private static final String USERS = "users";
    private static final String NAME = "name";
    private static final String CITY = "city";

    private static final String SERVICES = "services";
    private static final String USER_ID = "user id";
    private static final String DESCRIPTION = "description";
    private static final String COST = "cost";

    private static final String WORKING_TIME = "working time";
    private static final String WORKING_DAY_ID = "working day id";
    private static final String TIME = "time";

    private static final String WORKING_DAYS = "working days";
    private static final String SERVICE_ID = "service id";
    private static final String DATE = "data";
    private static final String TAG = "DBInf";

    private Button logInBtn;
    private Button verifyCodeBtn;
    private Button resendCodeBtn;

    private EditText phoneInput;
    private EditText codeInput;

    private Spinner codeSpinner;

    private long orderCounter;
    private String myPhoneNumber;

    DBHelper dbHelper;
    SharedPreferences sPref; //класс для работы с записью в файлы
    FirebaseAuth fbAuth;
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization);

        dbHelper = new DBHelper(this);
        fbAuth = FirebaseAuth.getInstance();

        logInBtn = findViewById(R.id.logInAuthBtn);
        verifyCodeBtn = findViewById(R.id.verifyAuthBtn);
        resendCodeBtn = findViewById(R.id.resendAuthBtn);

        phoneInput = findViewById(R.id.phoneAuthInput);
        codeInput = findViewById(R.id.codeAuthInput);

        codeSpinner = findViewById(R.id.codeAuthSpinner);
        codeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CountryCodes.countryNames));

        logInBtn.setOnClickListener(this);
        verifyCodeBtn.setOnClickListener(this);
        resendCodeBtn.setOnClickListener(this);

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            myPhoneNumber = fbUser.getPhoneNumber();
            authorizeUser();
        } else {
            showViewsOnScreen();
        }
        WorkWithViewApi.hideKeyboard(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case  R.id.logInAuthBtn:
                myPhoneNumber = phoneInput.getText().toString();
                if(isPhoneCorrect()) {
                    logInBtn.setClickable(false);
                    String countryCode = CountryCodes.codes[codeSpinner.getSelectedItemPosition()];
                    myPhoneNumber = countryCode + phoneInput.getText().toString();

                    setUpVerificationCallbacks();
                    sendVerificationCode();
                }
                break;

            case R.id.verifyAuthBtn:
                String code = codeInput.getText().toString();

                if(code.trim().length() >= 6) {
                    // подтверждаем код и если все хорошо, создаем юзера
                    verifyCode(code);
                } else {
                    codeInput.setError("Неправильный код");
                    codeInput.requestFocus();
                }
                break;

            case R.id.resendAuthBtn:
                resendVerificationCode();
            default:
                break;
        }
    }

    private void setUpVerificationCallbacks() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        //вызывается, если номер подтвержден
                        codeInput.setText("");
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Log.d(TAG, "Invalid credential: "
                                    + e.getLocalizedMessage());
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
                        verifyCodeBtn.setVisibility(View.VISIBLE);
                        resendCodeBtn.setVisibility(View.VISIBLE);
                    }
                };
    }

    private void sendVerificationCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                myPhoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                verificationCallbacks
        );
    }

    private void resendVerificationCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                myPhoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks,         // OnVerificationStateChangedCallbacks
                resendToken);             // ForceResendingToken from callbacks
    }

    public void verifyCode(String code) {
        //получаем ответ гугл
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);
        //заходим с айфоном и токеном
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        //входим
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //если введен верный код
                        if (task.isSuccessful()) {
                            authorizeUser();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                codeInput.setError("Неправильный код");
                                codeInput.requestFocus();
                            }
                        }
                    }
                });
    }

    private void authorizeUser() {
        // скарываем Views и запукаем прогресс бар
        hideViewsOfScreen();

        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(USERS).child(myPhoneNumber);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                // Получаем остальные данные о пользователе
                Object name = userSnapshot.child(NAME).getValue();
                if (name == null) {
                    // Имя в БД отсутствует, значит пользователь не до конца зарегистрировался
                    goToRegistration(myPhoneNumber);
                } else {
                    String city = String.valueOf(userSnapshot.child(CITY).getValue());

                    User user = new User();
                    user.setPhone(myPhoneNumber);
                    user.setName(String.valueOf(name));
                    user.setCity(city);

                    // Очищаем LocalStorage
                    clearSQLite();

                    // Добавляем все данные о пользователе в SQLite
                    addUserInfoInLocalStorage(user);

                    // Загружаем сервисы пользователя из FireBase
                    loadServiceByUserPhone();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void loadServiceByUserPhone() {
        final SQLiteDatabase localDatabase = dbHelper.getWritableDatabase();
        Query query = FirebaseDatabase.getInstance().getReference(SERVICES).
                orderByChild(USER_ID).
                equalTo(myPhoneNumber);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long servicesCount = dataSnapshot.getChildrenCount();

                if(servicesCount==0){
                    loadTimeByUserPhone();
                    return;
                }
                long serviceCounter = 0;

                for (DataSnapshot service : dataSnapshot.getChildren()) {
                    String serviceId = String.valueOf(service.getKey());

                    DownloadServiceData downloadServiceData = new DownloadServiceData();
                    downloadServiceData.loadSchedule(serviceId, localDatabase,
                            "Authorization", null);
                    serviceCounter++;
                    if (serviceCounter == servicesCount) {
                        loadTimeByUserPhone();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void loadTimeByUserPhone() {
        Query timeQuery = FirebaseDatabase.getInstance().getReference(WORKING_TIME)
                .orderByChild(USER_ID)
                .equalTo(myPhoneNumber);
        timeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final long ordersCount = dataSnapshot.getChildrenCount();
                if(ordersCount==0){
                    goToProfile();
                    return;
                }
                orderCounter = 0;
                for(DataSnapshot timeSnapshot:dataSnapshot.getChildren()){
                    String timeId = String.valueOf(timeSnapshot.getKey());
                    String time = String.valueOf(timeSnapshot.child(TIME).getValue());
                    String timeWorkingDayId = String.valueOf(timeSnapshot.child(WORKING_DAY_ID).getValue());

                    addTimeInLocalStorage(timeId, time, myPhoneNumber, timeWorkingDayId);

                    loadWorkingDayById(timeWorkingDayId, ordersCount);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void loadWorkingDayById(String workingDayId, final long ordersCount) {
        DatabaseReference dayReference = FirebaseDatabase.getInstance().getReference(WORKING_DAYS).child(workingDayId);
        dayReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot day) {
                String dayServiceId = String.valueOf(day.child(SERVICE_ID).getValue());
                String dayId = String.valueOf(day.getKey());
                String dayDate = String.valueOf(day.child(DATE).getValue());

                addWorkingDayInLocalStorage(dayId, dayDate, dayServiceId);

                loadServiceById(dayServiceId, ordersCount);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void loadServiceById(String serviceId, final long ordersCount) {
        DatabaseReference serviceReference = FirebaseDatabase.getInstance().getReference(SERVICES).child(serviceId);
        serviceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot service) {
                String serviceId = String.valueOf(service.getKey());
                String serviceName = String.valueOf(service.child(NAME).getValue());
                String serviceDescription = String.valueOf(service.child(DESCRIPTION).getValue());
                String serviceCost = String.valueOf(service.child(COST).getValue());
                String serviceUserId = String.valueOf(service.child(USER_ID).getValue());

                Service newService = new Service();
                newService.setId(serviceId);
                newService.setName(serviceName);
                newService.setDescription(serviceDescription);
                newService.setCost(serviceCost);
                newService.setUserId(serviceUserId);

                addUserServicesInLocalStorage(newService);
                orderCounter++;

                if((orderCounter == ordersCount)) {
                    // Выполняем вход
                    goToProfile();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    // Обновляет информацию о текущем пользователе в SQLite
    private void addUserInfoInLocalStorage(User user) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Заполняем contentValues информацией о данном пользователе
        contentValues.put(DBHelper.KEY_NAME_USERS, user.getName());
        contentValues.put(DBHelper.KEY_CITY_USERS, user.getCity());
        contentValues.put(DBHelper.KEY_USER_ID, user.getPhone());

        // Добавляем данного пользователя в SQLite
        database.insert(DBHelper.TABLE_CONTACTS_USERS, null, contentValues);
    }

    // Удаляет все данные о пользователях, сервисах, рабочих днях и рабочем времени из SQLite
    private void clearSQLite() {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(DBHelper.TABLE_CONTACTS_USERS,null,null);
        database.delete(DBHelper.TABLE_CONTACTS_SERVICES, null, null);
        database.delete(DBHelper.TABLE_WORKING_DAYS,null,null);
        database.delete(DBHelper.TABLE_WORKING_TIME,null,null);

        database.delete(DBHelper.TABLE_MESSAGES, null,null);
        database.delete(DBHelper.TABLE_REVIEWS, null,null);
        database.delete(DBHelper.TABLE_ORDERS, null,null);
    }

    // Добавляет информацию о сервисах данного пользователя в SQLite
    private void addUserServicesInLocalStorage(Service service) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Заполняем contentValues информацией о данном сервисе
        contentValues.put(DBHelper.KEY_ID, service.getId());
        contentValues.put(DBHelper.KEY_NAME_SERVICES, service.getName());
        contentValues.put(DBHelper.KEY_USER_ID, service.getUserId());
        contentValues.put(DBHelper.KEY_DESCRIPTION_SERVICES, service.getDescription());
        contentValues.put(DBHelper.KEY_MIN_COST_SERVICES, service.getCost());

        // Добавляем данный сервис в SQLite
        database.insert(DBHelper.TABLE_CONTACTS_SERVICES, null, contentValues);
    }

    private void addTimeInLocalStorage(String timeId, String time,
                                       String timeUserId, String timeWorkingDayId) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_ID, timeId);
        contentValues.put(DBHelper.KEY_TIME_WORKING_TIME, time);
        contentValues.put(DBHelper.KEY_USER_ID,timeUserId);
        contentValues.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, timeWorkingDayId);

        database.insert(DBHelper.TABLE_WORKING_TIME,null,contentValues);
    }

    private void addWorkingDayInLocalStorage(String dayId, String dayDate, String serviceId) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, dayId);
        contentValues.put(DBHelper.KEY_DATE_WORKING_DAYS, dayDate);
        contentValues.put(DBHelper.KEY_SERVICE_ID_WORKING_DAYS, serviceId);

        database.insert(DBHelper.TABLE_WORKING_DAYS, null, contentValues);

    }

    private void showViewsOnScreen(){
        logInBtn.setVisibility(View.VISIBLE);
        phoneInput.setVisibility(View.VISIBLE);
    }

    private void hideViewsOfScreen(){
        logInBtn.setVisibility(View.INVISIBLE);
        verifyCodeBtn.setVisibility(View.INVISIBLE);
        resendCodeBtn.setVisibility(View.INVISIBLE);

        phoneInput.setVisibility(View.INVISIBLE);
        codeInput.setVisibility(View.INVISIBLE);

        codeSpinner.setVisibility(View.INVISIBLE);
    }

    protected Boolean isPhoneCorrect(){
        if(myPhoneNumber.length() < 9 || myPhoneNumber.length() > 10) {
            phoneInput.setError("Некорректный номер");
            phoneInput.requestFocus();
            return false;
        }

        return  true;
    }

    private void checkInputs() {
        // Проверяем поля ввода
        if(!phoneInput.getText().toString().isEmpty()) {
            // Поля непустые
            // Выводим сообщене
            Toast.makeText(
                    this,
                    "Вы ввели неправильные данные",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //получить номер телефона для проверки
    private String getUserPhone() {
        sPref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        return  sPref.getString(PHONE_NUMBER, "-");
    }

    //получить пароль для проверки
    private String getUserPass() {
        sPref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        return  sPref.getString(PASS, "-");
    }

    private void saveIdAndPass(String phone, String pass) {
        sPref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(PHONE_NUMBER, phone);
        editor.putString(PASS, pass);
        editor.apply();
    }

    private static String encryptThisStringSHA512(String input)
    {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void goToRegistration(String phone) {
        Intent intent = new Intent(this, Registration.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(PHONE_NUMBER,phone);
        startActivity(intent);
    }

    private void goToConfirmation() {
        Intent intent = new Intent(this, Confirmation.class);
        startActivity(intent);
        finish();
    }

    private void goToProfile(){
        Intent intent = new Intent(this, Profile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToRecoveryPassword() {
        Intent intent = new Intent(this, PasswordRecovery.class);
        startActivity(intent);
        finish();
    }

    private void attentionBadConnection() {
        Toast.makeText(this,"Плохое соединение",Toast.LENGTH_SHORT).show();
    }
}