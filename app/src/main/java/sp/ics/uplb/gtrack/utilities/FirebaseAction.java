package sp.ics.uplb.gtrack.utilities;

import android.content.Context;

import com.firebase.client.Firebase;

import sp.ics.uplb.gtrack.R;

public class FirebaseAction {

    public static String push(final Context context,String firebaseid,String key,String value) {
        String status = null;
        int MAX_ITERATION = Integer.parseInt(context.getString(R.string.sqltask_max_iter)), iter = 0;
        while (status==null || (status.equals(Constants.ERROR_NETWORK_UNREACHABLE) && (++iter <= MAX_ITERATION))) {
            try {
                Firebase firebase = new Firebase(Constants.FIREBASE_APP + firebaseid);
                firebase.push();
                Firebase child = firebase.child(key);
                child.setValue(value);
                status = Constants.MESSAGE_FIREBASE_PUSH_SUCCESSFUL;
                break;
            } catch (Exception ex) {
                status = Constants.ERROR_NETWORK_UNREACHABLE;
            }
        }
        return status;
    }

    public static Firebase firebasePush(final Context context,String firebaseid,String key,String value) {
        String status = null;
        int MAX_ITERATION = Integer.parseInt(context.getString(R.string.sqltask_max_iter)), iter = 0;
        Firebase firebase = null;
        while (status==null || (status.equals(Constants.ERROR_NETWORK_UNREACHABLE) && (++iter <= MAX_ITERATION))) {
            try {
                firebase = (firebase==null) ? new Firebase(Constants.FIREBASE_APP + firebaseid) : firebase;
                firebase.push();
                Firebase child = firebase.child(key);
                child.setValue(value);
                status = Constants.MESSAGE_FIREBASE_PUSH_SUCCESSFUL;
                break;
            } catch (Exception ex) {
                status = Constants.ERROR_NETWORK_UNREACHABLE;
            }
        }
        return firebase;
    }
}
