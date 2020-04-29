package com.bunbeauty.ideal.myapplication.helpApi;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WorkWithTimeApi {

    private static final String TAG = "DBInf";

    static public long getSysdateLong(){
        //3600000*3 для москвы это +3 часа
        Date sysdate = new Date();
        return sysdate.getTime()+3600000*3;
    }

    static public long getMillisecondsStringDate(String date){
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        try {
            Date orderDate = formatForDateNow.parse(date);
            return orderDate.getTime() + 3600000*3;
        }
        catch (Exception e){
            Log.d(TAG, "getMillisecondsStringDate error: " + e );
        }
        return 0L;
    }

    static public long getMillisecondsStringDateYMD(String date){
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd");
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        try {
            Date orderDate = formatForDateNow.parse(date);
            return orderDate.getTime() + 3600000*3;
        }
        catch (Exception e){
            Log.d(TAG, "getMillisecondsStringDate error: " + e );
        }
        return 0L;
    }

    static public long getMillisecondsStringDateWithSeconds(String date) {

        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        try {
            Date orderDate = formatForDateNow.parse(date);
            return orderDate.getTime() + 3600000*3;
        }
        catch (Exception e){
            Log.d(TAG, "getMillisecondsStringDate error: " + e );
        }
        return 0L;
    }

    //возвращает время в формате yyyy-MM-dd HH:mm:ss
    static public String getDateInFormatYMDHMS(Date date) {
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        return formatForDateNow.format(date);
    }

    //возвращает время в формате MM-dd
    static public String getDateInFormatMD(Long date) {
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("MM-dd");
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        return formatForDateNow.format(date);
    }

    public static Boolean checkPremium(String premiumDate) {
        long premDate = WorkWithTimeApi.getMillisecondsStringDateWithSeconds(premiumDate);
        long sysDate = WorkWithTimeApi.getSysdateLong();

        if (sysDate > premDate) {
            // время вышло
            return false;
        } else {
            // премиумный период
            return true;
        }
    }
}
