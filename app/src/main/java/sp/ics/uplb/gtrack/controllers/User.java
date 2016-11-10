package sp.ics.uplb.gtrack.controllers;

import android.content.Context;

import org.json.JSONObject;

import sp.ics.uplb.gtrack.utilities.DBFunctions;

public class User {

    public static JSONObject getIdByUserName(Context context,String userName) {
        return DBFunctions.sqlSelect(context,"SELECT ID FROM USER WHERE USR_EML='"+userName+"'");
    }

    public static JSONObject getAllByUserName(Context context,String userName) {
        return DBFunctions.sqlSelect(context,"SELECT * FROM USER WHERE USR_EML='"+userName+"'");
    }

    public static JSONObject setUserFirebaseIdAndDeviceIdById(Context context,String firebaseId,String deviceId,String id) {
        return DBFunctions.sqlUpdate(context, "UPDATE USER SET USR_FIREBASEID='" + firebaseId + "', USR_DEVICEID='"+deviceId+"' WHERE ID=" + id);
    }

    public static JSONObject getFirebaseIdByUserName(Context context,String userName) {
        return DBFunctions.sqlSelect(context, "SELECT USR_FIREBASEID FROM USER WHERE USR_EML='" + userName + "'");
    }

    public static JSONObject getIdAndFirebaseIdByUserName(Context context,String userName) {
        return DBFunctions.sqlSelect(context, "SELECT ID,USR_FIREBASEID FROM USER WHERE USR_EML='" + userName + "'");
    }

    public static JSONObject setUserStatusByUserName(Context context,String status,String userName) {
        return DBFunctions.sqlUpdate(context, "UPDATE USER SET USR_STATUS="+status+" WHERE USR_EML='"+userName+"'");
    }

}
