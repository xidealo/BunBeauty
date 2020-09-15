package com.bunbeauty.ideal.myapplication.clean_architecture.domain;

/**
 * Use {@link com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.StringExtensionsKt}
 * instead this api
 */
@Deprecated
public class WorkWithStringsApi {

    static public String cutStringWithDots(String text, int limit) {
        if (text.length() > limit) {
            return text.substring(0, limit).trim() + "...";
        } else {
            return text;
        }
    }

    static public String firstCapitalSymbol(String text) {
        if (text.length() > 1) {
            return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        } else {
            return text;
        }
    }

    public static String doubleCapitalSymbols(String text) {
        String[] names = text.split(" ");
        if (names.length > 1) {
            for (int i = 0; i < names.length; i++) {
                names[i] = names[i].substring(0, 1).toUpperCase() + names[i].substring(1).toLowerCase();
            }

            return names[0] + " " + names[1];
        }
        return firstCapitalSymbol(names[0]);
    }
}
