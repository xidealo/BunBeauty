package com.example.ideal.myapplication.other;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.foundElements.foundServiceElement;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.Search;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity implements View.OnClickListener {

    // добавить, чтобы не было видно своих сервисов
    // например номер юзера, возвращаемого сервиса не должен быть равен локальному
    private static final String TAG = "DBInf";

    private static final String USERS = "users";
    private static final String CITY = "city";

    private DBHelper dbHelper;
    private FragmentManager manager;
  
    private Button [] categoriesBtns;
    private String [] categories;
    private  LinearLayout categoryLayout;

    private LinearLayout resultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        dbHelper = new DBHelper(this);
        manager = getSupportFragmentManager();

        resultLayout = findViewById(R.id.resultsMainScreenLayout);
        categoryLayout = findViewById(R.id.categoryMainScreenLayout);
        categoryLayout = findViewById(R.id.categoryMainScreenLayout);

        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerMainScreenLayout);
        panelBuilder.buildHeader(manager, "Главная", R.id.headerMainScreenLayout);
      
        categoryLayout = findViewById(R.id.categoryMainScreenLayout);

        categoriesBtns = new Button[6];
        categories = new String[]{"ногти", "волосы", "глаза", "визаж", "массаж", "остальные"};

        createCategoryFeed();
        createMainScreen("");

    }

    @Override
    public void onClick(View v) {
        String category;
        Button btn = (Button) v;

        if (Boolean.valueOf((btn.getTag(R.string.selectedId)).toString())) {
            btn.setBackgroundResource(R.drawable.time_button);
            btn.setTag(R.string.selectedId, false);
            category = "";
        } else {
            //for чтобы все сделать неактивынми
            for (Button categoriesBtn : categoriesBtns) {
                categoriesBtn.setTag(R.string.selectedId, false);
                categoriesBtn.setBackgroundResource(R.drawable.day_button);
            }

            btn.setBackgroundResource(R.drawable.pressed_button);
            btn.setTag(R.string.selectedId, true);
            category = btn.getText().toString();
        }

        createMainScreen(category);
    }

    private  void createCategoryFeed(){
        for(int i =0 ;i<categoriesBtns.length; i++){
            categoriesBtns[i] = new Button(this);
            categoriesBtns[i].setWidth(80);
            categoriesBtns[i].setHeight(40);
            categoriesBtns[i].setTag(R.string.selectedId,false);
            categoriesBtns[i].setOnClickListener(this);
            categoriesBtns[i].setText(categories[i]);

            categoryLayout.addView(categoriesBtns[i]);
        }
    }

    private void createMainScreen(String category) {
        //получаем id пользователя
        String userId = getUserId();

        //получаем город юзера
        String userCity = getUserCity(userId);

        //получаем все сервисы, которые находятся в городе юзера
        getServicesInThisCity(userCity, category);
    }

    private String getUserCity(String userId){

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // Получить город юзера
        // Таблица Users
        // с фиксированным userId
        String sqlQuery =
                "SELECT " + DBHelper.KEY_CITY_USERS
                        + " FROM " + DBHelper.TABLE_CONTACTS_USERS
                        + " WHERE " + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{userId});

        int indexCity = cursor.getColumnIndex(DBHelper.KEY_CITY_USERS);
        // дефолтное значение
        String city = "Dubna";

        if (cursor.moveToFirst()) {
            city = cursor.getString(indexCity);
        }
        cursor.close();
        return city;
    }
  
    private void getServicesInThisCity(final String userCity, final String category) {

        final Search search = new Search(this);

        //возвращение всех пользователей из контретного города
        Query userQuery = FirebaseDatabase.getInstance().getReference(USERS)
                .orderByChild(CITY)
                .equalTo(userCity);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {

                ArrayList<Object[]> serviceList = search.getServicesOfUsers(usersSnapshot, null, null, category);
                addToMainScreen(serviceList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void addToMainScreen(ArrayList<Object[]> serviceList) {
        resultLayout.removeAllViews();

        for (Object[] serviceData : serviceList) {
            foundServiceElement fElement = new foundServiceElement((Service) serviceData[1], (User) serviceData[2]);

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.resultsMainScreenLayout, fElement);
            transaction.commit();
        }
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
  
    private void attentionBadConnection() {
        Toast.makeText(this, "Плохое соединение", Toast.LENGTH_SHORT).show();
    }
}