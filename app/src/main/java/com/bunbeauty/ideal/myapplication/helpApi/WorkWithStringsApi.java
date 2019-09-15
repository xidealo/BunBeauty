package com.bunbeauty.ideal.myapplication.helpApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkWithStringsApi {

    public WorkWithStringsApi(){
    }
    static public String cutString (String text, int limit) {
        if (text.length() > limit) {
            return text.substring(0, limit).trim() + "...";
        } else {
            return text;
        }
    }

    // Преобразует дату в формат БД
    static public String convertDateToYMD(String dayAndMonth, String year) {
        String[] arrDate = dayAndMonth.split(" ");
        String day = arrDate[0];
        if (day.length() == 1) {
            day = "0" + day;
        }
        String month = monthToNumber(arrDate[1]);

        return year + "-" + month + "-" + day;
    }

    static public String dateTimeToUserFormat(String dateTime) {

        SimpleDateFormat databaseFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

        Date parsingDate = null;
        try {
            parsingDate = databaseFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String month = dateTime.split("-")[1];
        SimpleDateFormat userFormat = new SimpleDateFormat ("dd '" + monthToString(month) + "' HH:mm:ss");
        String userDateTime = userFormat.format(parsingDate);

        if(userDateTime.charAt(0)=='0') {
            userDateTime = userDateTime.substring(1);
        }

        return userDateTime;
    }

    static public String dateToUserFormat(String date) {

        SimpleDateFormat databaseFormat = new SimpleDateFormat ("yyyy-MM-dd");

        Date parsingDate = null;
        try {
            parsingDate = databaseFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String month = date.split("-")[1];
        SimpleDateFormat userFormat = new SimpleDateFormat ("dd '" + monthToString(month) + "'");
        String userDate = userFormat.format(parsingDate);

        if(userDate.charAt(0)=='0') {
            userDate = userDate.substring(1);
        }

        return userDate;
    }

    static public String monthToString(String month) {
        switch (month) {
            case "01":
                return "янв";
            case "02":
                return "фев";
            case "03":
                return "мар";
            case "04":
                return "апр";
            case "05":
                return "май";
            case "06":
                return "июня";
            case "07":
                return "июля";
            case "08":
                return "авг";
            case "09":
                return "сен";
            case "10":
                return "окт";
            case "11":
                return "ноя";
            case "12":
                return "дек";
        }
        return "";
    }

    static private String monthToNumber(String month) {
        switch (month) {
            case "янв":
                return "01";
            case "фев":
                return "02";
            case "мар":
                return "03";
            case "апр":
                return "04";
            case "май":
                return "05";
            case "июня":
                return "06";
            case "июля":
                return "07";
            case "авг":
                return "08";
            case "сен":
                return "09";
            case "окт":
                return "10";
            case "ноя":
                return "11";
            case "дек":
                return "12";
        }
        return "";
    }

    static public String firstCapitalSymbol(String text){
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
    public static String doubleCapitalSymbols(String text){
        String[] names = text.split(" ");
        if (names.length >1) {
            for (int i = 0; i < names.length; i++) {
                names[i] = names[i].substring(0, 1).toUpperCase() + names[i].substring(1);
            }

            return names[0] + " " + names[1];
        }
        return firstCapitalSymbol(names[0]);
    }
}
