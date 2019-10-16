package com.bunbeauty.ideal.myapplication.searchService;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.adapters.ServiceAdapter;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User;
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder;
import com.bunbeauty.ideal.myapplication.helpApi.Search;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper;
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

    private static final String NOT_CHOSEN = "не выбран";
    private static final String NAME_OF_SERVICE = "название сервиса";

    private String city = NOT_CHOSEN;
    private String searchBy = NAME_OF_SERVICE;

    private EditText searchLineInput;

    private ArrayList<Service> serviceList;
    private ArrayList<User> userList;
    private RecyclerView recyclerView;
    private ServiceAdapter serviceAdapter;

    private ProgressBar progressBar;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_service);

        TextView findBtn = findViewById(R.id.findServiceSearchServiceText);

        dbHelper = new DBHelper(this);
        FragmentManager manager = getSupportFragmentManager();

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
        progressBar = findViewById(R.id.progressBarSearchService);

        //RV
        serviceList = new ArrayList<>();
        userList = new ArrayList<>();

        recyclerView = findViewById(R.id.resultsSearchServiceRecycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

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
                if(!searchLineInput.getText().toString().toLowerCase().equals("")) {
                    clearArrays();
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    search();
                }
                else {
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    clearArrays();
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

        Query userQuery = FirebaseDatabase.getInstance().getReference(User.USERS)
                .orderByChild(User.CITY)
                .equalTo(userCity);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                ArrayList<Object[]> commonList = search.getServicesOfUsers(usersSnapshot,
                        null,
                        null,
                        null,
                        null,
                        null);
                for (Object[] serviceData : commonList) {
                    serviceList.add((Service) serviceData[1]);
                    userList.add((User) serviceData[2]);
                }
                serviceAdapter = new ServiceAdapter(serviceList.size(),serviceList,userList);
                recyclerView.setAdapter(serviceAdapter);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void search() {
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

        Query usersQuery = FirebaseDatabase.getInstance().getReference(User.USERS);
        if (!city.equals(NOT_CHOSEN)) {
            usersQuery = usersQuery.orderByChild(User.CITY).equalTo(city);
        }

        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                if (usersSnapshot.getValue() == null) {
                    attentionNothingFound();
                    return;
                }

                ArrayList<Object[]> commonList = search.getServicesOfUsers(usersSnapshot,
                        enteredText,
                        null,
                        null,
                        null,
                        null);
                for (Object[] serviceData : commonList) {
                    serviceList.add((Service) serviceData[1]);
                    userList.add((User) serviceData[2]);
                }
                if (commonList.isEmpty()) {
                    attentionNothingFound();
                } else {
                    serviceAdapter = new ServiceAdapter(serviceList.size(),serviceList,userList);
                    recyclerView.setAdapter(serviceAdapter);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
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

        Query userQuery = FirebaseDatabase.getInstance().getReference(User.USERS)
                .orderByChild(User.NAME)
                .equalTo(enteredText);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {

                if(usersSnapshot.getValue() == null) {
                    attentionNothingFound();
                    return;
                }

                ArrayList<Object[]> commonList = search.getServicesOfUsers(usersSnapshot,
                        null,
                        enteredText,
                        city,
                        null,
                        null);
                for (Object[] serviceData : commonList) {
                    serviceList.add((Service) serviceData[1]);
                    userList.add((User) serviceData[2]);
                }
                if (serviceList.isEmpty()) {
                    attentionNothingFound();
                } else {
                    serviceAdapter = new ServiceAdapter(serviceList.size(),serviceList,userList);
                    recyclerView.setAdapter(serviceAdapter);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void clearArrays() {
        serviceList.clear();
        userList.clear();
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
