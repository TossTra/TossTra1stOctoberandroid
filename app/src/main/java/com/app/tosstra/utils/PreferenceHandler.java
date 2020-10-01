package com.app.tosstra.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Method;

public class PreferenceHandler {

    public static final String USER_ID = "user_id";
    public static final String TOKEN = "token";
    private static final int MODE = Context.MODE_PRIVATE;
    private static final String PREF_NAME = "TossTra_APP_PREFS";


    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    // String
    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();
    }


    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    public static void clearPref(Context context) {
        if (context != null) {
            if (getPreferences(context) != null)
                getEditor(context).clear().commit();
        }
    }
}
