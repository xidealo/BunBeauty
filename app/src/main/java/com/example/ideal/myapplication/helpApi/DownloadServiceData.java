package com.example.ideal.myapplication.helpApi;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.foundElements.foundServiceElement;
import com.example.ideal.myapplication.fragments.foundElements.foundServiceProfileElement;
import com.example.ideal.myapplication.fragments.objects.Message;
import com.example.ideal.myapplication.fragments.objects.Photo;
import com.example.ideal.myapplication.fragments.objects.RatingReview;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DownloadServiceData {

    private static final String TAG = "DBInf";

    private static final String SERVICES = "services";
    private static final String SERVICE_ID = "service id";
    private static final String DESCRIPTION = "description";
    private static final String COST = "cost";
    private static final String USER_ID = "user id";

    private static final String TIME = "time";
    private static final String WORKING_DAYS = "working days";
    private static final String WORKING_TIME = "working time";
    private static final String WORKING_DAY_ID = "working day id";
    private static final String DATA = "data";

    private static final String REVIEWS = "reviews";
    private static final String REVIEW = "review";
    private static final String TYPE = "type";
    private static final String WORKING_TIME_ID = "working time id";
    private static final String RATING = "rating";
    private static final String MESSAGE_ID = "message id";
    private static final String REVIEW_FOR_SERVICE = "review for service";

    private static final String MESSAGES = "messages";
    private static final String MESSAGE_TIME = "message time";
    private static final String DIALOG_ID = "dialog id";

    private static final String CITY = "city";
    private static final String NAME = "name";
    private static final String USERS = "users";

    private static final String DIALOGS = "dialogs";
    private static final String FIRST_PHONE = "first phone";
    private static final String SECOND_PHONE = "second phone";

    //PHOTOS
    private static final String PHOTOS = "photos";
    private static final String PHOTO_LINK = "photo link";
    private static final String OWNER_ID = "owner id";

    private long currentCountOfDays;
    private WorkWithLocalStorageApi workWithLocalStorageApi;
    private SQLiteDatabase localDatabase;

    private String ownerId;
    private String status;
    private float sumRates;
    private long countOfRates;
    private long counter;
    private boolean addToScreen;
    private String serviceId;
    private Service service;
    private FragmentManager manager;

    public void loadSchedule(final String _serviceId, SQLiteDatabase _database,
                             String _status, FragmentManager _manager) {

        localDatabase = _database;
        serviceId = _serviceId;
        status = _status;

        manager = _manager;

        workWithLocalStorageApi = new WorkWithLocalStorageApi(localDatabase);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        service = new Service();


        //загружаем все сервисы в локалку
        DatabaseReference myRef = database
                .getReference(SERVICES)
                .child(serviceId);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot servicesSnapshot) {
                String serviceName = String.valueOf(servicesSnapshot.child(NAME).getValue());
                String serviceCost = String.valueOf(servicesSnapshot.child(COST).getValue());
                String serviceDescription = String.valueOf(servicesSnapshot.child(DESCRIPTION).getValue());
                String userId = String.valueOf(servicesSnapshot.child(USER_ID).getValue());

                service.setId(serviceId);
                service.setName(serviceName);
                service.setUserId(userId);
                service.setCost(serviceCost);
                service.setDescription(serviceDescription);

                updateServicesInLocalStorage(service);

                ownerId = userId;
                //загрузка фотографий для сервисов
                loadPhotosByServiceId(serviceId);
                loadPhotosByPhoneNumber(ownerId);

                //возвращает все дни определенного сервиса
                final Query query = database.getReference(WORKING_DAYS).
                        orderByChild(SERVICE_ID).
                        equalTo(serviceId);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot workingDaysSnapshot) {
                        final long countOfDays = workingDaysSnapshot.getChildrenCount();
                        currentCountOfDays = 0;
                        //если нет дней, убираем прогерсс бар и устанавливаем надпись, что нет оценок
                        if (countOfDays == 0) {
                            addToScreen(0);
                        }
                        //крутит цикл
                        for (DataSnapshot workingDay : workingDaysSnapshot.getChildren()) {
                            final String dayId = String.valueOf(workingDay.getKey());
                            String dayDate = String.valueOf(workingDay.child(DATA).getValue());
                            addScheduleInLocalStorage(dayId, dayDate, serviceId);

                            final Query queryTime = database.getReference(WORKING_TIME).
                                    orderByChild(WORKING_DAY_ID).
                                    equalTo(dayId);
                            //этот в цикле 2
                            queryTime.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot workingTimesSnapshot) {
                                    //если нет времени
                                    if (workingTimesSnapshot.getChildrenCount() == 0) {
                                        addToScreen(0);
                                    }

                                    for (DataSnapshot workingTimeSnapshot : workingTimesSnapshot.getChildren()) {
                                        String timeId = String.valueOf(workingTimeSnapshot.getKey());
                                        String time = String.valueOf(workingTimeSnapshot.child(TIME).getValue());
                                        String timeUserId = String.valueOf(workingTimeSnapshot.child(USER_ID).getValue());
                                        String timeWorkingDayId = String.valueOf(workingTimeSnapshot.child(WORKING_DAY_ID).getValue());
                                        addTimeInLocalStorage(timeId, time, timeUserId, timeWorkingDayId);
                                    }
                                    currentCountOfDays++;
                                    if (currentCountOfDays == countOfDays) {
                                        loadRating();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    
    private void loadRating() {
        //зашружаю среднюю оценку, складываю все и делю их на количество
        // также усталавниваю количество оценок
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //получаю все id working time заданного сервиса
        String sqlQuery =
                "SELECT "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + ", "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_USER_ID
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " = ? "
                        + " AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " = "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                        + " = "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID;

        final Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{serviceId});

        if (cursor.moveToFirst()) {
            do {
                int indexWorkingTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
                int indexUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
                final String workingTimeId = cursor.getString(indexWorkingTimeId);
                final String userId = cursor.getString(indexUserId);

                Query query = database.getReference(REVIEWS)
                        .orderByChild(WORKING_TIME_ID)
                        .equalTo(workingTimeId);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot reviewsSnapshot) {
                        //проверить обоюдное это ревью, если нет, то проверить на 72 часа
                        if(isMutualReview(reviewsSnapshot)) {
                          addReview(reviewsSnapshot,workingTimeId,userId);
                        }
                        else{
                            //проверка на время, если у timeId время с записи больше 72, то в любом случае добавляем в локалку
                            if(isAfterWeek(workingTimeId)){
                                addReview(reviewsSnapshot,workingTimeId,userId);
                            }
                        }

                        counter++;
                        if (counter == cursor.getCount() && (countOfRates == 0)) {
                            addToScreen(0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void addReview(DataSnapshot reviewsSnapshot, String workingTimeId,String userId){

        RatingReview ratingReview = new RatingReview();

        for (DataSnapshot reviewSnapshot : reviewsSnapshot.getChildren()) {
            String type = String.valueOf(reviewSnapshot.child(TYPE).getValue());
            String rating = String.valueOf(reviewSnapshot.child(RATING).getValue());
            if ((!rating.equals("0"))) {

                // считаем только ревью для сервисов
                if(type.equals(REVIEW_FOR_SERVICE)) {
                    countOfRates++;
                    sumRates += Float.valueOf(String.valueOf(reviewSnapshot.child(RATING).getValue()));
                }

                String messageId = String.valueOf(reviewSnapshot.child(MESSAGE_ID).getValue());

                ratingReview.setId(String.valueOf(reviewSnapshot.getKey()));
                ratingReview.setReview(String.valueOf(reviewSnapshot.child(REVIEW).getValue()));
                ratingReview.setRating(rating);
                ratingReview.setMessageId(messageId);
                ratingReview.setType(type);
                ratingReview.setWorkingTimeId(workingTimeId);
                //добавление ревью в локальную бд
                addReviewForServiceInLocalStorage(ratingReview);
                // загружать инфу о пользователе
                if (userId.equals("0")) {
                    loadMessageById(messageId);
                } else {
                    loadUserForThisReview(userId);
                }
            }
        }

    }

    private void addScheduleInLocalStorage(String dayId, String dayDate, String serviceId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_DATE_WORKING_DAYS, dayDate);
        contentValues.put(DBHelper.KEY_SERVICE_ID_WORKING_DAYS, serviceId);

        boolean isUpdate = workWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_WORKING_DAYS, dayId);

        if (isUpdate) {
            localDatabase.update(DBHelper.TABLE_WORKING_DAYS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{dayId});
        } else {
            contentValues.put(DBHelper.KEY_ID, dayId);
            localDatabase.insert(DBHelper.TABLE_WORKING_DAYS, null, contentValues);
        }
    }

    private void addTimeInLocalStorage(String timeId, String timeDate,
                                       String timeUserId, String timeWorkingDayId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_TIME_WORKING_TIME, timeDate);
        contentValues.put(DBHelper.KEY_USER_ID, timeUserId);
        contentValues.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, timeWorkingDayId);

        boolean isUpdate = workWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_WORKING_TIME, timeId);

        if (isUpdate) {
            localDatabase.update(DBHelper.TABLE_WORKING_TIME, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{timeId});
        } else {
            contentValues.put(DBHelper.KEY_ID, timeId);
            localDatabase.insert(DBHelper.TABLE_WORKING_TIME, null, contentValues);
        }
    }

    private void addReviewForServiceInLocalStorage(RatingReview ratingReview) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_REVIEW_REVIEWS, ratingReview.getReview());
        contentValues.put(DBHelper.KEY_RATING_REVIEWS, ratingReview.getRating());
        contentValues.put(DBHelper.KEY_TYPE_REVIEWS, ratingReview.getType());
        contentValues.put(DBHelper.KEY_WORKING_TIME_ID_REVIEWS, ratingReview.getWorkingTimeId());
        contentValues.put(DBHelper.KEY_MESSAGE_ID_REVIEWS, ratingReview.getMessageId());

        //для проверки на update || insert в таблицу
        boolean isUpdate =  workWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_REVIEWS,
                        ratingReview.getId());
        if(isUpdate){
            localDatabase.update(DBHelper.TABLE_REVIEWS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(ratingReview.getId())});
        }
        else {
            contentValues.put(DBHelper.KEY_ID, ratingReview.getId());
            localDatabase.insert(DBHelper.TABLE_REVIEWS, null, contentValues);
        }
    }

    private void loadMessageById(final String messageId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference messageRef = database.getReference(MESSAGES).child(messageId);
        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot messageSnapshot) {
                String messageTime = String.valueOf(messageSnapshot.child(MESSAGE_TIME).getValue());
                String dialogId = String.valueOf(messageSnapshot.child(DIALOG_ID).getValue());

                Message message = new Message();

                message.setId(messageId);
                message.setMessageTime(messageTime);
                message.setDialogId(dialogId);
                addMessageInLocalStorage(message);

                loadDialogById(dialogId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadUserForThisReview(final String valuingPhone) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS)
                .child(valuingPhone);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot user) {
                User localUser = new User();
                localUser.setPhone(valuingPhone);
                //загрузка фото людей с оценками
                localUser.setName(String.valueOf(user.child(NAME).getValue()));
                localUser.setCity(String.valueOf(user.child(CITY).getValue()));
                addUserInLocalStorage(localUser);
                if(!addToScreen) {
                    Log.d(TAG, "FOUR");
                    addToScreen(1);
                    addToScreen = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void loadOwnerAndAddToScreen(final float avgRating, final String ownerId) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS)
                .child(ownerId);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot user) {
                User localUser = new User();
                localUser.setPhone(ownerId);
                localUser.setName(String.valueOf(user.child(NAME).getValue()));
                localUser.setCity(String.valueOf(user.child(CITY).getValue()));
                addUserInLocalStorage(localUser);

                //подгружаем владельца сервиса и отображаем его на фрагменте
                addToScreenOnMainScreen(avgRating, service, localUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void addMessageInLocalStorage(Message message) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_MESSAGE_TIME_MESSAGES, message.getMessageTime());
        contentValues.put(DBHelper.KEY_DIALOG_ID_MESSAGES, message.getDialogId());

        //для проверки на update || insert в таблицу
        boolean isUpdate =  workWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_MESSAGES, message.getId());
        if(isUpdate){
            localDatabase.update(DBHelper.TABLE_MESSAGES, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(message.getId())});
        }
        else {
            contentValues.put(DBHelper.KEY_ID, message.getId());
            localDatabase.insert(DBHelper.TABLE_MESSAGES, null, contentValues);
        }
    }

    private void loadDialogById(final String dialogId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference messageRef = database.getReference(DIALOGS).child(dialogId);
        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dialogSnapshot) {
                String firstPhone = String.valueOf(dialogSnapshot.child(FIRST_PHONE).getValue());
                String secondPhone = String.valueOf(dialogSnapshot.child(SECOND_PHONE).getValue());

                addDialogInLocalStorage(dialogId, firstPhone, secondPhone);
                if(!firstPhone.equals(ownerId)) {
                    loadUserForThisReview(firstPhone);
                }

                if(!secondPhone.equals(ownerId) ) {
                    loadUserForThisReview(firstPhone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void addUserInLocalStorage(User localUser) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_NAME_USERS,localUser.getName());
        contentValues.put(DBHelper.KEY_CITY_USERS,localUser.getCity());

        boolean isUpdate = workWithLocalStorageApi
                .hasSomeDataForUsers(DBHelper.TABLE_CONTACTS_USERS,
                        localUser.getPhone());

        if (isUpdate) {
            localDatabase.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                    DBHelper.KEY_USER_ID + " = ?",
                    new String[]{String.valueOf(localUser.getPhone())});
        } else {
            contentValues.put(DBHelper.KEY_USER_ID, localUser.getPhone());
            localDatabase.insert(DBHelper.TABLE_CONTACTS_USERS, null, contentValues);
        }

    }

    private void addDialogInLocalStorage(String dialogId, String firstPhone, String secondPhone) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_FIRST_USER_ID_DIALOGS, firstPhone);
        contentValues.put(DBHelper.KEY_SECOND_USER_ID_DIALOGS, secondPhone);

        //для проверки на update || insert в таблицу
        boolean isUpdate =  workWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_DIALOGS, dialogId);
        if(isUpdate){
            localDatabase.update(DBHelper.TABLE_DIALOGS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(dialogId)});
        }
        else {
            contentValues.put(DBHelper.KEY_ID, dialogId);
            localDatabase.insert(DBHelper.TABLE_DIALOGS, null, contentValues);
        }
    }

    private void updateServicesInLocalStorage(Service service) {

        ContentValues contentValues = new ContentValues();
        // Заполняем contentValues информацией о данном сервисе
        contentValues.put(DBHelper.KEY_NAME_SERVICES, service.getName());
        contentValues.put(DBHelper.KEY_USER_ID, service.getUserId());
        contentValues.put(DBHelper.KEY_DESCRIPTION_SERVICES, service.getDescription());
        contentValues.put(DBHelper.KEY_MIN_COST_SERVICES, service.getCost());

        boolean isUpdate =  workWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_CONTACTS_SERVICES, serviceId);

        // Проверка есть ли такой сервис в SQLite
        if(isUpdate) {
            // Данный сервис уже есть
            // Обновляем информацию о нём
            localDatabase.update(
                    DBHelper.TABLE_CONTACTS_SERVICES,
                    contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{serviceId});
        } else {
            // Данного сервиса нет
            // Добавляем serviceId в contentValues
            contentValues.put(DBHelper.KEY_ID, serviceId);
            // Добавляем данный сервис в SQLite
            localDatabase.insert(DBHelper.TABLE_CONTACTS_SERVICES, null, contentValues);
        }
    }
    private void loadPhotosByServiceId(String serviceId) {

        Query photosQuery = FirebaseDatabase.getInstance().getReference(PHOTOS)
                .orderByChild(OWNER_ID)
                .equalTo(serviceId);

        photosQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot photosSnapshot) {
                for(DataSnapshot fPhoto: photosSnapshot.getChildren()){

                    Photo photo = new Photo();

                    photo.setPhotoId(fPhoto.getKey());
                    photo.setPhotoLink(String.valueOf(fPhoto.child(PHOTO_LINK).getValue()));
                    photo.setPhotoOwnerId(String.valueOf(fPhoto.child(OWNER_ID).getValue()));

                    addPhotoInLocalStorage(photo);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadPhotosByPhoneNumber(String myPhoneNumber) {

        final Query photosQuery = FirebaseDatabase.getInstance().getReference(PHOTOS)
                .orderByChild(OWNER_ID)
                .equalTo(myPhoneNumber);

        photosQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot photosSnapshot) {
                for(DataSnapshot fPhoto: photosSnapshot.getChildren()){

                    Photo photo = new Photo();

                    photo.setPhotoId(fPhoto.getKey());
                    photo.setPhotoLink(String.valueOf(fPhoto.child(PHOTO_LINK).getValue()));
                    photo.setPhotoOwnerId(String.valueOf(fPhoto.child(OWNER_ID).getValue()));

                    addPhotoInLocalStorage(photo);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addPhotoInLocalStorage(Photo photo) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
        contentValues.put(DBHelper.KEY_PHOTO_LINK_PHOTOS, photo.getPhotoLink());
        contentValues.put(DBHelper.KEY_OWNER_ID_PHOTOS,photo.getPhotoOwnerId());

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(localDatabase);
        boolean isUpdate = workWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_PHOTOS,
                        photo.getPhotoId());

        if(isUpdate){
            localDatabase.update(DBHelper.TABLE_PHOTOS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{photo.getPhotoId()});
        }
        else {
            contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
            localDatabase.insert(DBHelper.TABLE_PHOTOS, null, contentValues);
        }
    }

    private void addToScreenOnMainScreen(float avgRating, Service service, User user) {

        if(avgRating!=0) {
            avgRating = sumRates / countOfRates;
        }

        foundServiceElement fElement = new foundServiceElement(avgRating, service, user);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.resultsMainScreenLayout, fElement);
        transaction.commit();
    }

    private void addToScreenOnProfile(float avgRating) {

        if(avgRating!=0) {
            avgRating = sumRates / countOfRates;
        }

        foundServiceProfileElement fElement
                = new foundServiceProfileElement(avgRating,service);
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.servicesProfileLayout, fElement);

        transaction.commit();
    }

    //время полученное по timeId больше 3 дней
    private boolean isAfterWeek(String workingTimeId) {

        String date  = workWithLocalStorageApi.getDate(workingTimeId);
        WorkWithTimeApi workWithTimeApi = new WorkWithTimeApi();
        long dateMilliseconds = workWithTimeApi.getMillisecondsStringDate(date);
        boolean isAfterWeek = (workWithTimeApi.getSysdateLong() - dateMilliseconds) > 604800000;

        return isAfterWeek;
    }

    private  void addToScreen(float avgRating){

        if(!addToScreen) {
            addToScreen = true;
            switch (status){
                case "MainScreen":
                    //подгружаем владельца сервиса и отображаем его на фрагменте
                    loadOwnerAndAddToScreen(avgRating,ownerId);
                    break;
                case "Profile":
                    addToScreenOnProfile(avgRating);
                    break;
            }
        }
    }

    //ревью оставили 2 человека?
    private boolean isMutualReview(DataSnapshot reviewsSnapshot) {
        if(reviewsSnapshot.getChildrenCount()==0){
            return false;
        }
        for (DataSnapshot reviewSnapshot : reviewsSnapshot.getChildren()) {
            String rating = String.valueOf(reviewSnapshot.child(RATING).getValue());
            //если хоть 1 оценка 0, то возвращаем false
            if (rating.equals("0")) {
                return false;
            }
        }
        return true;
    }
}

