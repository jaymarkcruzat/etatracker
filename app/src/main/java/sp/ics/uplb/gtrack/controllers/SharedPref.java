package sp.ics.uplb.gtrack.controllers;

import android.content.Context;
import android.content.SharedPreferences;

import sp.ics.uplb.gtrack.utilities.Constants;
import sp.ics.uplb.gtrack.utilities.Logger;

public class SharedPref {
    public static SharedPreferences preferences = null;

    public static SharedPreferences getInstance(Context context) {
        if (preferences==null) {
            preferences = context.getSharedPreferences(Constants.SHARED_PREF, context.MODE_PRIVATE);
        }
        return preferences;
    }

    public static String getString(Context context,String preferenceName,String key,String defaultValue) {
        return context.getSharedPreferences(preferenceName, context.MODE_PRIVATE).getString(key, defaultValue);
    }

    public static void setString(Context context,String preferenceName,String key,String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(preferenceName, context.MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }

}
