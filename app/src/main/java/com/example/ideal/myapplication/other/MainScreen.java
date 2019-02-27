package com.example.ideal.myapplication.other;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.fragments.foundElements.foundServiceElement;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.reviews.DownloadServiceData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainScreen extends AppCompatActivity {

    // добавить, чтобы не было видно своих сервисов
    // например номер юзера, возвращаемого сервиса не должен быть равен локальному
    private static final String TAG = "DBInf";
    private static final String FILE_NAME = "Info";
    private static final String PHONE_NUMBER = "Phone number";

    private static final String USERS = "users";
    private static final String NAME = "name";
    private static final String CITY = "city";

    private static final String SERVICES = "services";
    private static final String USER_ID = "user id";

    private int countOfService = 0;

    private DBHelper dbHelper;

    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        manager = getSupportFragmentManager();

        dbHelper = new DBHelper(this);

        PanelBuilder panelBuilder = new PanelBuilder(this);
        panelBuilder.buildFooter(manager, R.id.footerMainScreenLayout);
        panelBuilder.buildHeader(manager, "Главная", R.id.headerMainScreenLayout);

        createMainScreen();
    }

    private void createMainScreen(){
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //получаем id пользователя
        String userId = getUserId();

        //получаем город юзера
        String userCity = getUserCity(database,userId);

        //получаем все сервисы, которые находятся в городе юзера
        getServicesInThisCity(userCity);
    }


    private String getUserCity(SQLiteDatabase database,String userId){
        // Получить город юзера
        // Таблица Users
        // с фиксированным userId
        String sqlQuery =
                "SELECT " + DBHelper.KEY_CITY_USERS
                        + " FROM " + DBHelper.TABLE_CONTACTS_USERS
                        + " WHERE " + DBHelper.KEY_USER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[] {userId});

        int indexCity = cursor.getColumnIndex(DBHelper.KEY_CITY_USERS);
        // дефолтное значение
        String city="Dubna";

        if(cursor.moveToFirst()) {
            city = cursor.getString(indexCity);
        }
        cursor.close();
        return city;
    }

    private  void getServicesInThisCity(final String userCity){

        final SQLiteDatabase database = dbHelper.getReadableDatabase();

        final int limitOfService = 6;
        //возвращение всех пользователей из контретного города
        Query userQuery = FirebaseDatabase.getInstance().getReference(USERS)
                .orderByChild(CITY)
                .equalTo(userCity);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    String userName = String.valueOf(snapshot.child(NAME).getValue());
                    final String userId = snapshot.getKey();

                    final User user = new User();
                    user.setName(userName);
                    user.setCity(userCity);

                    Query serviceQuery = FirebaseDatabase.getInstance().getReference(SERVICES)
                            .orderByChild(USER_ID)
                            .equalTo(userId);

                    serviceQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String serviceId = snapshot.getKey();

                                DownloadServiceData downloadServiceData = new DownloadServiceData();
                                downloadServiceData.loadSchedule(serviceId,database,
                                        "MainScreen",manager);

                                countOfService++;
                                //количество возможных предложений на mainScreen
                                if(countOfService == limitOfService) {
                                    return;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            attentionBadConnection();
                        }
                    });
                    if(countOfService == limitOfService) {
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
        countOfService = 0;
    }

    private  String getUserId(){
        SharedPreferences sPref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        return sPref.getString(PHONE_NUMBER, "-");
    }

    private void attentionBadConnection() {
        Toast.makeText(this,"Плохое соединение",Toast.LENGTH_SHORT).show();
    }
}