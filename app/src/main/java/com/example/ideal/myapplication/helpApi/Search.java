package com.example.ideal.myapplication.helpApi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class Search {

    private static final String TAG = "DBInf";

    private static final String NAME = "name";
    private static final String PHONE = "phone";
    private static final String CITY = "city";
    private static final String REVIEW_FOR_SERVICE = "review for service";
    private static final String ORDER_ID = "order_id";

    private static final String SERVICES = "services";
    private static final String MAX_COST = "max_cost";

    private long maxCost;
    private ArrayList<Object[]> serviceList;

    private WorkWithTimeApi workWithTimeApi;
    private DBHelper dbHelper;

    public Search (Context _context) {
        dbHelper = new DBHelper(_context);
        serviceList = new ArrayList<>();
        workWithTimeApi = new WorkWithTimeApi();
    }

    // получаем услуги мастеров
    public ArrayList<Object[]> getServicesOfUsers(DataSnapshot usersSnapshot, String serviceName, String city) {
        serviceList.clear();
        maxCost = getMaxCost();
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
            String userCity = String.valueOf(userSnapshot.child(CITY).getValue());

            // Создаём условие на город
            boolean cityCondition = true;
            if (city != null) {
                cityCondition = city.equals(userCity);
            }

            if(cityCondition) {
                String userName = String.valueOf(userSnapshot.child(NAME).getValue());
                String userPhone = String.valueOf(userSnapshot.child(PHONE).getValue());
                String userId = userSnapshot.getKey();

                User user = new User();
                user.setId(userId);
                user.setName(userName);
                user.setPhone(userPhone);
                user.setCity(userCity);

                DownloadServiceData downloadServiceData = new DownloadServiceData(database);
                downloadServiceData.loadUserInfo(userSnapshot);
                downloadServiceData.loadSchedule(userSnapshot.child(SERVICES), userId);

                updateServicesList(user, serviceName);
            }
        }

        return serviceList;
    }

    private long getMaxCost() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sqlQuery =
                "SELECT "
                        + " MAX(" + DBHelper.KEY_MIN_COST_SERVICES + ") AS " + MAX_COST
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES;

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{});

        long maxCost = 0;
        if (cursor.moveToFirst()) {
            maxCost = Long.valueOf(cursor.getString(cursor.getColumnIndex(MAX_COST)));
        }

        return maxCost;
    }

    private void updateServicesList(User user, String _serviceName) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String additionalCondition = "";
        if (_serviceName != null) {
            additionalCondition = " AND " + DBHelper.KEY_NAME_SERVICES + " = '" + _serviceName + "'";
        }
        // Возвращает id, название, рэйтинг и количество оценивших
        // используем таблицу сервисы
        // уточняем юзера по его id
        String sqlQuery =
                "SELECT * FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE "
                        + DBHelper.KEY_USER_ID + " = ? "
                        + additionalCondition;

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{user.getId()});
        //Идём с конца
        if(cursor.moveToFirst()){
            int indexServiceId = cursor.getColumnIndex(DBHelper.KEY_ID);
            int indexServiceName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexServiceCost = cursor.getColumnIndex(DBHelper.KEY_MIN_COST_SERVICES);
            int indexServiceCreationDate = cursor.getColumnIndex(DBHelper.KEY_CREATION_DATE_SERVICES);
            int indexServiceIsPremium = cursor.getColumnIndex(DBHelper.KEY_IS_PREMIUM_SERVICES);

            do {
                String serviceId = cursor.getString(indexServiceId);
                String serviceName = cursor.getString(indexServiceName);
                String serviceCost = cursor.getString(indexServiceCost);

                boolean isPremium = Boolean.valueOf(cursor.getString(indexServiceIsPremium));
                String creationDate = cursor.getString(indexServiceCreationDate);

                Service service = new Service();
                service.setId(serviceId);
                service.setName(serviceName);
                service.setCost(serviceCost);
                service.setIsPremium(isPremium);
                service.setCreationDate(creationDate);
                service.setAverageRating(figureAverageRating(serviceId));

                addToServiceList(service, user);
                //пока в курсоре есть строки и есть новые сервисы
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void addToServiceList(Service service, User user) {
        HashMap<String, Float> coefs = new HashMap<>();
        coefs.put("creation date", 0.25f);
        coefs.put("cost", 0.07f);
        coefs.put("rating", 0.68f);

        float points, creationDatePoints, costPoints, ratingPoints;

        boolean isPremium = service.getIsPremium();
        if (isPremium) {
            points = 1;
            serviceList.add(0, new Object[]{points, service, user});
        } else {
            creationDatePoints = figureCreationDatePoints(service.getCreationDate(), coefs.get("creation date"));
            costPoints = figureCostPoints(Long.valueOf(service.getCost()), coefs.get("cost"));
            ratingPoints = figureRatingPoints(service.getAverageRating(), coefs.get("rating"));
            points = creationDatePoints + costPoints + ratingPoints;
            sortAddition(new Object[]{points, service, user});
        }
    }

    private float figureCreationDatePoints(String creationDate, float coefficient) {
        float creationDatePoints;

        long dateBonus = (workWithTimeApi.getMillisecondsStringDate(creationDate) -
                workWithTimeApi.getSysdateLong()) / (3600000*24) + 7;
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

    private void sortAddition(Object[] serviceData) {
        for (int i = 0; i < serviceList.size(); i++) {
            if ((float)(serviceList.get(i)[0]) < (float)(serviceData[0])) {
                serviceList.add(i, serviceData);
                return;
            }
        }

        serviceList.add(serviceList.size(), serviceData);
    }

    private float figureAverageRating (String serviceId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sqlQuery =
                "SELECT "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + ", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID + " AS " + ORDER_ID + ", "
                        + DBHelper.KEY_RATING_REVIEWS
                        + " FROM "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_WORKING_TIME + ", "
                        + DBHelper.TABLE_ORDERS + ", "
                        + DBHelper.TABLE_REVIEWS
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
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ? "
                        + " AND "
                        + DBHelper.KEY_TYPE_REVIEWS + " = ? "
                        + " AND "
                        + DBHelper.KEY_RATING_REVIEWS + " != 0 "
                        + " AND "
                        + DBHelper.KEY_REVIEW_REVIEWS + " != '-'";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId, REVIEW_FOR_SERVICE});
        int countOfRates = 0;
        float avgRating  = 0;
        float sumOfRates = 0;
        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        if (cursor.moveToFirst()){
            int indexRating = cursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
            int indexWorkingTimeId= cursor.getColumnIndex(DBHelper.KEY_ID);
            int indexOrderId= cursor.getColumnIndex(ORDER_ID);
            do {
                String workingTimeId = cursor.getString(indexWorkingTimeId);
                String orderId = cursor.getString(indexOrderId);

                if(workWithLocalStorageApi.isMutualReview(orderId)) {
                    sumOfRates += Float.valueOf(cursor.getString(indexRating));
                    countOfRates++;
                }
                else {
                    if(workWithLocalStorageApi.isAfterThreeDays(workingTimeId)){
                        sumOfRates += Float.valueOf(cursor.getString(indexRating));
                        countOfRates++;
                    }
                }
            }while (cursor.moveToNext());

            if(countOfRates!=0){
                avgRating = sumOfRates / countOfRates;
            }
            cursor.close();
        }

        return avgRating;
    }
}
