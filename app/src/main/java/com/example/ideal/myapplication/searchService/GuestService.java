package com.example.ideal.myapplication.searchService;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.createService.MyCalendar;
import com.example.ideal.myapplication.fragments.PremiumElement;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.helpApi.LoadingGuestServiceData;
import com.example.ideal.myapplication.helpApi.LoadingUserElementData;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.other.IPremium;
import com.example.ideal.myapplication.reviews.Comments;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GuestService extends AppCompatActivity implements View.OnClickListener, IPremium {

    private static final String TAG = "DBInf";

    private static final String WORKER = "worker";
    private static final String USER = "user";
    private static final String SERVICE_ID = "service id";
    private static final String TYPE = "type";
    private static final String SERVICES = "services";
    private static final String IS_PREMIUM = "is premium";
    private static final String SERVICE_OWNER_ID = "service owner id";
    private static final String USERS = "users";
    private static final String CODES = "codes";
    private static final String CODE = "code";
    private static final String WORKING_DAYS = "working days";
    private static final String WORKING_TIME = "working time";
    private static final String DATE = "date";
    private static final String ORDERS = "orders";
    private static final String COUNT_OF_RATES = "count of rates";
    private static final String COUNT = "count";
    private static final String DESCRIPTION = "description";
    private static final String COST = "cost";
    private static final String USER_ID = "user id";
    private static final String TIME = "time";
    private static final String CATEGORY = "category";
    private static final String AVG_RATING = "avg rating";
    private static final String ADDRESS = "address";
    private static final String NAME = "name";
    private static final String IS_CANCELED = "is canceled";

    private static final String STATUS_USER_BY_SERVICE = "status UserCreateService";

    private static final String REVIEW_FOR_SERVICE = "review for service";

    private String status;

    private String userId;
    private String serviceId;
    private String ownerId;
    private String serviceName;
    private String countOfRatesForComments;

    private TextView costText;
    private TextView addressText;
    private TextView withoutRatingText;
    private TextView descriptionText;
    private TextView countOfRatesText;
    private TextView avgRatesText;
    private TextView premiumText;
    private TextView noPremiumText;

    private RatingBar ratingBar;
    private LinearLayout ratingLL;
    private LinearLayout imageFeedLayout;
    private LinearLayout premiumLayout;
    private LinearLayout topPanelLayout;
    private boolean isMyService;
    private boolean isPremiumLayoutSelected;
    private DBHelper dbHelper;
    private static ArrayList<String> serviceIdsFirstSetGS = new ArrayList<>();
    private SQLiteDatabase database;
    private ProgressBar progressBar;
    private ScrollView mainScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_service);

        init();

        if (!serviceIdsFirstSetGS.contains(serviceId)) {
            loadServiceData();
            serviceIdsFirstSetGS.add(serviceId);
        } else {
            //получаем данные о сервисе
            getInfoAboutService(serviceId);
        }
    }

    private void init() {
        FragmentManager manager = getSupportFragmentManager();

        costText = findViewById(R.id.costGuestServiceText);
        addressText = findViewById(R.id.addressGuestServiceText);
        descriptionText = findViewById(R.id.descriptionGuestServiceText);
        withoutRatingText = findViewById(R.id.withoutRatingText);
        ratingBar = findViewById(R.id.ratingBarGuestServiceRatingBar);
        countOfRatesText = findViewById(R.id.countOfRatesGuestServiceElementText);
        avgRatesText = findViewById(R.id.avgRatingGuestServiceElementText);
        progressBar = findViewById(R.id.progressBarGuestService);
        mainScroll = findViewById(R.id.guestServiceMainScroll);

        premiumText = findViewById(R.id.yesPremiumGuestServiceText);
        noPremiumText = findViewById(R.id.noPremiumGuestServiceText);

        Button editScheduleBtn = findViewById(R.id.editScheduleGuestServiceBtn);
        ratingLL = findViewById(R.id.ratingGuestServiceLayout);
        premiumLayout = findViewById(R.id.premiumGuestServiceLayout);
        imageFeedLayout = findViewById(R.id.feedGuestServiceLayout);
        LinearLayout premiumIconLayout = findViewById(R.id.premiumIconGuestServiceLayout);

        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
        topPanelLayout = findViewById(R.id.headerGuestServiceLayout);
        userId = getUserId();

        serviceId = getIntent().getStringExtra(SERVICE_ID);
        ownerId = getOwnerId();
        // мой сервис или нет?
        isMyService = userId.equals(ownerId);

        //убрана панель премиума
        isPremiumLayoutSelected = false;
        if (isMyService) {
            status = WORKER;
            editScheduleBtn.setText("Редактировать расписание");
            premiumText.setOnClickListener(this);
            noPremiumText.setOnClickListener(this);
            premiumText.setOnClickListener(this);

            PremiumElement premiumElement = new PremiumElement();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.premiumGuestServiceLayout, premiumElement);
            transaction.commit();
        } else {
            status = USER;
            editScheduleBtn.setText("Расписание");
            premiumIconLayout.setVisibility(View.GONE);
        }
        editScheduleBtn.setOnClickListener(this);
    }

    private void loadServiceData() {
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = firebaseDatabase.getReference(USERS)
                .child(ownerId);
        //загружаем один раз всю информацию
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                //подгрузка фото
                LoadingUserElementData.loadPhotos(userSnapshot, database);

                //подгрузка сервиса
                final DataSnapshot serviceSnapshot = userSnapshot
                        .child(SERVICES)
                        .child(serviceId);

                LoadingGuestServiceData.addServiceInfoInLocalStorage(serviceSnapshot, database);

                Service service = new Service();
                service.setCost(serviceSnapshot.child(COST).getValue(String.class));
                service.setAddress(serviceSnapshot.child(ADDRESS).getValue(String.class));
                service.setDescription(serviceSnapshot.child(DESCRIPTION).getValue(String.class));
                service.setName(serviceSnapshot.child(NAME).getValue(String.class));
                service.setAverageRating(serviceSnapshot.child(AVG_RATING).getValue(Float.class));
                service.setCountOfRates(serviceSnapshot.child(COUNT_OF_RATES).getValue(Long.class));
                service.setIsPremium(checkPremium(serviceSnapshot.child(IS_PREMIUM).getValue(String.class)));

                setGuestService(service);

                final DatabaseReference workingDaysRef = myRef
                        .child(SERVICES)
                        .child(serviceId)
                        .child(WORKING_DAYS);

                workingDaysRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot workingDaySnapshot, @Nullable String s) {
                        final String workingDayId = workingDaySnapshot.getKey();
                        long sysdateLong = WorkWithTimeApi.getSysdateLong();
                        long dateLong = WorkWithTimeApi.getMillisecondsStringDateYMD(workingDaySnapshot.child(DATE).getValue(String.class));
                        if (dateLong > sysdateLong) {
                            LoadingGuestServiceData.addWorkingDaysInLocalStorage(workingDaySnapshot, serviceId, database);

                            final DatabaseReference workingTimesRef = workingDaysRef
                                    .child(workingDayId)
                                    .child(WORKING_TIME);

                            workingTimesRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot timeSnapshot, @Nullable String s) {
                                    //при добавлении нового времени
                                    LoadingGuestServiceData.addTimeInLocalStorage(timeSnapshot, workingDayId, database);
                                    final String timeId = timeSnapshot.getKey();
                                    DatabaseReference ordersRef = workingTimesRef
                                            .child(timeId)
                                            .child(ORDERS);
                                    ordersRef.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {
                                            LoadingGuestServiceData.addOrderInLocalStorage(orderSnapshot,timeId, database);
                                            // если кто-то записался
                                        }

                                        @Override
                                        public void onChildChanged(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {
                                            //если от кого-то отказались
                                            LoadingGuestServiceData.addOrderInLocalStorage(orderSnapshot,timeId, database);
                                        }

                                        @Override
                                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                            //void

                                        }

                                        @Override
                                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                            //void
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            //void
                                        }
                                    });

                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    //пусто
                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot timeSnapshot) {
                                    //при удалении времени
                                    LoadingGuestServiceData.deleteTimeFromLocalStorage(timeSnapshot.getKey(), database);
                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    //пусто
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        //пустое
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        //пустое
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        //пустое
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //пустое
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //слушатель на данные сервиса
        final DatabaseReference serviceRef = firebaseDatabase.getReference(USERS)
                .child(ownerId)
                .child(SERVICES)
                .child(serviceId);
        serviceRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot serviceSnapshot, @Nullable String s) {
                //слушатель на каждый день, чтобы отслеживать изменение времени
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot serviceSnapshot, @Nullable String s) {
                //если что-то меняется, мы сохраняем даные
                LoadingGuestServiceData.addServiceInfoInLocalStorage(serviceSnapshot, database);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static Boolean checkPremium(String premiumDate) {
        long premDate = WorkWithTimeApi.getMillisecondsStringDateWithSeconds(premiumDate);
        long sysDate = WorkWithTimeApi.getSysdateLong();

        Log.d(TAG, "checkPremium: " + premDate);
        if (sysDate > premDate + 60*60*24*7*1000) {
            // время вышло
            return false;
        } else {
            // премиумный период
            return true;
        }
    }

    private String getOwnerId() {
        String sqlQuery =
                "SELECT *"
                        + " FROM " + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " = ? ";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});
        if (cursor.moveToFirst()) {
            int indexUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
            ownerId = cursor.getString(indexUserId);
        }
        return ownerId;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editScheduleGuestServiceBtn:
                if (status.equals(WORKER)) {
                    // если мой сервис, я - воркер
                    // сразу идём редактировать расписание
                    goToMyCalendar(WORKER);
                } else {
                    // если не мой сервис, я - юзер
                    // проверяем какие дни мне доступны
                    checkScheduleAndGoToProfile();
                }
                break;
            case R.id.noPremiumGuestServiceText:
                showPremium();
                break;

            case R.id.yesPremiumGuestServiceText:
                showPremium();
                break;

            case R.id.ratingGuestServiceLayout:
                goToComments();
            default:
                break;
        }
    }

    private void getInfoAboutService(String serviceId) {
        // все осервисе, оценка, количество оценок
        String sqlQuery =
                "SELECT *"
                        + " FROM " + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " = ? ";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});

        if (cursor.moveToFirst()) {
            int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexMinCost = cursor.getColumnIndex(DBHelper.KEY_MIN_COST_SERVICES);
            int indexAddress = cursor.getColumnIndex(DBHelper.KEY_ADDRESS_SERVICES);
            int indexIsPremium = cursor.getColumnIndex(DBHelper.KEY_IS_PREMIUM_SERVICES);
            int indexServiceRating = cursor.getColumnIndex(DBHelper.KEY_RATING_SERVICES);
            int indexCountOfRates = cursor.getColumnIndex(DBHelper.KEY_COUNT_OF_RATES_SERVICES);
            int indexDescription = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION_SERVICES);
            float serviceRating = Float.valueOf(cursor.getString(indexServiceRating));
            boolean isPremium = checkPremium(cursor.getString(indexIsPremium));
            long countOfRates = Long.valueOf(cursor.getString(indexCountOfRates));

            Service service = new Service();
            service.setCost(cursor.getString(indexMinCost));
            service.setAddress(cursor.getString(indexAddress));
            service.setDescription(cursor.getString(indexDescription));
            service.setName(cursor.getString(indexName));
            service.setAverageRating(serviceRating);
            service.setCountOfRates(countOfRates);
            service.setIsPremium(isPremium);

            setGuestService(service);
        }
        cursor.close();
    }

    private void setGuestService(Service service){

        serviceName = service.getName();
        costText.setText("Цена от: " + service.getCost() + "р");
        addressText.setText("Адрес: " + service.getAddress());
        descriptionText.setText(service.getDescription());

        if (service.getIsPremium()) {
            setWithPremium();
        }

        countOfRatesForComments = String.valueOf(service.getCountOfRates());
        createRatingBar(service.getAverageRating(), service.getCountOfRates());
        createPhotoFeed(serviceId);
    }
    private void buildPanels() {
        PanelBuilder panelBuilder = new PanelBuilder();
        FragmentManager manager = getSupportFragmentManager();
        topPanelLayout.removeAllViews();

        panelBuilder.buildHeader(manager, getServiceName(), R.id.headerGuestServiceLayout, isMyService, serviceId, ownerId);
        panelBuilder.buildFooter(manager, R.id.footerGuestServiceLayout);

    }

    private void checkScheduleAndGoToProfile() {
        if (WorkWithLocalStorageApi.hasAvailableTime(serviceId, userId, dbHelper.getReadableDatabase())) {
            goToMyCalendar(USER);
        } else {
            attentionThisScheduleIsEmpty();
        }
    }

    private void createPhotoFeed(String serviceId) {

        int width = getResources().getDimensionPixelSize(R.dimen.photo_width);
        int height = getResources().getDimensionPixelSize(R.dimen.photo_height);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        //получаем ссылку на фото по id владельца
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_PHOTO_LINK_PHOTOS
                        + " FROM "
                        + DBHelper.TABLE_PHOTOS
                        + " WHERE "
                        + DBHelper.KEY_OWNER_ID_PHOTOS + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});

        if (cursor.moveToFirst()) {
            do {
                int indexPhotoLink = cursor.getColumnIndex(DBHelper.KEY_PHOTO_LINK_PHOTOS);

                String photoLink = cursor.getString(indexPhotoLink);

                ImageView serviceImage = new ImageView(getApplicationContext());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        width,
                        height);
                params.setMargins(15, 15, 15, 15);
                serviceImage.setLayoutParams(params);
                serviceImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageFeedLayout.addView(serviceImage);

                //установка аватарки
                Picasso.get()
                        .load(photoLink)
                        .resize(width, height)
                        .centerCrop()
                        .into(serviceImage);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildPanels();
        imageFeedLayout.removeAllViews();
        createPhotoFeed(serviceId);
    }

    private String getServiceName() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        //получаем ссылку на фото по id владельца
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_NAME_SERVICES
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});

        if (cursor.moveToFirst()) {
            int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            serviceName = cursor.getString(indexName);
            return serviceName;
        }
        return null;
    }

    private void attentionThisScheduleIsEmpty() {
        Toast.makeText(
                this,
                "Пользователь еще не написал расписание к этому сервису.",
                Toast.LENGTH_SHORT).show();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void createRatingBar(float avgRating, long countOfRates) {
        if (countOfRates > 0) {
            countOfRatesText.setVisibility(View.VISIBLE);
            avgRatesText.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);

            countOfRatesText.setText("(" + countOfRates + " оценок)");

            //приводим цифры к виду, чтобы было 2 число после запятой
            String avgRatingWithFormat = new DecimalFormat("#0.00").format(avgRating);
            avgRatesText.setText(avgRatingWithFormat);

            ratingBar.setRating(avgRating);
            ratingLL.setOnClickListener(this);
        }
        else {
            setWithoutRating();
        }

        mainScroll.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void showPremium() {
        if (isPremiumLayoutSelected) {
            premiumLayout.setVisibility(View.GONE);
            isPremiumLayoutSelected = false;
        } else {
            premiumLayout.setVisibility(View.VISIBLE);
            isPremiumLayoutSelected = true;
        }
    }

    private void setWithoutRating() {
        withoutRatingText.setVisibility(View.VISIBLE);
    }

    private void setWithPremium() {
        noPremiumText.setVisibility(View.GONE);
        premiumText.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPremium() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS)
                .child(getUserId())
                .child(SERVICES)
                .child(serviceId)
                .child(IS_PREMIUM);
        String premiumDate = WorkWithTimeApi.getDateInFormatYMDHMS(new Date());
        myRef.setValue(premiumDate);

        setWithPremium();
        premiumLayout.setVisibility(View.GONE);
        attentionPremiumActivated();
        updatePremiumLocalStorage(serviceId, premiumDate);
    }

    private void updatePremiumLocalStorage(String serviceId, String premiumDate) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_IS_PREMIUM_SERVICES, premiumDate);
        //update
        database.update(DBHelper.TABLE_CONTACTS_SERVICES, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{serviceId});
    }

    @Override
    public void checkCode(final String code) {
        //проверка кода
        Query query = FirebaseDatabase.getInstance().getReference(CODES).
                orderByChild(CODE).
                equalTo(code);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot codesSnapshot) {
                if (codesSnapshot.getChildrenCount() == 0) {
                    attentionWrongCode();
                } else {
                    DataSnapshot userSnapshot = codesSnapshot.getChildren().iterator().next();
                    int count = userSnapshot.child(COUNT).getValue(int.class);
                    if (count > 0) {
                        setPremium();

                        String codeId = userSnapshot.getKey();

                        DatabaseReference myRef = FirebaseDatabase.getInstance()
                                .getReference(CODES)
                                .child(codeId);
                        Map<String, Object> items = new HashMap<>();
                        items.put(COUNT, count - 1);
                        myRef.updateChildren(items);
                    } else {
                        attentionOldCode();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void attentionWrongCode() {
        Toast.makeText(this, "Неверно введен код", Toast.LENGTH_SHORT).show();
    }

    private void attentionOldCode() {
        Toast.makeText(this, "Код больше не действителен", Toast.LENGTH_SHORT).show();
    }

    private void attentionPremiumActivated() {
        Toast.makeText(this, "Премиум активирован", Toast.LENGTH_LONG).show();
    }

    private void goToMyCalendar(String status) {
        Intent intent = new Intent(this, MyCalendar.class);
        intent.putExtra(SERVICE_ID, serviceId);
        intent.putExtra(STATUS_USER_BY_SERVICE, status);

        startActivity(intent);
    }

    private void goToComments() {
        Intent intent = new Intent(this, Comments.class);
        intent.putExtra(SERVICE_ID, serviceId);
        intent.putExtra(TYPE, REVIEW_FOR_SERVICE);
        intent.putExtra(SERVICE_OWNER_ID, ownerId);
        intent.putExtra(COUNT_OF_RATES, countOfRatesForComments);
        startActivity(intent);
    }

}