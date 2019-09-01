package com.example.ideal.myapplication.searchService;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.adapters.ServiceAdapter;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.Search;
import com.example.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MainScreen extends AppCompatActivity implements View.OnClickListener {

    // добавить, чтобы не было видно своих сервисов
    // например номер юзера, возвращаемого сервиса не должен быть равен локальному
    private static final String TAG = "DBInf";

    private static final String USERS = "users";
    private static final String CITY = "city";

    private DBHelper dbHelper;
    private FragmentManager manager;
    private Search search;
  
    private Button [] categoriesBtns;
    private ArrayList<String> categories;
    private ArrayList<String> selectedTagsArray;
    private LinearLayout categoryLayout;
    private LinearLayout tagsLayout;
    private LinearLayout innerLayout;
    private ProgressBar progressBar;

    private ArrayList<Service> serviceList;
    private ArrayList<User> userList;
    private RecyclerView recyclerView;
    private ServiceAdapter serviceAdapter;
    private boolean isUpdated;
    private String category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        init();
    }

    private void init() {
        dbHelper = new DBHelper(this);
        manager = getSupportFragmentManager();
        search = new Search(this);
        serviceList = new ArrayList<>();
        userList = new ArrayList<>();
        isUpdated = true;
        categories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.categories)));
        selectedTagsArray = new ArrayList<>();
        categoriesBtns = new Button[categories.size()];

        categoryLayout = findViewById(R.id.categoryMainScreenLayout);
        recyclerView = findViewById(R.id.resultsMainScreenRecycleView);
        categoryLayout = findViewById(R.id.categoryMainScreenLayout);
        tagsLayout = findViewById(R.id.tagsMainScreenLayout);
        innerLayout = findViewById(R.id.tagsInnerMainScreenLayout);
        progressBar = findViewById(R.id.progressBarMainScreen);
        Button minimizeTagsBtn = findViewById(R.id.minimizeTagsMainScreenBtn);
        Button clearTagsBtn = findViewById(R.id.clearTagsMainScreenBtn);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if ((recyclerView.computeVerticalScrollOffset() == 0) && !isUpdated) //check for scroll down
                {
                    serviceList.clear();
                    userList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
        minimizeTagsBtn.setOnClickListener(this);
        clearTagsBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        createCategoryFeed();
        createMainScreen();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.minimizeTagsMainScreenBtn:
                hideTags();
                break;

            case R.id.clearTagsMainScreenBtn:
                startLoading();
                for(Button btn : categoriesBtns) {
                    if (category.equals(btn.getText().toString())) {
                        clearCategory(btn);
                        break;
                    }
                }
                createMainScreen();
                break;

            default:
                if (((View) v.getParent()).getId() == R.id.categoryMainScreenLayout) {
                    categoriesClick((Button) v);
                } else {
                    tagClick((TextView) v);
                }
                break;
        }
    }

    private void tagClick(TextView tagText) {
        startLoading();

        String text = tagText.getText().toString();
        if (selectedTagsArray.contains(text)) {
            tagText.setBackgroundResource(0);
            tagText.setTextColor(Color.GRAY);
            selectedTagsArray.remove(text);
        } else {
            tagText.setBackgroundResource(R.drawable.category_button_pressed);
            tagText.setTextColor(Color.BLACK);
            selectedTagsArray.add(text);
        }

        createMainScreen();
    }

    private void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        serviceList.clear();
        userList.clear();
    }

    private void categoriesClick(Button btn ) {
        // Если категория уже выбрана
        if (category.equals(btn.getText().toString())) {
            if (tagsLayout.getVisibility() == View.VISIBLE) {
                hideTags();
            } else {
                showTags();
            }
        } else {
            startLoading();
            enableCategory(btn);
            createMainScreen();
        }
    }

    private void clearCategory(Button btn) {
        Log.d(TAG, "clearCategory: ");
        disableCategoryBtn(btn);
        category = "";
        hideTags();
        selectedTagsArray.clear();
    }

    private void hideTags() {
        innerLayout.removeAllViews();
        tagsLayout.setVisibility(View.GONE);
    }

    private void enableCategory(Button button) {
        hideTags();
        button.setBackgroundResource(R.drawable.category_button_pressed);
        button.setTextColor(getResources().getColor(R.color.black));

        for (Button categoriesBtn : categoriesBtns) {
            if (category.equals(categoriesBtn.getText().toString())) {
                disableCategoryBtn(categoriesBtn);
                break;
            }
        }
        selectedTagsArray.clear();
        category = button.getText().toString();
        showTags();
    }

    private void disableCategoryBtn(Button button) {
        button.setBackgroundResource(R.drawable.category_button);
        button.setTextColor(getResources().getColor(R.color.white));
    }

    // настроить вид кнопок
    private  void createCategoryFeed() {
        int width = getResources().getDimensionPixelSize(R.dimen.categories_width);
        int height = getResources().getDimensionPixelSize(R.dimen.categories_height);
        for(int i =0 ; i<categoriesBtns.length; i++){
            categoriesBtns[i] = new Button(this);
            categoriesBtns[i].setOnClickListener(this);
            categoriesBtns[i].setText(categories.get(i));
            categoriesBtns[i].setTextSize(14);
            disableCategoryBtn(categoriesBtns[i]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                categoriesBtns[i].setAutoSizeTextTypeUniformWithConfiguration(
                        8, 14, 1, TypedValue.COMPLEX_UNIT_DIP);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int)(width*categories.get(i).length()/6.6),
                    height);
            params.setMargins(10,10,10,16);
            categoriesBtns[i].setLayoutParams(params);

            categoryLayout.addView(categoriesBtns[i]);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerMainScreenLayout);
        panelBuilder.buildHeader(manager, "Главная", R.id.headerMainScreenLayout);
    }

    private void createMainScreen() {
        //получаем id пользователя
        String userId = getUserId();

        //получаем город юзера
        String userCity = getUserCity(userId);

        //получаем все сервисы, которые находятся в городе юзера
        getServicesInThisCity(userCity, category, selectedTagsArray);
    }

    private String getUserCity(String userId) {

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

    private void getServicesInThisCity(final String userCity, final String category, final ArrayList<String> selectedTagsArray) {

        //возвращение всех пользователей из контретного города
        Query userQuery = FirebaseDatabase.getInstance().getReference(USERS)
                .orderByChild(CITY)
                .equalTo(userCity);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {

                ArrayList<Object[]> commonList = search.getServicesOfUsers(usersSnapshot,
                        null,
                        null,
                        null,
                        category,
                        selectedTagsArray);
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

    private void showTags() {
        CharSequence[] tagsArray = getResources()
                .obtainTypedArray(R.array.tags_references)
                .getTextArray(categories.indexOf(category));

        for (CharSequence tag : tagsArray) {
            TextView tagText = new TextView(this);
            tagText.setText(tag.toString());
            tagText.setTextColor(Color.GRAY);
            tagText.setGravity(Gravity.CENTER);
            tagText.setTypeface(ResourcesCompat.getFont(this, R.font.roboto_bold));
            tagText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tagText.setOnClickListener(this);
            tagText.setPadding(0, 16, 0, 16);
            if (selectedTagsArray.contains(tag.toString())) {
                tagText.setBackgroundResource(R.drawable.category_button_pressed);
                tagText.setTextColor(Color.BLACK);
            }

            innerLayout.addView(tagText);
        }

        tagsLayout.setVisibility(View.VISIBLE);
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
  
    private void attentionBadConnection() {
        Toast.makeText(this, "Плохое соединение", Toast.LENGTH_SHORT).show();
    }
}
