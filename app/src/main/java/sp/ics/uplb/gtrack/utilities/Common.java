package sp.ics.uplb.gtrack.utilities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sp.ics.uplb.gtrack.R;

public class Common {

    public static void delayInSeconds(long secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void handlerPostDelayed(Runnable r,long secs) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(r,secs * 1000);
    }

    public static boolean isEmailValid(String email) {
        return email.matches("^[a-z0-9_-]{8,15}$");
    }

    public static boolean isPasswordValid(String password) {
        return password.matches("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,15})");
    }

    public static boolean isGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void checkPermission(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    public static boolean isInternetConnectionAvailable() {
        Runtime runTime = Runtime.getRuntime();
        try {
            String address = Constants.FIREBASE_APP.substring(8);
            Logger.print("Pinging address: "+address.replace("/",""));
            Process pingProcess = runTime.exec("/system/bin/ping -c 1 "+address.replace("/",""));
            int exitValue = pingProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    public static String getErrorMessage(Context context,String status) {
        String message = status;
        if (status.contains(Constants.STATUS_ERROR)) {
            switch (status) {
                case Constants.ERROR_INCORRECT_PASSWORD: {
                    message = context.getString(R.string.error_incorrect_password);
                    break;
                }
                case Constants.ERROR_REG_FAILED: {
                    message = context.getString(R.string.error_reg_failed);
                    break;
                }
                case Constants.ERROR_SERVER_DOWN: {
                    message = context.getString(R.string.error_server_down);
                    break;
                }
                case Constants.ERROR_UPDATE_FAILED: {
                    message = context.getString(R.string.error_update_failed);
                    break;
                }
                case Constants.ERROR_NETWORK_UNREACHABLE: {
                    message = context.getString(R.string.error_network_unreachable);
                    break;
                }
                case Constants.ERROR_SYSTEM_ADMIN: {
                    message = context.getString(R.string.error_system_admin);
                    break;
                }
                case Constants.ERROR_INSERT_FAILED: {
                    message = context.getString(R.string.error_insert_failed);
                    break;
                }
                case Constants.ERROR_DELETE_FAILED: {
                    message = context.getString(R.string.error_delete_failed);
                    break;
                }
                case Constants.ERROR_SELECT_FAILED: {
                    message = context.getString(R.string.error_select_failed);
                    break;
                }
                case Constants.ERROR_INVALID_EMAIL: {
                    message = context.getString(R.string.error_invalid_email);
                    break;
                }
                case Constants.ERROR_INVALID_PASSWORD: {
                    message = context.getString(R.string.error_invalid_password);
                    break;
                }
                case Constants.ERROR_EMAIL_REQUIRED: {
                    message = context.getString(R.string.error_email_required);
                    break;
                }
                case Constants.ERROR_PASSWORD_REQUIRED: {
                    message = context.getString(R.string.error_password_required);
                    break;
                }
                case Constants.ERROR_GPS_DISABLED: {
                    message = context.getString(R.string.error_gps_disabled);
                    break;
                }
                case Constants.ERROR_PARTICIPANT_NOT_YET_REGISTERED: {
                    message = context.getString(R.string.error_participant_not_yet_registered);
                    break;
                }
                case Constants.ERROR_REQUEST_ALREADY_SENT: {
                    message = context.getString(R.string.error_request_already_sent);
                    break;
                }
                case Constants.ERROR_PARTICIPANT_CANT_BE_OWN_EMAIL: {
                    message = context.getString(R.string.error_participant_cant_be_own_email);
                    break;
                }
                case Constants.ERROR_UNABLE_TO_GENERATE_ID: {
                    message = context.getString(R.string.error_unable_to_generate_id);
                    break;
                }
                case Constants.ERROR_PASSWORD_ENCRYPTION: {
                    message = context.getString(R.string.error_password_encryption);
                    break;
                }
                case Constants.ERROR_RECIPIENT_NOT_ON_YOUR_CONTACT_LIST: {
                    message = context.getString(R.string.error_recipient_not_on_your_contact_list);
                    break;
                }
                case Constants.ERROR_DEVICE_IS_ALREADY_REGISTERED: {
                    message = context.getString(R.string.error_marker_already_deleted);
                    break;
                }
                default : {
                    message = context.getString(R.string.error_system_admin);
                    break;
                }
            }
        }
        return message;
    }

    public static void updateStatusBar(TextView statusBarMain,int color,String message) {
        if (statusBarMain!=null) {
            statusBarMain.setTextColor(color);
            statusBarMain.setText(message);
            statusBarMain.setSelected(true);
            statusBarMain.invalidate();
        }
    }

    public static void showProgress(Context context, final View progressView, final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public static String convertDateToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static Date convertStringToDate(String strDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            return simpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getCurrentTime() {
        return Calendar.getInstance().getTime();
    }

}
