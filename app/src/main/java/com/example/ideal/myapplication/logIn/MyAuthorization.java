package com.example.ideal.myapplication.logIn;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.ideal.myapplication.fragments.objects.Photo;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.DownloadServiceData;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.other.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MyAuthorization {
    private static final String PHONE = "phone";

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

    //PHOTOS
    private static final String PHOTOS = "photos";
    private static final String PHOTO_LINK = "photo link";
    private static final String OWNER_ID = "owner id";

    private DBHelper dbHelper;

    private long orderCounter;
    private Context context;
    private String myPhoneNumber;

    MyAuthorization(Context _context, String _myPhoneNumber) {
        context = _context;
        myPhoneNumber = _myPhoneNumber;

        dbHelper = new DBHelper(context);
    }

    void authorizeUser() {
        // скарываем Views и запукаем прогресс бар

        Query query = FirebaseDatabase.getInstance().getReference(USERS).
                orderByChild(PHONE).
                equalTo(myPhoneNumber);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                if (usersSnapshot.getChildrenCount() == 0) {
                    goToRegistration();
                } else {
                    // Получаем остальные данные о пользователе
                    DataSnapshot userSnapshot = usersSnapshot.getChildren().iterator().next();
                    Object name = userSnapshot.child(NAME).getValue();
                    if (name == null) {
                        // Имя в БД отсутствует, значит пользователь не до конца зарегистрировался
                        goToRegistration();
                    } else {
                        String city = String.valueOf(userSnapshot.child(CITY).getValue());

                        User user = new User();
                        user.setPhone(myPhoneNumber);
                        user.setName(String.valueOf(name));
                        user.setCity(city);
                        String userId = getUserId();
                        user.setId(userId);
                        // Очищаем LocalStorage
                        clearSQLite();

                        // Добавляем все данные о пользователе в SQLite
                        addUserInfoInLocalStorage(user);

                        // Добавляем подписки пользователя
                        loadUserSubscriptions();

                        //добавляем фото
                        loadPhotosByPhoneNumber(userId);

                        // Загружаем сервисы пользователя из FireBase
                        loadServiceByUserPhone(userSnapshot, user);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void loadUserSubscriptions() {
        Query query = FirebaseDatabase.getInstance().getReference("subscribers").
                orderByChild(USER_ID).
                equalTo(myPhoneNumber);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot subSnapshot : dataSnapshot.getChildren()) {
                    String id = subSnapshot.getKey();
                    String workerId = String.valueOf(subSnapshot.child("worker id").getValue());

                    loadUserById(workerId);

                    addUserSubscriptionInLocalStorage(id, workerId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void loadUserById(final String userId) {

        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(USERS).child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                // Получаем остальные данные о пользователе
                Object name = userSnapshot.child(NAME).getValue();
                if (name == null) {
                    // Имя в БД отсутствует, значит пользователь не до конца зарегистрировался
                    goToRegistration();
                } else {
                    String city = String.valueOf(userSnapshot.child(CITY).getValue());

                    User user = new User();
                    user.setPhone(userId);
                    user.setName(String.valueOf(name));
                    user.setCity(city);

                    loadPhotosByPhoneNumber(userId);

                    // Добавляем все данные о пользователе в SQLite
                    addUserInfoInLocalStorage(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void addUserSubscriptionInLocalStorage(String id, String workerId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, id);
        contentValues.put(DBHelper.KEY_USER_ID, myPhoneNumber);
        contentValues.put(DBHelper.KEY_WORKER_ID, workerId);

        database.insert(DBHelper.TABLE_SUBSCRIBERS, null, contentValues);
    }

    private void loadServiceByUserPhone(DataSnapshot usersSnapshot, User user) {
        SQLiteDatabase localDatabase = dbHelper.getWritableDatabase();
        String userId = user.getId();

        DownloadServiceData downloadServiceData = new DownloadServiceData(localDatabase, "Authorization");
        downloadServiceData.loadSchedule(usersSnapshot, userId);

        loadTimeByUserPhone();
    }

    //загружаю место куда я записан
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

    private void loadPhotosByPhoneNumber(final String userId) {

        final DatabaseReference photoRef = FirebaseDatabase.getInstance().getReference(USERS)
                .child(userId)
                .child(PHOTO_LINK);

        photoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot photosSnapshot) {
                String photoLink = String.valueOf(photosSnapshot.getValue());

                Photo photo = new Photo();

                photo.setPhotoId(userId);
                photo.setPhotoLink(photoLink);

                addPhotoInLocalStorage(photo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void addPhotoInLocalStorage(Photo photo) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
        contentValues.put(DBHelper.KEY_PHOTO_LINK_PHOTOS, photo.getPhotoLink());

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);
        boolean isUpdate = workWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_PHOTOS,
                        photo.getPhotoId());

        if(isUpdate){
            database.update(DBHelper.TABLE_PHOTOS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{photo.getPhotoId()});
        }
        else {
            contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
            database.insert(DBHelper.TABLE_PHOTOS, null, contentValues);
        }
    }

    // Обновляет информацию о текущем пользователе в SQLite
    private void addUserInfoInLocalStorage(User user) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Заполняем contentValues информацией о данном пользователе
        contentValues.put(DBHelper.KEY_NAME_USERS, user.getName());
        contentValues.put(DBHelper.KEY_CITY_USERS, user.getCity());
        contentValues.put(DBHelper.KEY_USER_ID, user.getPhone());
        contentValues.put(DBHelper.KEY_ID, user.getId());
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

        database.delete(DBHelper.TABLE_PHOTOS,null,null);
        database.delete(DBHelper.TABLE_SUBSCRIBERS,null,null);

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

    private void goToRegistration() {
        Intent intent = new Intent(context, Registration.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(PHONE, myPhoneNumber);
        context.startActivity(intent);
    }

    private void goToProfile() {

        Intent intent = new Intent(context, Profile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private void attentionBadConnection() {
        Toast.makeText(context,"Плохое соединение",Toast.LENGTH_SHORT).show();
    }

    private String getUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
