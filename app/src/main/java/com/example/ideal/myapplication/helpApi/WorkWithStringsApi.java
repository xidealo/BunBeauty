package com.example.ideal.myapplication.helpApi;

public class WorkWithStringsApi {

    public String cutString (String text, int limit) {
        if (text.length() > limit) {
            return text.substring(0, limit) + "...";
        } else {
            return text;
        }
    }

}
