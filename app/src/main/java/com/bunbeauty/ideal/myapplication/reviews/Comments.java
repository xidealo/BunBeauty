package com.bunbeauty.ideal.myapplication.reviews;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.adapters.CommentAdapter;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.FBListener;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Comment;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User;
import com.bunbeauty.ideal.myapplication.helpApi.ListeningManager;
import com.bunbeauty.ideal.myapplication.helpApi.LoadingCommentsData;
import com.bunbeauty.ideal.myapplication.helpApi.LoadingUserElementData;
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper;
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
    private static final String SERVICE_ID = "service id";
    private static final String WORKING_DAYS = "working days";
    private static final String WORKING_TIME = "working time";
    private static final String DATE = "date";
    private static final String COUNT_OF_RATES = "count of rates";
    private static final String ORDERS = "orders";
    private static final String RATING = "rating";
    private static final String REVIEW = "review";

    private static final String WORKING_DAY_ID = "working day id";
    private static final String WORKING_TIME_ID = "working time id";
    private static final String WORKER_ID = "worker id";

    private static final String REVIEWS = "reviews";

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
    public static ArrayList<String> serviceIdsFirstSetComments = new ArrayList<>();
    public static ArrayList<String> userIdsFirstSetComments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);
        init();

        String type = getIntent().getStringExtra(TYPE);
        if (type.equals(REVIEW_FOR_SERVICE)) {
            countOfRates = Long.valueOf(getIntent().getStringExtra(COUNT_OF_RATES));
            Log.d(TAG, "REVIEW_FOR_SERVICE: " + countOfRates);
            if (!serviceIdsFirstSetComments.contains(serviceId)) {
                loadCommentsForService();
                serviceIdsFirstSetComments.add(serviceId);
            } else {
                getCommentsForService(serviceId);
            }
        } else {
            countOfRates = Long.valueOf(getIntent().getStringExtra(COUNT_OF_RATES));
            Log.d(TAG, "REVIEW_FOR_USER: " + countOfRates);
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
                .child(Service.SERVICES)
                .child(serviceId)
                .child(WORKING_DAYS);

        ChildEventListener workingDaysListener = workingDaysRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot workingDaySnapshot, @Nullable String s) {
                final String workingDayId = workingDaySnapshot.getKey();
                long sysdateLong = WorkWithTimeApi.getSysdateLong();
                long dateLong = WorkWithTimeApi.getMillisecondsStringDateYMD(workingDaySnapshot.child(DATE).getValue(String.class));
                //Важное отличие от GS, загрузка только просроченных дней
                if (dateLong < sysdateLong) {
                    LoadingCommentsData.addWorkingDaysInLocalStorage(workingDaySnapshot, serviceId, database);

                    final DatabaseReference workingTimesRef = workingDaysRef
                            .child(workingDayId)
                            .child(WORKING_TIME);

                    ChildEventListener workingTimesListener = workingTimesRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull final DataSnapshot timeSnapshot, @Nullable String s) {
                            //при добавлении нового времени
                            LoadingCommentsData.addTimeInLocalStorage(timeSnapshot, workingDayId, database);
                            final String timeId = timeSnapshot.getKey();
                            final DatabaseReference ordersRef = workingTimesRef
                                    .child(timeId)
                                    .child(ORDERS);
                            ChildEventListener ordersListener = ordersRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {
                                    LoadingCommentsData.addOrderInLocalStorage(orderSnapshot, timeId, database);

                                    final String orderId = orderSnapshot.getKey();
                                    DatabaseReference reviewRef = ordersRef
                                            .child(orderId)
                                            .child(REVIEWS);
                                    ChildEventListener reviewListener = reviewRef.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot reviewSnapshot, @Nullable String s) {
                                            LoadingCommentsData.addReviewInLocalStorage(reviewSnapshot, orderId, database);

                                            //если на одном времени больше чем 1 ревью
                                            if (!String.valueOf(reviewSnapshot.child(RATING).getValue()).equals("0")) {
                                                currentCountOfReview++;
                                            }

                                            if (countOfRates == currentCountOfReview) {
                                                Log.d(TAG, "onChildAdded: ");
                                                getCommentsForService(serviceId);
                                            }
                                            Log.d(TAG, "onChildAdded: ");
                                        }

                                        @Override
                                        public void onChildChanged(@NonNull DataSnapshot reviewSnapshot, @Nullable String s) {
                                            LoadingCommentsData.addReviewInLocalStorage(reviewSnapshot, orderId, database);
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

                                    ListeningManager.addToListenerList(new FBListener(reviewRef, reviewListener));
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {
                                    //если от кого-то отказались
                                    LoadingCommentsData.addOrderInLocalStorage(orderSnapshot, timeId, database);
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

                            ListeningManager.addToListenerList(new FBListener(ordersRef, ordersListener));
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot timeSnapshot) {
                            //при удалении времени
                            //LoadingGuestServiceData.deleteTimeFromLocalStorage(timeSnapshot.getKey(), database);
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                    ListeningManager.addToListenerList(new FBListener(workingTimesRef, workingTimesListener));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
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

        ListeningManager.addToListenerList(new FBListener(workingDaysRef, workingDaysListener));
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
                + DBHelper.KEY_RATING_REVIEWS + " != 0 "
                + " ORDER BY " + DBHelper.KEY_TIME_REVIEWS + " DESC";

        Cursor cursor = database.rawQuery(mainSqlQuery, new String[]{_serviceId, REVIEW_FOR_SERVICE});

        if (cursor.moveToFirst()) {
            int indexWorkingTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
            do {
                String workingTimeId = cursor.getString(indexWorkingTimeId);

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
                comment.setUserName(String.valueOf(userSnapshot.child(User.NAME).getValue()));
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

        final DatabaseReference orderRef = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(ownerId)
                .child(ORDERS);

        ChildEventListener orderListener = orderRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {

                final String orderId = orderSnapshot.getKey();

                final String serviceId = (String) orderSnapshot.child(SERVICE_ID).getValue();
                final String workingDayId = (String) orderSnapshot.child(WORKING_DAY_ID).getValue();
                final String workingTimeId = (String) orderSnapshot.child(WORKING_TIME_ID).getValue();
                final String workerId = (String) orderSnapshot.child(WORKER_ID).getValue();

                //Вызываем отдельный поток, который кладет путь в лок БД
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
                        .child(Service.SERVICES)
                        .child(serviceId)
                        .child(WORKING_DAYS)
                        .child(workingDayId);

                timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot workingDaySnapshot) {
                        //set day with his time
                        LoadingCommentsData.addWorkingDaysInLocalStorage(workingDaySnapshot, serviceId, database);
                        for (DataSnapshot timeSnapshot : workingDaySnapshot.child(WORKING_TIME).getChildren()) {
                            LoadingCommentsData.addTimeInLocalStorage(timeSnapshot, workingDayId, database);
                        }

                        //can be optimized
                        final DatabaseReference reviewRef = orderRef
                                .child(orderId)
                                .child(REVIEWS);

                        ChildEventListener reviewListener = reviewRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot reviewSnapshot, @Nullable String s) {
                                LoadingCommentsData.addReviewInLocalStorage(reviewSnapshot, orderId, database);
                                final Comment comment = new Comment();
                                // no review no currentCountOfReview!
                                String review = reviewSnapshot.child(REVIEW).getValue(String.class);
                                float rating = reviewSnapshot.child(RATING).getValue(Float.class);
                                if (rating != 0) {
                                    comment.setReview(review);
                                    comment.setRating(reviewSnapshot.child(RATING).getValue(Float.class));
                                    comment.setUserId(workerId);
                                    currentCountOfReview++;
                                    Log.d(TAG, "onChildAdded: " + reviewSnapshot.child(REVIEW).getValue());
                                    Log.d(TAG, "onChildAdded: " + currentCountOfReview);
                                    if (workWithLocalStorageApi.isAfterThreeDays(workingTimeId)) {
                                        createUserComment(comment);
                                    }
                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot reviewSnapshot, @Nullable String s) {
                                LoadingCommentsData.addReviewInLocalStorage(reviewSnapshot, orderId, database);
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

                        ListeningManager.addToListenerList(new FBListener(reviewRef, reviewListener));
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
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        ListeningManager.addToListenerList(new FBListener(orderRef, orderListener));
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
                + DBHelper.KEY_RATING_REVIEWS + " != 0"
                + " ORDER BY " + DBHelper.KEY_TIME_REVIEWS + " DESC";

        // убрать не оценненые
        final Cursor cursor = database.rawQuery(mainSqlQuery, new String[]{_userId, REVIEW_FOR_USER});
        if (cursor.moveToFirst()) {
            int indexWorkingTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
            final int reviewIndex = cursor.getColumnIndex(DBHelper.KEY_REVIEW_REVIEWS);
            final int ratingIndex = cursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
            final int ownerIdIndex = cursor.getColumnIndex(OWNER_ID);
            do {
                final Comment comment = new Comment();
                comment.setUserId(cursor.getString(ownerIdIndex));
                comment.setReview(cursor.getString(reviewIndex));
                comment.setRating(cursor.getFloat(ratingIndex));
                String workingTimeId = cursor.getString(indexWorkingTimeId);

                //if (workWithLocalStorageApi.isMutualReview(orderId)) {
                //    createUserComment(cursor);
                //} else {
                if (workWithLocalStorageApi.isAfterThreeDays(workingTimeId)) {
                    createUserComment(comment);
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

    private void createUserComment(final Comment comment) {

        comment.setServiceName(REVIEW_FOR_USER);
        addedReview = true;
        DatabaseReference myRef = FirebaseDatabase
                .getInstance()
                .getReference(USERS)
                .child(comment.getUserId());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                comment.setUserName(String.valueOf(userSnapshot.child(User.NAME).getValue()));
                LoadingUserElementData.loadUserNameAndPhoto(userSnapshot, database);
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
