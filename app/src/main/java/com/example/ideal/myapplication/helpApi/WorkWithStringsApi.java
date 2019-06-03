package com.example.ideal.myapplication.helpApi;

public class WorkWithStringsApi {

    public WorkWithStringsApi(){
    }
    public String cutString (String text, int limit) {
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

        String convertedDate = year + "-" + month + "-" + day;

        return convertedDate;
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
                return "июнь";
            case "07":
                return "июль";
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
            case "июнь":
                return "06";
            case "июль":
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

}
