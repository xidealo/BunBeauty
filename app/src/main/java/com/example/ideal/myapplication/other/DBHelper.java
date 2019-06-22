package com.example.ideal.myapplication.other;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final  int DATABASE_VERSION = 96;
    public static final String DATABASE_NAME = "MyFirstDB";

    //tables name
    public static final String TABLE_CONTACTS_USERS = "users";
    public static final String TABLE_CONTACTS_SERVICES = "services";
    public static final String TABLE_WORKING_DAYS = "working_days";
    public static final String TABLE_WORKING_TIME = "working_time";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_REVIEWS = "reviews";
    public static final String TABLE_PHOTOS = "photos";
    public static final String TABLE_SUBSCRIBERS = "subscribers";

    //for all
    public  static final  String KEY_ID = "_id";

    // users
    public  static final  String KEY_PHONE_USERS = "phone";
    public  static final  String KEY_NAME_USERS = "user_name";
    public  static final  String KEY_CITY_USERS = "city";
    public  static final  String KEY_RATING_USERS = "user_rating";
    public  static final  String KEY_SUBSCRIPTIONS_COUNT_USERS = "subscriptions_count";
    public  static final  String KEY_SUBSCRIBERS_COUNT_USERS = "subscribers_count";

    //services
    public  static final  String KEY_NAME_SERVICES = "service_name";
    public  static final  String KEY_RATING_SERVICES = "service_rating";
    public  static final  String KEY_DESCRIPTION_SERVICES = "description";
    public  static final  String KEY_MIN_COST_SERVICES = "minCost";
    public  static final  String KEY_IS_PREMIUM_SERVICES = "is_premium";
    public  static final  String KEY_CREATION_DATE_SERVICES = "creation_date";
    public  static final  String KEY_CATEGORY_SERVICES = "category";
    public  static final  String KEY_ADDRESS_SERVICES = "address";
    public  static final  String KEY_COUNT_OF_RATES_SERVICES = "count_of_rates";

    //working days
    public  static final  String KEY_DATE_WORKING_DAYS = "date";
    public  static final  String KEY_SERVICE_ID_WORKING_DAYS = "service_id";

    // working time
    public  static final  String KEY_TIME_WORKING_TIME = "time";
    public  static final  String KEY_WORKING_DAYS_ID_WORKING_TIME = "id_working_days";

    //orders
    public  static final  String KEY_IS_CANCELED_ORDERS = "is_canceled";
    public  static final  String KEY_WORKING_TIME_ID_ORDERS = "working_time_id";
    public  static final  String KEY_MESSAGE_TIME_ORDERS = "message_time";

    //reviews
    public  static final  String KEY_REVIEW_REVIEWS = "review";
    public  static final  String KEY_RATING_REVIEWS = "rating";
    public  static final  String KEY_TYPE_REVIEWS = "type";
    public  static final  String KEY_ORDER_ID_REVIEWS = "order_id";

    //photos
    public  static final  String KEY_PHOTO_LINK_PHOTOS = "photo_link";
    public  static final  String KEY_OWNER_ID_PHOTOS = "owner_id";

    //subscribers
    public  static final  String KEY_USER_ID = "user_id";
    public  static final  String KEY_WORKER_ID = "worker_id";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String users = "create table "+ TABLE_CONTACTS_USERS
                + "("
                + KEY_ID + " text primary key,"
                + KEY_PHONE_USERS + " text,"
                + KEY_NAME_USERS + " text,"
                + KEY_CITY_USERS + " text,"
                + KEY_SUBSCRIPTIONS_COUNT_USERS + " text,"
                + KEY_SUBSCRIBERS_COUNT_USERS + " text,"
                + KEY_RATING_USERS + " text"
                + ")";

        String services = "create table "+ TABLE_CONTACTS_SERVICES
                + "(" + KEY_ID + " text primary key,"
                + KEY_USER_ID + " text,"
                + KEY_NAME_SERVICES + " text,"
                + KEY_RATING_SERVICES + " text,"
                + KEY_DESCRIPTION_SERVICES+ " text,"
                + KEY_MIN_COST_SERVICES + " text,"
                + KEY_IS_PREMIUM_SERVICES + " text,"
                + KEY_CATEGORY_SERVICES + " text,"
                + KEY_ADDRESS_SERVICES + " text,"
                + KEY_COUNT_OF_RATES_SERVICES + " text,"
                + KEY_CREATION_DATE_SERVICES+ " text"
                + ")";

        String workingDays = "create table "+ TABLE_WORKING_DAYS
                + "("
                + KEY_ID + " text primary key,"
                + KEY_DATE_WORKING_DAYS + " date,"
                + KEY_SERVICE_ID_WORKING_DAYS + " text"
                + ")";

        String workingTime = "create table "+ TABLE_WORKING_TIME
                + "("
                + KEY_ID + " text primary key,"
                + KEY_TIME_WORKING_TIME + " text,"
                + KEY_WORKING_DAYS_ID_WORKING_TIME + " integer"
                + ")";

        String orders = "create table "+ TABLE_ORDERS
                + "("
                + KEY_ID + " text primary key,"
                + KEY_USER_ID + " text,"
                + KEY_IS_CANCELED_ORDERS + " text,"
                + KEY_MESSAGE_TIME_ORDERS + " text,"
                + KEY_WORKING_TIME_ID_ORDERS + " integer"
                + ")";

        String reviews = "create table "+ TABLE_REVIEWS
                + "(" + KEY_ID + " text primary key,"
                + KEY_REVIEW_REVIEWS + " text,"
                + KEY_RATING_REVIEWS + " text,"
                + KEY_TYPE_REVIEWS + " text,"
                + KEY_ORDER_ID_REVIEWS + " text"
                + ")";

        String photos = "create table "+ TABLE_PHOTOS
                + "("
                + KEY_ID + " text primary key,"
                + KEY_PHOTO_LINK_PHOTOS + " text,"
                + KEY_OWNER_ID_PHOTOS + " text"
                + ")";

        String subscribers = "create table "+ TABLE_SUBSCRIBERS
                + "("
                + KEY_ID + " text primary key,"
                + KEY_USER_ID + " text,"
                + KEY_WORKER_ID + " text"
                + ")";

        // create users table
        db.execSQL(users);

        // create service table
        db.execSQL(services);

        // create working days table
        db.execSQL(workingDays);

        // create working time table
        db.execSQL(workingTime);

        //create reviews
        db.execSQL(reviews);

        //create orders
        db.execSQL(orders);

        //create photos
        db.execSQL(photos);

        //create subscribers
        db.execSQL(subscribers);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_CONTACTS_USERS);
        db.execSQL("drop table if exists " + TABLE_CONTACTS_SERVICES);
        db.execSQL("drop table if exists " + TABLE_WORKING_DAYS);
        db.execSQL("drop table if exists " + TABLE_WORKING_TIME);
        db.execSQL("drop table if exists " + TABLE_REVIEWS);
        db.execSQL("drop table if exists " + TABLE_ORDERS);
        db.execSQL("drop table if exists " + TABLE_PHOTOS);
        db.execSQL("drop table if exists " + TABLE_SUBSCRIBERS);

        onCreate(db);
    }
}
