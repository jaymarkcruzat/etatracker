package sp.ics.uplb.gtrack.controllers;

import android.content.Context;

import org.json.JSONObject;

import sp.ics.uplb.gtrack.utilities.DBFunctions;

public class Request {

    public static JSONObject create(Context context,String userId,String recipient,String status) {
        return DBFunctions.sqlInsert(context, "INSERT INTO REQUEST VALUES(null," + userId + "," + recipient + ","+status+")");
    }

    public static JSONObject deleteById(Context context,String id) {
        return DBFunctions.sqlDelete(context, "DELETE FROM REQUEST WHERE ID="+id);
    }

    public static JSONObject setRequestStatusById(Context context,String requestStatus,String id) {
        return DBFunctions.sqlUpdate(context, "UPDATE REQUEST SET REQ_STATUS=" + requestStatus + " WHERE ID=" + id);
    }

    public static JSONObject getPendingRequestsByUserId(Context context,String userId) {
        return DBFunctions.sqlSelect(context, "SELECT DISTINCT req.ID,req.REQ_SENDER,usr.ID USR_ID,usr.USR_NM,usr.USR_EML,usr.USR_FIREBASEID,usr.USR_STATUS,req.REQ_STATUS FROM REQUEST req INNER JOIN USER usr WHERE req.REQ_STATUS=0 AND req.REQ_RECIPIENT=" + userId + " AND usr.ID=req.REQ_SENDER ORDER BY usr.USR_NM");
    }

    public static JSONObject getApprovedRequestsByUserId(Context context,String userId) {
        return DBFunctions.sqlSelect(context, "SELECT DISTINCT req.ID,req.REQ_SENDER,req.REQ_RECIPIENT,usr.ID USR_ID,usr.USR_NM,usr.USR_EML,usr.USR_FIREBASEID,usr.USR_STATUS,req.REQ_STATUS FROM REQUEST req INNER JOIN USER usr WHERE req.REQ_STATUS=1 AND ((req.REQ_RECIPIENT="+userId+" AND usr.ID=req.REQ_SENDER) OR (req.REQ_SENDER="+userId+" AND usr.ID=req.REQ_RECIPIENT)) ORDER BY usr.USR_NM");
    }

    public static JSONObject getDuplicateRequestByUserIdAndRecipient(Context context,String userId,String recipient) {
        return DBFunctions.sqlSelect(context, "SELECT ID FROM REQUEST WHERE (REQ_SENDER=" + userId + " AND REQ_RECIPIENT=" + recipient + ") OR (REQ_SENDER=" + recipient + " AND REQ_RECIPIENT=" + userId + ")");
    }

}
