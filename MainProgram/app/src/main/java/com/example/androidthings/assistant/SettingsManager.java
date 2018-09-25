package com.example.androidthings.assistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * Version 1.1
 * Created by N on 14/9/2014.
 * Last Edited 25/3/2015
 *   * Support for floats
 * Last Edited 13/5/2015
 *   * Support for syncing data to wearables
 */
public class SettingsManager {
    private static final String TAG = SettingsManager.class.getSimpleName();
    private static final boolean DEBUG = false;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context mContext;

    public SettingsManager(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
        editor = sharedPreferences.edit();
    }

    public boolean has(String key) {
        return sharedPreferences.contains(key);
    }

    public boolean has(int resId) {
        return sharedPreferences.contains(mContext.getString(resId));
    }

    public String getString(int resId) {
        return getString(mContext.getString(resId));
    }
    public String getString(String key) {
        return getString(key, "NULL", "");
    }
    public String getString(int resId, String def) {
        return getString(mContext.getString(resId), def);
    }
    public String getString(String key, String def) {
        return getString(key, "NULL", def);
    }
    public String getString(String key, String val, String def) {
        String result = sharedPreferences.getString(key, val);
        if(result == "NULL") {
            editor.putString(key, def);
            if (DEBUG) {
                Log.d(TAG, key + ", " + def);
            }
            editor.commit();
            result = def;
        }
        return result;
    }

    public String setString(int resId, String val) {
        return setString(mContext.getString(resId), val);
    }

    public String setString(String key, String val) {
        editor.putString(key, val);
        editor.commit();
        return val;
    }

    public boolean getBoolean(int resId) {
        return getBoolean(mContext.getString(resId));
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def) {
        boolean result = sharedPreferences.getBoolean(key, def);
        editor.putBoolean(key, result);
        editor.commit();
        return result;
    }

    public boolean setBoolean(int resId, boolean val) {
        return setBoolean(mContext.getString(resId), val);
    }

    public boolean setBoolean(String key, boolean val) {
        editor.putBoolean(key, val);
        editor.commit();
        return val;
    }

    public float getFloat(int resId, float def) {
        return sharedPreferences.getFloat(mContext.getString(resId), def);
    }

    public float getFloat(String key, float def) {
        return sharedPreferences.getFloat(key, def);
    }

    public int getInt(int resId) {
        return sharedPreferences.getInt(mContext.getString(resId), 0);
    }

    public int setInt(int resId, int val) {
        editor.putInt(mContext.getString(resId), val);
        editor.commit();
        return val;
    }

    public long getLong(int resId) {
        return getLong(resId, 0);
    }

    public long getLong(int resId, long def) {
        return sharedPreferences.getLong(mContext.getString(resId), def);
    }

    public long setLong(int resId, long val) {
        editor.putLong(mContext.getString(resId), val);
        editor.commit();
        return val;
    }

    //Default Stuff
    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return context.getSharedPreferences(getDefaultSharedPreferencesName(context),
                getDefaultSharedPreferencesMode());
    }

    private static String getDefaultSharedPreferencesName(Context context) {
        return context.getPackageName() + "_preferences";
    }

    private static int getDefaultSharedPreferencesMode() {
        return Context.MODE_PRIVATE;
    }
}