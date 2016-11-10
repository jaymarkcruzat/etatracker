package sp.ics.uplb.gtrack.controllers;

import android.content.Context;

import org.json.JSONObject;

import sp.ics.uplb.gtrack.utilities.DBFunctions;

public class Markers {

    public static JSONObject create(Context context,String title,String description,double latitude,double longitude,String target,String userId) {
        return DBFunctions.sqlInsert(context, "INSERT INTO MARKER VALUES(null,'" + title + "','" + description + "',GeomFromText( ' POINT(" + latitude + " " + longitude + ") ' ),"+target+"," + userId + ")");
    }

    public static JSONObject getAllByUserId(Context context,String userId) {
        return DBFunctions.sqlSelect(context, "SELECT ID,MKR_TITLE,MKR_DESC,MKR_TARGET,AsText(MKR_LATLNG) MKR_LATLNG,MKR_TARGET,USER_ID FROM MARKER WHERE USER_ID=" + userId);
    }

    public static JSONObject getIdByTitleAndUserId(Context context,String title,String userId) {
        return DBFunctions.sqlSelect(context, "SELECT ID,MKR_TITLE,MKR_DESC,AsText(MKR_LATLNG) MKR_LATLNG,MKR_TARGET,USER_ID FROM MARKER WHERE MKR_TITLE='"+title+"' AND USER_ID="+userId);
    }

    public static JSONObject deleteMarkerByTitleAndUserId(Context context,String title,String userId) {
        return DBFunctions.sqlDelete(context, "DELETE FROM MARKER WHERE MKR_TITLE='" + title + "' AND USER_ID=" + userId);
    }

    public static JSONObject setTitleAndDescriptionByTitleAndUserId(Context context,String newTitle,String newDescription,String title,String userId) {
        return DBFunctions.sqlUpdate(context, "UPDATE MARKER SET MKR_TITLE='" + newTitle + "', MKR_DESC='" + newDescription + "' WHERE MKR_TITLE='" + title + "' AND USER_ID=" + userId);
    }

    public static JSONObject setTargetByTitleAndUserId(Context context,String target,String title,String userId) {
        return DBFunctions.sqlUpdate(context, "UPDATE MARKER SET MKR_TARGET=" + target + " WHERE MKR_TITLE='" + title + "' AND USER_ID=" + userId);
    }

    public static JSONObject setLatlngByTitleAndUserId(Context context, double latitude, double longitude,String title,String userId) {
        return DBFunctions.sqlUpdate(context, "UPDATE MARKER SET MKR_LATLNG=GeomFromText( ' POINT("+ latitude + " " + longitude +") ' ) WHERE MKR_TITLE='"+title+"' AND USER_ID="+userId);
    }

}
