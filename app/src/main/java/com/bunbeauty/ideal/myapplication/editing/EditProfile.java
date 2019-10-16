package com.bunbeauty.ideal.myapplication.editing;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Photo;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User;
import com.bunbeauty.ideal.myapplication.helpApi.ListeningManager;
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.AuthorizationActivity;
import com.bunbeauty.ideal.myapplication.logIn.CountryCodes;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper;
import com.bunbeauty.ideal.myapplication.other.MyService;
import com.bunbeauty.ideal.myapplication.reviews.Comments;
import com.bunbeauty.ideal.myapplication.searchService.GuestService;
import com.bunbeauty.ideal.myapplication.subscriptions.Subscribers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "DBInf";

    private static final String USERS = "users";
    private static final String USER_NAME = "name";
    private static final String USER_CITY = "city";
    private static final String PHONE = "phone";

    private static final String AVATAR = "avatar";
    private static final String PHOTO_LINK = "photo link";

    private static final String TOKEN = "token";

    private final int PICK_IMAGE_REQUEST = 71;

    private String oldPhone;
    private String phone;
    private Uri lastFilePath;

    private Button editBtn;
    private Button verifyButton;
    private Button resendButton;

    private EditText nameInput;
    private EditText surnameInput;
    private Spinner citySpinner;
    private EditText phoneInput;
    private EditText codeInput;
    //private ProgressBar progressBar;
    private Spinner codeSpinner;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private DBHelper dbHelper;
    private User user;

    private ImageView avatarImage;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        nameInput = findViewById(R.id.nameEditProfileInput);
        surnameInput = findViewById(R.id.surnameEditProfileInput);
        citySpinner = findViewById(R.id.citySpinnerEditProfileSpinner);
        phoneInput = findViewById(R.id.phoneEditProfileInput);
        codeInput = findViewById(R.id.codeEditProfileInput);
        Button logOutBtn = findViewById(R.id.logOutEditProfileBtn);

        editBtn = findViewById(R.id.editProfileEditProfileBtn);
        resendButton = findViewById(R.id.resendCodeEditProfileBtn);
        verifyButton = findViewById(R.id.verifyCodeEditProfileBtn);

        //progressBar = findViewById(R.id.progressBarEditProfile);

        //для работы с картинкой
        avatarImage = findViewById(R.id.avatarEditProfileImage);

        codeSpinner = findViewById(R.id.codeEditProfileSpinner);

        user = new User();
        dbHelper = new DBHelper(this);
        userId = getUserId();
        WorkWithLocalStorageApi localStorageApi = new WorkWithLocalStorageApi(dbHelper.getReadableDatabase());
        int width = getResources().getDimensionPixelSize(R.dimen.photo_width);
        int height = getResources().getDimensionPixelSize(R.dimen.photo_height);
        localStorageApi.setPhotoAvatar(userId,avatarImage,width,height);

        oldPhone = getUserPhone();

        setInformation();

        editBtn.setOnClickListener(this);
        resendButton.setOnClickListener(this);
        verifyButton.setOnClickListener(this);
        avatarImage.setOnClickListener(this);
        logOutBtn.setOnClickListener(this);
    }

    private  void setInformation(){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sqlQuery = "SELECT "
                + DBHelper.KEY_NAME_USERS + ", "
                + DBHelper.KEY_CITY_USERS + ", "
                + DBHelper.KEY_PHONE_USERS
                + " FROM " + DBHelper.TABLE_CONTACTS_USERS
                + " WHERE " + DBHelper.KEY_PHONE_USERS + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{oldPhone});

        if (cursor.moveToFirst()) {
            int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_USERS);
            int indexCity = cursor.getColumnIndex(DBHelper.KEY_CITY_USERS);
            int indexPhone = cursor.getColumnIndex(DBHelper.KEY_PHONE_USERS);

            String[] nickName = cursor.getString(indexName).split(" ");
            nameInput.setText(WorkWithStringsApi.firstCapitalSymbol(nickName[0]));
            surnameInput.setText(WorkWithStringsApi.firstCapitalSymbol(nickName[1]));
            //set city in spinner
            ArrayList<String> citiesArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.choice_cites)));
            citySpinner.setSelection(citiesArray.indexOf(WorkWithStringsApi.firstCapitalSymbol(cursor.getString(indexCity))));

            String phone = cursor.getString(indexPhone);
            // не универсальная обрезка кода (возможно стоит хранить отдельно код)
            phoneInput.setText(phone.substring(2));
            codeSpinner.setSelection(Arrays.asList(CountryCodes.codes).indexOf(phone.substring(0, 2)));
            // Arrays.asList(CountryCodes.codes).indexOf(phone.substring(0, 2))
        }
        cursor.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.editProfileEditProfileBtn:
                changeProfileData();
                break;

            case R.id.verifyCodeEditProfileBtn:

                String code = codeInput.getText().toString().trim();
                if (code.length() >= 6) {
                    // подтверждаем код и если все хорошо, создаем юзера
                    verifyCode(code);
                }
                break;

            case R.id.avatarEditProfileImage:
                chooseImage();
                break;

            case R.id.logOutEditProfileBtn:
                goToLogIn();
                break;

            case R.id.resendCodeEditProfileBtn:
                resendCode();
                break;

            default:
                break;
        }
    }

    private void changeProfileData() {
        if (areInputsCorrect()) {
            String newPhone = CountryCodes.codes[codeSpinner.getSelectedItemPosition()]
                    + phoneInput.getText().toString().trim();
            user.setName((nameInput.getText().toString() + " " + surnameInput.getText().toString()).toLowerCase());
            user.setCity(citySpinner.getSelectedItem().toString().toLowerCase());
            if (oldPhone.equals(newPhone)) {
                saveChanges();
            } else {
                if (newPhone.length() >= 11) {
                    phone = newPhone;

                    Query userQuery = FirebaseDatabase
                            .getInstance()
                            .getReference(USERS)
                            .orderByChild(PHONE)
                            .equalTo(newPhone);

                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            //такого номера нет
                            if (userSnapshot.getValue() == null) {
                                sendVerificationCode();
                            } else {
                                attentionThisUserAlreadyReg();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            attentionBadConnection();
                        }
                    });
                } else {
                    phoneInput.setError("Некорекный номер телефона");
                    phoneInput.requestFocus();
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager manager = getSupportFragmentManager();
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerEditProfileLayout);
        panelBuilder.buildHeader(manager, "Редактирование профиля", R.id.headerEditProfileLayout);
    }

    private Boolean areInputsCorrect() {
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

        String city = citySpinner.getSelectedItem().toString();
        if (city.equals("Выбрать город")) {
            assertNoSelectedCity();
            return false;
        }

        return  true;
    }

    private void saveChanges() {
        editBtn.setVisibility(View.GONE);
        codeInput.setVisibility(View.GONE);
        resendButton.setVisibility(View.GONE);
        verifyButton.setVisibility(View.GONE);
        //progressBar.setVisibility(View.VISIBLE);

        DatabaseReference reference = FirebaseDatabase
                .getInstance()
                .getReference(USERS)
                .child(userId);

        Map<String, Object> items = new HashMap<>();

        items.put(USER_NAME, user.getName());
        items.put(USER_CITY, user.getCity());
        if (phone != null) {
            items.put(PHONE, phone);
        }
        reference.updateChildren(items);

        if(lastFilePath!=null) {
            uploadImage(lastFilePath);
            updateInfoInLocalStorage();
        }
        else {
            updateInfoInLocalStorage();
            goToProfile();
        }


    }

    private void sendVerificationCode(){
        setUpVerificationCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
    }

    public void verifyCode(String code) {
        //получаем ответ гугл
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);
        //заходим с айфоном и токеном
        updatePhone(credential);
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


    private void updatePhone(PhoneAuthCredential credential) {

        FirebaseAuth.getInstance().getCurrentUser().updatePhoneNumber(credential)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //если введенный код совпадает с присланным кодом
                        if (task.isSuccessful()) {
                            saveChanges();
                            goToProfile();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                attentionThisCodeWasWrong();
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    //Обновление информации в БД
    private void updateInfoInLocalStorage() {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_NAME_USERS, user.getName());
        contentValues.put(DBHelper.KEY_CITY_USERS, user.getCity());
        if (phone != null) {
            contentValues.put(DBHelper.KEY_CITY_USERS, phone);
        }

        database.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{userId});
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
        return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    }



    private void chooseImage() {
        //Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK:
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        //Тип получаемых объектов - image:
        photoPickerIntent.setType("image/*");
        //Запускаем переход с ожиданием обратного результата в виде информации об изображении:
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            Uri filePath = data.getData();
            try {
                //установка картинки на activity
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                avatarImage.setImageBitmap(bitmap);
                lastFilePath = filePath;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(Uri filePath) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        if(filePath != null)
        {
            final String photoId = userId;
            final StorageReference storageReference = firebaseStorage.getReference(AVATAR + "/" + photoId);
            storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            updatePhotos(uri.toString(),photoId);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) { }
            });

        }
    }

    private void updatePhotos(String storageReference, String photoId) {
        // проверяем нет ли такого телефона в FB, если есть то перезаписываем только ссылку
        final FirebaseDatabase database =  FirebaseDatabase.getInstance();

        DatabaseReference photoRef = database.getReference(USERS)
                .child(userId)
                .child(PHOTO_LINK);

        photoRef.setValue(storageReference);

        Photo photo = new Photo();
        photo.setPhotoId(photoId);
        photo.setPhotoLink(storageReference);
        photo.setPhotoOwnerId(userId);

        addPhotoInLocalStorage(photo);
    }

    private void addPhotoInLocalStorage(Photo photo) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String ownerId = photo.getPhotoOwnerId();

        database.delete(
                DBHelper.TABLE_PHOTOS,
                DBHelper.KEY_OWNER_ID_PHOTOS + " = ?",
                new String[]{ownerId});

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
        contentValues.put(DBHelper.KEY_PHOTO_LINK_PHOTOS, photo.getPhotoLink());

        if(hasSomePhoto()) {
            database.update(DBHelper.TABLE_PHOTOS, contentValues,
                    DBHelper.KEY_OWNER_ID_PHOTOS + " = ?",
                    new String[]{ownerId});
        }
        else {
            contentValues.put(DBHelper.KEY_OWNER_ID_PHOTOS, ownerId);
            database.insert(DBHelper.TABLE_PHOTOS, null, contentValues);
        }
        goToProfile();
    }

    private boolean hasSomePhoto() {

        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_PHOTOS
                + " WHERE "
                + DBHelper.KEY_OWNER_ID_PHOTOS + " = ?";

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sqlQuery, new String[]{userId});

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    private  String getUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void goToProfile() {
        super.onBackPressed();
    }

    private void attentionBadConnection() {
        Toast.makeText(this,"Плохое соединение",Toast.LENGTH_SHORT).show();
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

    private void goToLogIn() {
        FirebaseAuth.getInstance().signOut();

        ListeningManager.removeAllListeners();
        stopService(new Intent(this, MyService.class));
        clearNotificationToken();
        clearStaticData();
        //ListeningManager.removeAllListeners();

        Intent intent = new Intent(this, AuthorizationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void clearStaticData() {
        GuestService.serviceIdsFirstSetGS.clear();
        Comments.serviceIdsFirstSetComments.clear();
        Comments.userIdsFirstSetComments.clear();
        Subscribers.isFirst = true;
    }

    // Стираем токен, чтобы на устройство не приходили оповещения для данного пользователя
    private void clearNotificationToken() {
        DatabaseReference tokenRef = FirebaseDatabase
                .getInstance()
                .getReference(USERS)
                .child(userId)
                .child(TOKEN);
        tokenRef.setValue("-");
    }

    /*private void stopAllThreads() {
        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        for (Thread trd : threads) {
            if (trd.isAlive()) {
                trd.interrupt();
            }
        }
    }*/

    private void assertNoSelectedCity() {
        Toast.makeText(this, "Выберите город", Toast.LENGTH_SHORT).show();
    }

    private void attentionThisCodeWasWrong(){
        Toast.makeText(
                this,
                "Код введен неверно",
                Toast.LENGTH_SHORT).show();
    }
}