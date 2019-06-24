package com.example.ideal.myapplication.reviews;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.adapters.CommentAdapter;
import com.example.ideal.myapplication.fragments.objects.Comment;
import com.example.ideal.myapplication.helpApi.LoadingGuestServiceData;
import com.example.ideal.myapplication.helpApi.LoadingUserElementData;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Comments extends AppCompatActivity {

    private static final String TAG = "DBInf";

    private static final String TYPE = "type";
    private static final String REVIEW_FOR_USER = "review for user";
    private static final String REVIEW_FOR_SERVICE = "review for service";
    private static final String USERS = "users";
    private static final String ORDER_ID = "order_id";
    private static final String OWNER_ID = "owner_id";
    private static final String SERVICE_OWNER_ID = "service owner id";
    private static final String NAME = "name";
    private static final String SERVICE_ID = "service id";
    private static final String SERVICES = "services";
    private static final String WORKING_DAYS = "working days";
    private static final String WORKING_TIME = "working time";
    private static final String DATE = "date";
    private static final String COUNT_OF_RATES = "count of rates";
    private static final String ORDERS = "orders";

    private static final String WORKING_DAY_ID = "working day id";
    private static final String WORKING_TIME_ID = "working time id";
    private static final String WORKER_ID = "worker id";

    private static final String REVIEWS = "reviews";
    private static final String REVIEW = "review";
    private static final String RATING = "rating";

    private FragmentManager manager;
    private WorkWithLocalStorageApi workWithLocalStorageApi;
    private ProgressBar progressBar;
    private ArrayList<Comment> commentList;
    private RecyclerView recyclerView;
    private TextView withoutRatingText;
    private CommentAdapter commentAdapter;
    private String serviceId;
    private String ownerId;
    private long countOfRates;
    private long currentCountOfReview;
    private boolean addedReview;
    private Thread additionToLocalStorage;
    private SQLiteDatabase database;
    private static ArrayList<String> serviceIdsFirstSetComments = new ArrayList<>();
    private static ArrayList<String> userIdsFirstSetComments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);
        init();

        String type = getIntent().getStringExtra(TYPE);
        if (type.equals(REVIEW_FOR_SERVICE)) {
            countOfRates = Long.valueOf(getIntent().getStringExtra(COUNT_OF_RATES));
            if (!serviceIdsFirstSetComments.contains(serviceId)) {
                loadCommentsForService();
                serviceIdsFirstSetComments.add(serviceId);
            } else {
                getCommentsForService(serviceId);
            }
        } else {
            //ПЕРЕПИСАТЬ И БРАТЬ НОРМАЛЬНОЕ КОЛИЧЕСТВО
            countOfRates = Long.valueOf(getIntent().getStringExtra(COUNT_OF_RATES));
            //комментарии для юзера
            if (!userIdsFirstSetComments.contains(ownerId)) {
                loadCommentsForUser();
                userIdsFirstSetComments.add(ownerId);
            } else {
                getCommentsForUser(ownerId);
            }
        }
    }

    private void init() {
        DBHelper dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        manager = getSupportFragmentManager();
        serviceId = getIntent().getStringExtra(SERVICE_ID);
        ownerId = getIntent().getStringExtra(SERVICE_OWNER_ID);
        currentCountOfReview = 0;
        recyclerView = findViewById(R.id.resultsCommentsRecycleView);
        progressBar = findViewById(R.id.progressBarComments);
        withoutRatingText = findViewById(R.id.withoutReviewsCommentsText);
        commentList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        workWithLocalStorageApi = new WorkWithLocalStorageApi(database);
    }

    private void loadCommentsForService() {
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference workingDaysRef = firebaseDatabase.getReference(USERS)
                .child(ownerId)
                .child(SERVICES)
                .child(serviceId)
                .child(WORKING_DAYS);

        workingDaysRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot workingDaySnapshot, @Nullable String s) {
                final String workingDayId = workingDaySnapshot.getKey();
                long sysdateLong = WorkWithTimeApi.getSysdateLong();
                long dateLong = WorkWithTimeApi.getMillisecondsStringDateYMD(workingDaySnapshot.child(DATE).getValue(String.class));
                //Важное отличие от GS, загрузка только просроченных дней
                if (dateLong < sysdateLong) {
                    LoadingGuestServiceData.addWorkingDaysInLocalStorage(workingDaySnapshot, serviceId, database);

                    final DatabaseReference workingTimesRef = workingDaysRef
                            .child(workingDayId)
                            .child(WORKING_TIME);

                    workingTimesRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull final DataSnapshot timeSnapshot, @Nullable String s) {
                            //при добавлении нового времени
                            LoadingGuestServiceData.addTimeInLocalStorage(timeSnapshot, workingDayId, database);
                            final String timeId = timeSnapshot.getKey();
                            final DatabaseReference ordersRef = workingTimesRef
                                    .child(timeId)
                                    .child(ORDERS);
                            ordersRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {
                                    LoadingGuestServiceData.addOrderInLocalStorage(orderSnapshot, timeId, database);
                                    // ревью
                                    final String orderId = orderSnapshot.getKey();
                                    DatabaseReference reviewRef = ordersRef
                                            .child(orderId)
                                            .child(REVIEWS);
                                    reviewRef.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot reviewSnapshot, @Nullable String s) {
                                            addReviewInLocalStorage(reviewSnapshot, orderId);
                                            currentCountOfReview++;
                                            if (countOfRates == currentCountOfReview) {
                                                getCommentsForService(serviceId);
                                            }
                                        }

                                        @Override
                                        public void onChildChanged(@NonNull DataSnapshot reviewSnapshot, @Nullable String s) {
                                            addReviewInLocalStorage(reviewSnapshot, orderId);
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

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {
                                    //если от кого-то отказались
                                    LoadingGuestServiceData.addOrderInLocalStorage(orderSnapshot, timeId, database);
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

    private void getCommentsForService(String _serviceId) {
        currentCountOfReview = 0;

        String mainSqlQuery = "SELECT "
                + DBHelper.KEY_REVIEW_REVIEWS + ", "
                + DBHelper.KEY_RATING_REVIEWS + ", "
                + DBHelper.KEY_NAME_SERVICES + ", "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + ", "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " AS " + OWNER_ID + ", "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID + " AS " + ORDER_ID
                + " FROM "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_REVIEWS + ", "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_CONTACTS_USERS + ", "
                + DBHelper.TABLE_ORDERS + ", "
                + DBHelper.TABLE_CONTACTS_SERVICES
                + " WHERE "
                + DBHelper.KEY_ORDER_ID_REVIEWS
                + " = "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                + " = "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                + " = "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                + " = "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID
                + " = "
                + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ? "
                + " AND "
                + DBHelper.KEY_TYPE_REVIEWS + " = ? "
                + " AND "
                + DBHelper.KEY_RATING_REVIEWS + " != 0 ";

        Cursor cursor = database.rawQuery(mainSqlQuery, new String[]{_serviceId, REVIEW_FOR_SERVICE});

        if (cursor.moveToFirst()) {
            int indexWorkingTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
            int indexOrderId = cursor.getColumnIndex(ORDER_ID);
            do {
                String workingTimeId = cursor.getString(indexWorkingTimeId);
                //String orderId = cursor.getString(indexOrderId);

                //if (workWithLocalStorageApi.isMutualReview(orderId)) {
                //   createServiceComment(cursor);
                // } else {
                if (workWithLocalStorageApi.isAfterThreeDays(workingTimeId)) {
                    createServiceComment(cursor);
                }
                // }
                currentCountOfReview++;

            } while (cursor.moveToNext());
        } else {
            setWithoutReview();
        }
        if (!addedReview) {
            setWithoutReview();
        }
        cursor.close();
    }

    private void createServiceComment(final Cursor mainCursor) {
        final int reviewIndex = mainCursor.getColumnIndex(DBHelper.KEY_REVIEW_REVIEWS);
        final int ratingIndex = mainCursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
        final int ownerIdIndex = mainCursor.getColumnIndex(OWNER_ID);
        final int nameServiceIndex = mainCursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
        String ownerId = mainCursor.getString(ownerIdIndex);
        addedReview = true;
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(USERS)
                .child(ownerId);
        final Comment comment = new Comment();
        comment.setUserId(mainCursor.getString(ownerIdIndex));
        comment.setReview(mainCursor.getString(reviewIndex));
        comment.setRating(mainCursor.getFloat(ratingIndex));
        comment.setServiceName(mainCursor.getString(nameServiceIndex));

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                comment.setUserName(String.valueOf(userSnapshot.child(NAME).getValue()));
                commentList.add(comment);
                if (countOfRates == currentCountOfReview) {
                    commentAdapter = new CommentAdapter(commentList.size(), commentList);
                    recyclerView.setAdapter(commentAdapter);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadCommentsForUser() {

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference(USERS)
                .child(ownerId)
                .child(ORDERS);

        orderRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {

                final String orderId = orderSnapshot.getKey();

                final String serviceId = (String) orderSnapshot.child(SERVICE_ID).getValue();
                final String workingDayId = (String) orderSnapshot.child(WORKING_DAY_ID).getValue();
                final String workingTimeId = (String) orderSnapshot.child(WORKING_TIME_ID).getValue();
                final String workerId = (String) orderSnapshot.child(WORKER_ID).getValue();

                //Вызываем отдельный поток, который кладет данные в лок БД
                additionToLocalStorage = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //в таблицу USERS
                        ContentValues contentValuesUser = new ContentValues();
                        contentValuesUser.put(DBHelper.KEY_ID, workerId);
                        boolean hasSomeData = WorkWithLocalStorageApi
                                .hasSomeData(DBHelper.TABLE_CONTACTS_USERS, workerId);
                        if (!hasSomeData) {
                            contentValuesUser.put(DBHelper.KEY_ID, workerId);
                            database.insert(DBHelper.TABLE_CONTACTS_USERS, null, contentValuesUser);
                        }

                        //в таблицу SERVICES
                        ContentValues contentValuesService = new ContentValues();
                        contentValuesService.put(DBHelper.KEY_ID, serviceId);
                        contentValuesService.put(DBHelper.KEY_USER_ID, workerId);
                        if (WorkWithLocalStorageApi
                                .hasSomeData(DBHelper.TABLE_CONTACTS_SERVICES, serviceId)) {
                            database.update(DBHelper.TABLE_CONTACTS_SERVICES, contentValuesService,
                                    DBHelper.KEY_ID + " = ?",
                                    new String[]{serviceId});
                        } else {
                            database.insert(DBHelper.TABLE_CONTACTS_SERVICES, null, contentValuesService);
                        }

                        //в таблицу WorkingDay
                        ContentValues contentValuesWorkingDay = new ContentValues();
                        contentValuesWorkingDay.put(DBHelper.KEY_ID, workingDayId);
                        contentValuesWorkingDay.put(DBHelper.KEY_SERVICE_ID_WORKING_DAYS, serviceId);

                        if (WorkWithLocalStorageApi
                                .hasSomeData(DBHelper.TABLE_WORKING_DAYS, workingDayId)) {
                            database.update(DBHelper.TABLE_WORKING_DAYS, contentValuesWorkingDay,
                                    DBHelper.KEY_ID + " = ?",
                                    new String[]{workingDayId});
                        } else {
                            database.insert(DBHelper.TABLE_WORKING_DAYS, null, contentValuesWorkingDay);
                        }

                        //в таблицу WorkingTime
                        ContentValues contentValuesWorkingTime = new ContentValues();
                        contentValuesWorkingTime.put(DBHelper.KEY_ID, workingTimeId);
                        contentValuesWorkingTime.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, workingDayId);

                        if (WorkWithLocalStorageApi
                                .hasSomeData(DBHelper.TABLE_WORKING_TIME, workingTimeId)) {
                            database.update(DBHelper.TABLE_WORKING_TIME, contentValuesWorkingTime,
                                    DBHelper.KEY_ID + " = ?",
                                    new String[]{workingTimeId});
                        } else {
                            database.insert(DBHelper.TABLE_WORKING_TIME, null, contentValuesWorkingTime);
                        }

                        //в таблицу Orders
                        ContentValues contentValuesOrder = new ContentValues();
                        contentValuesOrder.put(DBHelper.KEY_ID, orderId);
                        contentValuesOrder.put(DBHelper.KEY_WORKING_TIME_ID_ORDERS, workingTimeId);
                        contentValuesOrder.put(DBHelper.KEY_USER_ID, ownerId);

                        if (WorkWithLocalStorageApi
                                .hasSomeData(DBHelper.TABLE_ORDERS, orderId)) {
                            database.update(DBHelper.TABLE_ORDERS, contentValuesOrder,
                                    DBHelper.KEY_ID + " = ?",
                                    new String[]{orderId});
                        } else {
                            database.insert(DBHelper.TABLE_ORDERS, null, contentValuesOrder);
                        }
                        additionToLocalStorage.interrupt();
                    }
                });
                additionToLocalStorage.start();

                final DatabaseReference timeRef = FirebaseDatabase.getInstance()
                        .getReference(USERS)
                        .child(workerId)
                        .child(SERVICES)
                        .child(serviceId)
                        .child(WORKING_DAYS)
                        .child(workingDayId);

                final DatabaseReference reviewRef = orderRef
                        .child(orderId)
                        .child(REVIEWS);

                timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot workingDaySnapshot) {
                        LoadingGuestServiceData.addWorkingDaysInLocalStorage(workingDaySnapshot, serviceId, database);
                        for (DataSnapshot timeSnapshot : workingDaySnapshot.child(WORKING_TIME).getChildren()) {
                            LoadingGuestServiceData.addTimeInLocalStorage(timeSnapshot, workingDayId, database);
                        }

                        reviewRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot reviewSnapshot, @Nullable String s) {
                                currentCountOfReview++;
                                addReviewInLocalStorage(reviewSnapshot, orderId);
                                if (countOfRates == currentCountOfReview) {
                                    getCommentsForUser(ownerId);
                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot reviewSnapshot, @Nullable String s) {
                                addReviewInLocalStorage(reviewSnapshot, orderId);
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

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {

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

    private void getCommentsForUser(String _userId) {
        currentCountOfReview = 0;

        String mainSqlQuery = "SELECT "
                + DBHelper.KEY_RATING_REVIEWS + ", "
                + DBHelper.KEY_REVIEW_REVIEWS + ", "
                + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_NAME_USERS + ", "
                + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID + " AS " + OWNER_ID + ", "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + ", "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + ", "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID + " AS " + ORDER_ID
                + " FROM "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_REVIEWS + ", "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_CONTACTS_USERS + ", "
                + DBHelper.TABLE_ORDERS + ", "
                + DBHelper.TABLE_CONTACTS_SERVICES
                + " WHERE "
                + DBHelper.KEY_ORDER_ID_REVIEWS
                + " = "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                + " = "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                + " = "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                + " = "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID
                + " = "
                + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " = ? "
                + " AND "
                + DBHelper.KEY_TYPE_REVIEWS + " = ? "
                + " AND "
                + DBHelper.KEY_RATING_REVIEWS + " != 0";
        // убрать не оценненые
        final Cursor cursor = database.rawQuery(mainSqlQuery, new String[]{_userId, REVIEW_FOR_USER});
        if (cursor.moveToFirst()) {
            int indexWorkingTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
            int indexOrderId = cursor.getColumnIndex(ORDER_ID);
            do {
                String workingTimeId = cursor.getString(indexWorkingTimeId);
                //String orderId = cursor.getString(indexOrderId);

                //if (workWithLocalStorageApi.isMutualReview(orderId)) {
                //    createUserComment(cursor);
                //} else {
                if (workWithLocalStorageApi.isAfterThreeDays(workingTimeId)) {
                    createUserComment(cursor);
                }
                //}
                currentCountOfReview++;

            } while (cursor.moveToNext());
        } else {
            setWithoutReview();
        }
        if (!addedReview) {
            setWithoutReview();
        }
        cursor.close();
    }

    private int currentForCreateUserComment=0;
    private void createUserComment(final Cursor mainCursor) {
        final int reviewIndex = mainCursor.getColumnIndex(DBHelper.KEY_REVIEW_REVIEWS);
        final int ratingIndex = mainCursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
        final int ownerIdIndex = mainCursor.getColumnIndex(OWNER_ID);
        addedReview = true;
        String ownerId = mainCursor.getString(ownerIdIndex);
        DatabaseReference myRef = FirebaseDatabase
                .getInstance()
                .getReference(USERS)
                .child(ownerId);
        final Comment comment = new Comment();
        comment.setUserId(mainCursor.getString(ownerIdIndex));
        comment.setReview(mainCursor.getString(reviewIndex));
        comment.setRating(mainCursor.getFloat(ratingIndex));
        comment.setServiceName(REVIEW_FOR_USER);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                comment.setUserName(String.valueOf(userSnapshot.child(NAME).getValue()));
                LoadingUserElementData.loadUserNameAndPhoto(userSnapshot, database);
                commentList.add(comment);
                currentForCreateUserComment++;
                Log.d(TAG, "currentCountOfReview: "+currentCountOfReview);
                Log.d(TAG, "countOfRates: "+currentCountOfReview);
                if (countOfRates == currentForCreateUserComment) {
                        commentAdapter = new CommentAdapter(commentList.size(), commentList);
                        recyclerView.setAdapter(commentAdapter);
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addReviewInLocalStorage(DataSnapshot reviewSnapshot, String orderId) {

        ContentValues contentValues = new ContentValues();
        String reviewId = reviewSnapshot.getKey();
        contentValues.put(DBHelper.KEY_REVIEW_REVIEWS, String.valueOf(reviewSnapshot.child(REVIEW).getValue()));
        contentValues.put(DBHelper.KEY_RATING_REVIEWS, String.valueOf(reviewSnapshot.child(RATING).getValue()));
        contentValues.put(DBHelper.KEY_TYPE_REVIEWS, String.valueOf(reviewSnapshot.child(TYPE).getValue()));
        contentValues.put(DBHelper.KEY_ORDER_ID_REVIEWS, orderId);

        boolean hasSomeData = WorkWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_REVIEWS, reviewId);

        if (hasSomeData) {
            database.update(DBHelper.TABLE_REVIEWS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{reviewId});
        } else {
            contentValues.put(DBHelper.KEY_ID, reviewId);
            database.insert(DBHelper.TABLE_REVIEWS, null, contentValues);
        }

    }

    private void setWithoutReview() {
        progressBar.setVisibility(View.GONE);
        withoutRatingText.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerCommentsLayout);
        panelBuilder.buildHeader(manager, "Отзывы", R.id.headerCommentsLayout);
    }

}
