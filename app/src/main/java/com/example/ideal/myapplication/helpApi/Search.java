package com.example.ideal.myapplication.helpApi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.searchService.GuestService;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Search {

    private static final String TAG = "DBInf";

    private static final String NOT_CHOSEN = "не выбран";
    private static final String CITY = "city";
    private static final String SERVICES = "services";
    private static final String MAX_COST = "max_cost";
    private static final String SERVICE_ID = "servise_id";
    private static final String CREATION_DATE = "creation date";
    private static final String COST = "cost";
    private static final String RATING = "rating";

    private long maxCost;
    private ArrayList<Object[]> serviceList;
    private ArrayList<Object[]> premiumList;

    private WorkWithTimeApi workWithTimeApi;
    private DBHelper dbHelper;

    public Search (Context _context) {
        dbHelper = new DBHelper(_context);
        serviceList = new ArrayList<>();
        premiumList = new ArrayList<>();
        workWithTimeApi = new WorkWithTimeApi();
    }

    // загружаем услуги мастеров мастеров в LocalStorage
    public ArrayList<Object[]> getServicesOfUsers(DataSnapshot usersSnapshot, String serviceName, String userName,
                                                  String city, String category, ArrayList<String> selectedTagsArray) {
        serviceList.clear();
        premiumList.clear();
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
            String userCity = String.valueOf(userSnapshot.child(CITY).getValue());
            if((city == null) || city.equals(userCity) || city.equals(NOT_CHOSEN)) {
                //загрузка данных для элементов MS
                LoadingUserElementData.loadUserNameAndPhotoWithCity(userSnapshot, database);
                LoadingMainScreenElement.loadService(userSnapshot.child(SERVICES), userSnapshot.getKey(), database);
            }
        }

        updateServicesList(serviceName, userName, city, category, selectedTagsArray);
        choosePremiumServices();
        return serviceList;
    }

    // Кладём услуги мастеров в список
    private void updateServicesList(String sName, String name, String city,
                                    String category, ArrayList<String> selectedTagsArray) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        maxCost = getMaxCost();

        String serviceNameCondition = "";
        if (sName != null) {
            serviceNameCondition = " AND " + DBHelper.KEY_NAME_SERVICES + " = '" + sName + "' ";
        }

        String cityCondition = "";
        if (city != null && !city.equals(NOT_CHOSEN)) {
            cityCondition = " AND " + DBHelper.KEY_CITY_USERS + " = '" + city + "' ";
        }

        String categoryCondition = "";
        if (category != null && !category.equals("")) {
            categoryCondition = " AND " + DBHelper.KEY_CATEGORY_SERVICES + " = '" + category + "' ";
        }

        String userNameCondition = "";
        if (name != null) {
            userNameCondition = " AND " + DBHelper.KEY_NAME_USERS + " = '" + name + "' ";
        }

        String tagsCondition = "";
        if (selectedTagsArray != null && !selectedTagsArray.isEmpty()) {
            tagsCondition = " AND (";
            for (String tag : selectedTagsArray) {
                tagsCondition += DBHelper.KEY_TAG_TAGS + " = '" + tag + "' OR ";
            }
            tagsCondition = tagsCondition.substring(0, tagsCondition.length() - 4) + ") ";
        }

        // Возвращает id, название, рэйтинг и количество оценивших
        // используем таблицу сервисы
        // уточняем юзера по его id
        String sqlQuery =
                "SELECT DISTINCT " + DBHelper.TABLE_CONTACTS_USERS + ".*, "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ".*, "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " AS " + SERVICE_ID
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_USERS + ", "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_TAGS
                        + " WHERE "
                        + DBHelper.KEY_USER_ID + " = "
                        + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID
                        + " AND " + DBHelper.KEY_SERVISE_ID_TAGS + " = "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                        + serviceNameCondition
                        + cityCondition
                        + categoryCondition
                        + userNameCondition
                        + tagsCondition;

        Log.d(TAG, "sqlQuery: " + sqlQuery);

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{});
        if(cursor.moveToFirst()) {
            int indexUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
            int indexUserName = cursor.getColumnIndex(DBHelper.KEY_NAME_USERS);
            int indexUserPhone = cursor.getColumnIndex(DBHelper.KEY_PHONE_USERS);
            int indexUserCity = cursor.getColumnIndex(DBHelper.KEY_CITY_USERS);

            int indexServiceId = cursor.getColumnIndex(SERVICE_ID);
            int indexServiceName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexServiceCost = cursor.getColumnIndex(DBHelper.KEY_MIN_COST_SERVICES);
            int indexServiceCreationDate = cursor.getColumnIndex(DBHelper.KEY_CREATION_DATE_SERVICES);
            int indexServiceIsPremium = cursor.getColumnIndex(DBHelper.KEY_IS_PREMIUM_SERVICES);
            int indexServiceAvgRating = cursor.getColumnIndex(DBHelper.KEY_RATING_SERVICES);

            do {
                // Информация о мастере
                String userId = cursor.getString(indexUserId);
                String userName = cursor.getString(indexUserName);
                String userPhone = cursor.getString(indexUserPhone);
                String userCity = cursor.getString(indexUserCity);

                User user = new User();
                user.setId(userId);
                user.setName(userName);
                user.setPhone(userPhone);
                user.setCity(userCity);

                // Информация об услуге
                String serviceId = cursor.getString(indexServiceId);
                String serviceName = cursor.getString(indexServiceName);
                String serviceCost = cursor.getString(indexServiceCost);
                float serviceRating = Float.valueOf(cursor.getString(indexServiceAvgRating));

                boolean isPremium = GuestService.checkPremium(cursor.getString(indexServiceIsPremium));
                String creationDate = cursor.getString(indexServiceCreationDate);

                Service service = new Service();
                service.setId(serviceId);
                service.setName(serviceName);
                service.setCost(serviceCost);
                service.setIsPremium(isPremium);
                service.setCreationDate(creationDate);
                service.setAverageRating(serviceRating);

                addToServiceList(service, user);
                //пока в курсоре есть строки и есть новые сервисы
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void choosePremiumServices() {
        final Random random = new Random();
        int limit = 3;

        if (premiumList.size() <= limit) {
            serviceList.addAll(0,premiumList);
        } else {
            for (int i = 0; i < limit; i++) {
                Object[] premiumService;
                do {
                    int index = random.nextInt(premiumList.size());
                    premiumService = premiumList.get(index);
                } while (serviceList.contains(premiumService));
                serviceList.add(0, premiumService);
            }
        }
    }

    private long getMaxCost() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sqlQuery =
                "SELECT "
                        + " MAX(CAST(" + DBHelper.KEY_MIN_COST_SERVICES + " AS INTEGER)) AS " + MAX_COST
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES;

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{});

        long maxCost = 0;
        if (cursor.moveToFirst()) {
            maxCost = cursor.getInt(cursor.getColumnIndex(MAX_COST));
        }

        cursor.close();
        return maxCost;
    }

    // Добавляем конкретную услугу в список в сообветствии с её коэфициентом
    private void addToServiceList(Service service, User user) {
        HashMap<String, Float> coefficients = new HashMap<>();
        coefficients.put(CREATION_DATE, 0.25f);
        coefficients.put(COST, 0.07f);
        coefficients.put(RATING, 0.68f);
        float points, creationDatePoints, costPoints, ratingPoints, penaltyPoints;

        boolean isPremium = service.getIsPremium();

        if (isPremium) {
            points = 1;
            premiumList.add(0, new Object[]{points, service, user});
        } else {
            creationDatePoints = figureCreationDatePoints(service.getCreationDate(), coefficients.get(CREATION_DATE));
            costPoints = figureCostPoints(Long.valueOf(service.getCost()), coefficients.get(COST));
            ratingPoints = figureRatingPoints(service.getAverageRating(), coefficients.get(RATING));
            //penaltyPoints = figurePenaltyPoints(service.getId(), user.getId());
            points = creationDatePoints + costPoints + ratingPoints/* - penaltyPoints*/;
            sortAddition(new Object[]{points, service, user});
        }
    }

    private float figureCreationDatePoints(String creationDate, float coefficient) {
        float creationDatePoints;

        long dateBonus = (workWithTimeApi.getMillisecondsStringDate(creationDate) -
                WorkWithTimeApi.getSysdateLong()) / (3600000*24) + 7;
        if (dateBonus < 0) {
            creationDatePoints = 0;
        } else {
            creationDatePoints = dateBonus * coefficient / 7;
        }

        return creationDatePoints;
    }

    private float figureCostPoints(long cost, float coefficient) {
        return (1 - cost * 1f / maxCost) * coefficient;
    }

    private float figureRatingPoints(float rating, float coefficient) {
        return rating * coefficient / 5;
    }

    private float figurePenaltyPoints(String serviceId, String userId) {
        float penaltyPoints = 0;

        if (!WorkWithLocalStorageApi.hasAvailableTime(serviceId, userId, dbHelper.getReadableDatabase())) {
            penaltyPoints = 0.3f;
        }

        return penaltyPoints;
    }


    private void sortAddition(Object[] serviceData) {
        for (int i = 0; i < serviceList.size(); i++) {
            if ((float)(serviceList.get(i)[0]) < (float)(serviceData[0])) {
                serviceList.add(i, serviceData);
                return;
            }
        }

        serviceList.add(serviceList.size(), serviceData);
    }

}
