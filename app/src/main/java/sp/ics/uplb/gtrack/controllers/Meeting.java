package sp.ics.uplb.gtrack.controllers;

import android.content.Context;

import org.json.JSONObject;

import sp.ics.uplb.gtrack.utilities.DBFunctions;

public class Meeting {

    public static JSONObject create(Context context,String markerId,String participant,String status) {
        return DBFunctions.sqlInsert(context, "INSERT INTO MEETING VALUES(null," + markerId + "," + participant + "," + status + ")");
    }

    public static JSONObject getIdByMarkerIdAndParticipant(Context context,String markerId,String participant) {
        return DBFunctions.sqlSelect(context, "SELECT ID FROM MEETING WHERE MARKER_ID=" + markerId + " AND MTG_PARTICIPANT=" + participant);
    }

    public static JSONObject getMarkersByParticipant(Context context, String participant) {
        return DBFunctions.sqlSelect(context, "SELECT * FROM MEETING mtg INNER JOIN MARKER mkr WHERE mtg.MTG_PARTICIPANT="+participant+" AND mtg.MARKER_ID=mkr.ID");
    }

    public static JSONObject getParticipantsByMarkerId(Context context, String markerId) {
        return DBFunctions.sqlSelect(context, "SELECT usr.ID,usr.USR_EML,usr.USR_FIREBASEID FROM MEETING mtg INNER JOIN USER usr WHERE MARKER_ID="+markerId+" AND mtg.MTG_PARTICIPANT=usr.ID");
    }

}
