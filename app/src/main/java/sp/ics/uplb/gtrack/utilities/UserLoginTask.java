package sp.ics.uplb.gtrack.utilities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import sp.ics.uplb.gtrack.R;
import sp.ics.uplb.gtrack.activities.LoginActivity;
import sp.ics.uplb.gtrack.activities.MainActivity;
import sp.ics.uplb.gtrack.controllers.User;

public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
    private LoginActivity loginActivity;
    private String userName;
    private String userCode;
    private String email;
    private String firebaseid;
    private String deviceId;
    private String password;
    public String status;

    public UserLoginTask(LoginActivity loginActivity, String email, String password) {
        this.loginActivity = loginActivity;
        this.email = email;
        this.password = password;
        this.deviceId = getDeviceId();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String link, data = null;

            link=loginActivity.getString(R.string.server_protocol) + loginActivity.getString(R.string.server_ip) + "/" + loginActivity.getString(R.string.app_name) + "/" + loginActivity.getString(R.string.php_index);
            try {
                data = URLEncoder.encode(Constants.HTTP_POST_USERNAME,   loginActivity.getString(R.string.encrypt_encoding))  + "=" + URLEncoder.encode(email.split("@")[0], loginActivity.getString(R.string.encrypt_encoding)) +"&"+
                       URLEncoder.encode(Constants.HTTP_POST_PASSWORD,   loginActivity.getString(R.string.encrypt_encoding))  + "=" + URLEncoder.encode(loginActivity.encrypt(password), loginActivity.getString(R.string.encrypt_encoding)) +"&"+
                       URLEncoder.encode(Constants.HTTP_POST_EMAIL,      loginActivity.getString(R.string.encrypt_encoding))  + "=" + URLEncoder.encode(email, loginActivity.getString(R.string.encrypt_encoding)) +"&"+
                       URLEncoder.encode(Constants.HTTP_POST_DEVICEID,   loginActivity.getString(R.string.encrypt_encoding))  + "=" + URLEncoder.encode(deviceId, loginActivity.getString(R.string.encrypt_encoding)) +"&"+
                       URLEncoder.encode(Constants.HTTP_POST_DBHOST,     loginActivity.getString(R.string.encrypt_encoding))  + "=" + URLEncoder.encode(loginActivity.getString(R.string.server_localhost), loginActivity.getString(R.string.encrypt_encoding)) +"&"+
                       URLEncoder.encode(Constants.HTTP_POST_DBUSER,     loginActivity.getString(R.string.encrypt_encoding))  + "=" + URLEncoder.encode(loginActivity.getString(R.string.server_dbuser), loginActivity.getString(R.string.encrypt_encoding)) +"&"+
                       URLEncoder.encode(Constants.HTTP_POST_DBNAME,     loginActivity.getString(R.string.encrypt_encoding))  + "=" + URLEncoder.encode(loginActivity.getString(R.string.server_dbname), loginActivity.getString(R.string.encrypt_encoding)) +"&"+
                       URLEncoder.encode(Constants.HTTP_POST_DBPASSWORD, loginActivity.getString(R.string.encrypt_encoding))  + "=" + URLEncoder.encode(loginActivity.getString(R.string.server_dbpassword), loginActivity.getString(R.string.encrypt_encoding));
            }
            catch (java.io.UnsupportedEncodingException ex) {}
            JSONObject jsonResponse = HttpRequest.doPost(loginActivity,link,data);
            try {
                if (jsonResponse.has(Constants.JSON_MESSAGE)) status = jsonResponse.getString(Constants.JSON_MESSAGE);
                if (jsonResponse.has(Constants.JSON_USERNAME)) userName = jsonResponse.getString(Constants.JSON_USERNAME);
                if (jsonResponse.has(Constants.JSON_USERID)) userCode = jsonResponse.getString(Constants.JSON_USERID);
                if (jsonResponse.has(Constants.JSON_FIREBASEID)) firebaseid = jsonResponse.getString(Constants.JSON_FIREBASEID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        loginActivity.userLoginTask = null;
        loginActivity.showProgress(false);
        switch (status) {
            case Constants.MESSAGE_LOGIN_SUCCESSFUL: {
                Common.updateStatusBar(loginActivity.statusBarView, ContextCompat.getColor(loginActivity.getApplicationContext(), R.color.message), loginActivity.getString(R.string.progress_connecting));
                Intent mainActivity = new Intent(loginActivity, MainActivity.class);
                mainActivity.putExtra(Constants.USER_EMAIL, email);
                mainActivity.putExtra(Constants.USER_NAME, userName);
                mainActivity.putExtra(Constants.USER_CODE, userCode);
                mainActivity.putExtra(Constants.USER_FIREBASEID, firebaseid);
                loginActivity.startActivity(mainActivity);
                break;
            }
            case Constants.ERROR_INVALID_PASSWORD: {
                Common.updateStatusBar(loginActivity.statusBarView, ContextCompat.getColor(loginActivity.getApplicationContext(), R.color.error), loginActivity.getString(R.string.error_invalid_password));
                break;
            }
            case Constants.ERROR_INCORRECT_PASSWORD: {
                Common.updateStatusBar(loginActivity.statusBarView, ContextCompat.getColor(loginActivity.getApplicationContext(), R.color.error), loginActivity.getString(R.string.error_incorrect_password));
                break;
            }
            case Constants.ERROR_MULTIPLE_LOGIN_INVALID: {
                Common.updateStatusBar(loginActivity.statusBarView,ContextCompat.getColor(loginActivity.getApplicationContext(), R.color.error),loginActivity.getString(R.string.error_multiple_login_not_allowed));
                break;
            }
            case Constants.MESSAGE_REG_SUCCESSFUL: {
                generateFirebaseID();
                break;
            }
            case Constants.ERROR_REG_FAILED: {
                Common.updateStatusBar(loginActivity.statusBarView, ContextCompat.getColor(loginActivity.getApplicationContext(), R.color.error), loginActivity.getString(R.string.error_reg_failed));
                break;
            }
            case Constants.ERROR_SERVER_DOWN: {
                Common.updateStatusBar(loginActivity.statusBarView, ContextCompat.getColor(loginActivity.getApplicationContext(), R.color.error), loginActivity.getString(R.string.error_server_down));
                break;
            }
            case Constants.ERROR_NETWORK_UNREACHABLE: {
                Common.updateStatusBar(loginActivity.statusBarView, ContextCompat.getColor(loginActivity.getApplicationContext(), R.color.error), loginActivity.getString(R.string.error_network_unreachable));
                break;
            }
            case Constants.ERROR_GPS_DISABLED: {
                Common.updateStatusBar(loginActivity.statusBarView, ContextCompat.getColor(loginActivity.getApplicationContext(), R.color.error), loginActivity.getString(R.string.error_gps_disabled));
                break;
            }
            case Constants.ERROR_SYSTEM_ADMIN: {
                Common.updateStatusBar(loginActivity.statusBarView, ContextCompat.getColor(loginActivity.getApplicationContext(), R.color.error), loginActivity.getString(R.string.error_system_admin));
                break;
            }
            case Constants.ERROR_DEVICE_IS_ALREADY_REGISTERED: {
                Common.updateStatusBar(loginActivity.statusBarView, ContextCompat.getColor(loginActivity.getApplicationContext(), R.color.error), loginActivity.getString(R.string.error_device_is_already_registered));
                break;
            }
            default: {
                break;
            }
        }
    }

    private String getDeviceId() {
        final TelephonyManager telephonyManager = (TelephonyManager) loginActivity.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    private void generateFirebaseID() {
        Firebase firebase = new Firebase(Constants.FIREBASE_APP);
        Firebase regFirebase = firebase.push();
        Map<String, String> val = new HashMap<>();
        val.put(Constants.USER_EMAIL, email);
        regFirebase.setValue(val);
        final String firebaseid = regFirebase.getKey();
        AsyncTask generateFirebaseidTask = new AsyncTask() {
            @Override
            protected void onPostExecute(Object jsonResponse) {
                super.onPostExecute(jsonResponse);
                String status = null;
                try {
                    status = ((JSONObject) jsonResponse).get(Constants.STATUS_MESSAGE).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status != null) {
                    if (status.equals(Constants.MESSAGE_UPDATE_SUCCESSFUL)) {
                        Intent mainActivity = new Intent(loginActivity, MainActivity.class);
                        mainActivity.putExtra(Constants.USER_EMAIL, email);
                        mainActivity.putExtra(Constants.USER_NAME, userName);
                        mainActivity.putExtra(Constants.USER_CODE, userCode);
                        mainActivity.putExtra(Constants.USER_FIREBASEID, firebaseid);
                        loginActivity.startActivity(mainActivity);
                    } else if (status.contains(Constants.STATUS_ERROR)) {
                        Common.updateStatusBar(loginActivity.statusBarView,ContextCompat.getColor(loginActivity.getApplicationContext(), R.color.error),loginActivity.getString(R.string.error_unable_to_generate_id));
                    }
                }
            }
            @Override
            protected void onProgressUpdate(Object[] values) {
                super.onProgressUpdate(values);
                Common.updateStatusBar(loginActivity.statusBarView, ContextCompat.getColor(loginActivity.getApplicationContext(), R.color.message), values[0].toString());
            }
            @Override
            protected Object doInBackground(Object[] params) {
                publishProgress(loginActivity.getString(R.string.progress_generatingid));
                return User.setUserFirebaseIdAndDeviceIdById(loginActivity.getApplicationContext(),firebaseid,deviceId,userCode);
            }
        };
        generateFirebaseidTask.execute((Void)null);
    }

    @Override
    protected void onCancelled() {
        loginActivity.userLoginTask = null;
        loginActivity.showProgress(false);
    }
}
