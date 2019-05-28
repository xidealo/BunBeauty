package com.example.ideal.myapplication.searchService;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.foundElements.foundServiceElement;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.Search;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchService extends AppCompatActivity implements View.OnClickListener {

    // сначала идут константы
    private static final String TAG = "DBInf";

    private static final String USERS = "users";
    private static final String NAME = "name";
    private static final String CITY = "city";

    private static final String NOT_CHOSEN = "не выбран";
    private static final String NAME_OF_SERVICE = "название сервиса";

    private String city = NOT_CHOSEN;
    private String searchBy = NAME_OF_SERVICE;

    private EditText searchLineInput;

    //Вертикальный лэйаут
    private LinearLayout resultLayout;

    private DBHelper dbHelper;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_service);

        TextView findBtn = findViewById(R.id.findServiceSearchServiceText);

        dbHelper = new DBHelper(this);
        manager = getSupportFragmentManager();

        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerSearchServiceLayout);

        //создаём выпадающее меню на основе массива городов
        //Выпадающее меню
        Spinner citySpinner = findViewById(R.id.citySearchServiceSpinner);
        citySpinner.setPrompt(NOT_CHOSEN);

        //создаём выпадающее меню "Поиск по"
        Spinner searchBySpinner = findViewById(R.id.searchBySearchServiceSpinner);
        searchBySpinner.setPrompt(NAME_OF_SERVICE);

        searchLineInput = findViewById(R.id.searchLineSearchServiceInput);
        resultLayout = findViewById(R.id.resultSearchServiceLayout);

        showServicesInHomeTown();

        findBtn.setOnClickListener(this);

        //отслеживаем смену городов в выпадающем меню
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                TextView cityText = (TextView)itemSelected;
                city = cityText.getText().toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        searchBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                TextView searchByText = (TextView)itemSelected;
                searchBy = searchByText.getText().toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.findServiceSearchServiceText:
                resultLayout.removeAllViews();
                if(!searchLineInput.getText().toString().toLowerCase().equals("")) {
                    search();
                }
                else {
                    showServicesInHomeTown();
                }
                break;
            default:
                break;
        }
    }

    private void showServicesInHomeTown() {
        //получаем id пользователя
        String userId = getUserId();

        //получаем город юзера
        String userCity = getUserCity(userId);

        //получаем все сервисы, которые находятся в городе юзера
        getServicesInThisCity(userCity);
    }

    //Получает город пользователя
    private String getUserCity(String userId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Получить город юзера
        // Таблица Users
        // с фиксированным userId
        String sqlQuery =
                "SELECT " + DBHelper.KEY_CITY_USERS
                        + " FROM " + DBHelper.TABLE_CONTACTS_USERS
                        + " WHERE " + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[] {userId});

        int indexCity = cursor.getColumnIndex(DBHelper.KEY_CITY_USERS);
        // дефолтное значение
        String city="dubna";

        if(cursor.moveToFirst()) {
            city = cursor.getString(indexCity);
        }
        cursor.close();
        return city;
    }

    private void getServicesInThisCity(final String userCity) {
        final Search search = new Search(this);

        Query userQuery = FirebaseDatabase.getInstance().getReference(USERS)
                .orderByChild(CITY)
                .equalTo(userCity);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                ArrayList<Object[]> serviceList = search.getServicesOfUsers(usersSnapshot, null, null, null, null);
                addToScreen(serviceList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void search() {
        Log.d(TAG, "search: " + searchBy);
        switch (searchBy) {
            case "название сервиса":
                searchByNameService();
                break;

            case "имя и фамилия":
                searchByWorkerName();
                break;
        }
    }

    private void searchByNameService() {
        final String enteredText = searchLineInput.getText().toString().toLowerCase();
        final Search search = new Search(this);

        Query usersQuery = FirebaseDatabase.getInstance().getReference(USERS);
        if (!city.equals(NOT_CHOSEN)) {
            usersQuery = usersQuery.orderByChild(CITY).equalTo(city);
        }

        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                if (usersSnapshot.getValue() == null) {
                    attentionNothingFound();
                    return;
                }

                ArrayList<Object[]> serviceList = search.getServicesOfUsers(usersSnapshot, enteredText, null,null, null);
                if (serviceList.isEmpty()) {
                    attentionNothingFound();
                } else {
                    addToScreen(serviceList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void searchByWorkerName() {
        final String enteredText = searchLineInput.getText().toString().toLowerCase();
        final Search search = new Search(this);

        Query userQuery = FirebaseDatabase.getInstance().getReference(USERS)
                .orderByChild(NAME)
                .equalTo(enteredText);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {

                if(usersSnapshot.getValue() == null) {
                    attentionNothingFound();
                    return;
                }

                ArrayList<Object[]> serviceList;
                serviceList = search.getServicesOfUsers(usersSnapshot,null, enteredText, city, null);
                Log.d(TAG, "getChildrenCount: " + usersSnapshot.getChildrenCount());
                if (serviceList.isEmpty()) {
                    attentionNothingFound();
                } else {
                    addToScreen(serviceList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    // Вывод фрагмента сервиса на экран
    private void addToScreen(ArrayList<Object[]> serviceList) {
        resultLayout.removeAllViews();

        for (Object[] serviceData : serviceList) {
            foundServiceElement fElement = new foundServiceElement((Service) serviceData[1], (User) serviceData[2]);
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.resultSearchServiceLayout, fElement);
            transaction.commit();
        }
    }
    // Получает id пользователя
    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    private void attentionNothingFound() {
        Toast.makeText(this, "Ничего не найдено", Toast.LENGTH_SHORT).show();
    }

    private void attentionBadConnection() {
        Toast.makeText(this,"Плохое соединение",Toast.LENGTH_SHORT).show();
    }
}
