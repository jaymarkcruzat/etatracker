package sp.ics.uplb.gtrack.controllers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    public static String getString(Context context,String preferenceName,String key,String defaultValue) {
        return context.getSharedPreferences(preferenceName, context.MODE_PRIVATE).getString(key, defaultValue);
    }

    public static void setString(Context context,String preferenceName,String key,String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(preferenceName, context.MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }

}
