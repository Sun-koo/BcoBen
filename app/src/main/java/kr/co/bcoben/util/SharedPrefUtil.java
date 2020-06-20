package kr.co.bcoben.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtil {
    private static SharedPreferences preferences;

    public static void init(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        if (preferences == null) return defaultValue;
        return preferences.getBoolean(key, defaultValue);
    }

    public static void putBoolean(String key, boolean Value) {
        if (preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, Value);
        editor.apply();
    }

    public static String getString(String key, String defaultValue) {
        if (preferences == null) return defaultValue;
        return preferences.getString(key, defaultValue);
    }

    public static void putString(String key, String value) {
        if (preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static int getInt(String key, int defaultValue) {
        if (preferences == null) return defaultValue;
        return preferences.getInt(key, defaultValue);
    }

    public static void putInt(String key, int value) {
        if (preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static long getLong(String key, long defaultValue) {
        if (preferences == null) return defaultValue;
        return preferences.getLong(key, defaultValue);
    }

    public static void putLong(String key, long value) {
        if (preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static float getFloat(String key, float defaultValue) {
        if (preferences == null) return defaultValue;
        return preferences.getFloat(key, defaultValue);
    }

    public static void putFloat(String key, float value) {
        if (preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static boolean containsKey(String key) {
        return preferences.contains(key);
    }
}
