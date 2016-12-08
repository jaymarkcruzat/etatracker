package sp.ics.uplb.gtrack.services;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sp.ics.uplb.gtrack.R;
import sp.ics.uplb.gtrack.activities.MainActivity;
import sp.ics.uplb.gtrack.controllers.Contact;
import sp.ics.uplb.gtrack.controllers.SQLiteDatabaseHandler;
import sp.ics.uplb.gtrack.controllers.SharedPref;
import sp.ics.uplb.gtrack.utilities.Common;
import sp.ics.uplb.gtrack.utilities.Constants;
import sp.ics.uplb.gtrack.utilities.FirebaseAction;
import sp.ics.uplb.gtrack.utilities.JSONParser;
import sp.ics.uplb.gtrack.utilities.Logger;

public class FirebaseListenerService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private NotificationCompat.Builder builder = null;
    private SQLiteDatabaseHandler db = null;
    private GoogleApiClient googleApiClient = null;
    private String userCode = null;
    private String userName = null;
    private String userFirebaseId = null;
    private ArrayList connectedPeers = new ArrayList();
    private ArrayList ignoreList = new ArrayList();
    private SharedPreferences sharedPreference = null;

    public String targetName = null;
    public LatLng targetLatLng = null;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public FirebaseListenerService getService() {
            return FirebaseListenerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void setTargetLocation(String name,LatLng target) {
        targetName = name;
        targetLatLng = target;
    }

    synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        googleApiClient.disconnect();
    }

    private void sendLocationData(Location location) {

        final List<Contact> contacts;
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();
        final double speed = location.getSpeed();
        final double eta = 0d;

        try {
            contacts = db.getAllConnectedContacts();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Logger.print("Error in retrieving connected contacts...");
            return;
        }

        Logger.print("targetLocation: "+targetName+" :"+targetLatLng);

        Intent intentGPS = new Intent();
        intentGPS.putExtra(Constants.KEY_CURRENT_GPS_LATITUDE, String.valueOf(latitude));
        intentGPS.putExtra(Constants.KEY_CURRENT_GPS_LONGITUDE, String.valueOf(longitude));
        intentGPS.putExtra(Constants.KEY_USER_CODE, userCode);
        intentGPS.putExtra(Constants.KEY_SENDER_USER_NAME, userName);
        intentGPS.putExtra(Constants.KEY_CURRENT_SPEED, String.valueOf(speed));
        intentGPS.putExtra(Constants.KEY_CURRENT_ETA, String.valueOf(eta));
        intentGPS.putExtra(Constants.KEY_FBASE_SERVER_TIMESTAMP, Common.convertDateToString(Common.getCurrentTime()));
        intentGPS.putExtra(Constants.KEY_TARGET_LOCATION, Constants.GLOBAL_BLANK);
        intentGPS.setAction(Constants.INTENT_DRAW_GPS);
        sendBroadcast(intentGPS);

        userFirebaseId = SharedPref.getString(getApplicationContext(),Constants.SHARED_PREF,Constants.USER_FIREBASEID,null);

        Iterator iterator = contacts.iterator();
        if (Common.isInternetConnectionAvailable()) {
            while (iterator.hasNext()) {
                final Contact contact = (Contact) iterator.next();
                AsyncTask sendGPSData = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            FirebaseAction.firebasePush(getApplicationContext(), contact.getFirebaseId(), Constants.KEY_CURRENT_GPS + userCode,
                                    new JSONObject()
                                            .put(Constants.KEY_USER_CODE, userCode)
                                            .put(Constants.KEY_SENDER_USER_NAME, userName)
                                            .put(Constants.KEY_CURRENT_GPS_LATITUDE, latitude)
                                            .put(Constants.KEY_CURRENT_GPS_LONGITUDE, longitude)
                                            .put(Constants.KEY_CURRENT_SPEED, speed)
                                            .put(Constants.KEY_CURRENT_ETA, eta)
                                            .put(Constants.KEY_SENDER_FIREBASEID, userFirebaseId)
                                            .put(Constants.KEY_FBASE_SERVER_TIMESTAMP, Common.convertDateToString(Common.getCurrentTime()))
                                            .put(Constants.KEY_TARGET_LOCATION,Constants.GLOBAL_BLANK).toString());
                            Logger.print("GPS Data Sent!");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                sendGPSData.execute((Void) null);
            }
        }
        else Logger.print("Connection is unavailable.");
    }

    @Override
    public void onLocationChanged(final Location location) {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                sendLocationData(location);
                return null;
            }
        }.execute((Void)null);
    }

    private boolean isTargetLocationReached(Location location, Location targetLocation) {
        if (location.distanceTo(targetLocation)<Constants.MIN_DISTANCE_TO_REACH_TARGET) {
            return true;
        }
        return false;
    }

    private void lockGPSReceiver(String userName) {
        Logger.print("lockGPSReceiver: "+userName);
        if (!ignoreList.contains(userName)) ignoreList.add(userName);
    }

    private void disconnectUser(String userName) {
        Logger.print("disconnectUser: " + userName);
        if (db.connectedContactExists(userName)) {
            db.deleteConnectedContact(userName);
        }
        Logger.print("Disconnect " + (db.connectedContactExists(userName) ? "Failed!" : "Success!"));
    }

    private void sendDisconnectMessageTo(String userName) {
        Logger.print("sendDisconnectMessageTo: " + userName);
        Contact contact = db.getListContactByUserName(userName);
        FirebaseAction.push(getApplicationContext(), contact.getFirebaseId(), Constants.KEY_SEND_DISCONNECT_REQ, this.userName);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Firebase firebase;
        String firebaseid;
        buildGoogleApiClient();

        sharedPreference = SharedPref.getInstance(this);

        if (intent!=null) {
            Bundle params = intent.getExtras();
            firebaseid = params.getString(Constants.USER_FIREBASEID);
            firebase = new Firebase(Constants.FIREBASE_APP + firebaseid);
            firebase.setPriority(Constants.FIREBASE_PRIORITY_HIGH);
            if (builder==null) createUIForeground(intent);
            userCode = (String) params.get(Constants.USER_CODE);
            userName = (String) params.get(Constants.USER_EMAIL);
            userFirebaseId = (String) params.get(Constants.USER_FIREBASEID);
            String userToDisconnect = (String) params.get(Constants.DISCONNECT);
            if (userToDisconnect!=null) {
                lockGPSReceiver(userToDisconnect);
                disconnectUser(userToDisconnect);
                sendDisconnectMessageTo(userToDisconnect);
            }
        }
        else {
            firebaseid = sharedPreference.getString(Constants.USER_FIREBASEID,null);
            firebase = new Firebase(Constants.FIREBASE_APP + firebaseid);
            firebase.setPriority(Constants.FIREBASE_PRIORITY_HIGH);
            userCode = SharedPref.getString(getApplicationContext(), Constants.SHARED_PREF, Constants.USER_CODE, null);
            userName = SharedPref.getString(getApplicationContext(),Constants.SHARED_PREF,Constants.USER_NAME,null);
            userFirebaseId = SharedPref.getString(getApplicationContext(),Constants.SHARED_PREF,Constants.USER_FIREBASEID,null);
        }
        db = new SQLiteDatabaseHandler(this,userCode);

        final Firebase fbase = firebase;
        fbase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Logger.print("Connected Peers: " + connectedPeers.toString());
                if (dataSnapshot != null) {
                    String key = dataSnapshot.getKey();
                    String value = (String) dataSnapshot.getValue();
                    if (key != null) {
                        if (key.contains(Constants.KEY_ACCEPT_REQUEST)) {
                            JSONObject jsonObject = JSONParser.getJSONObject(value);
                            String userCode = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_USER_CODE);
                            String userName = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_USER_NAME);
                            String userFirebaseID = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_USER_FIREBASEID);
                            if (!db.listContactExists(userName)) {
                                Contact newContact = new Contact(Integer.parseInt(userCode), userName, userFirebaseID);
                                db.addListContact(newContact);
                            }
                            sendNotification(String.format(getString(R.string.ticker_accept_request), userName));
                            fbase.child(key).removeValue();
                        } else if (key.contains(Constants.KEY_REQUEST_FROM)) {
                            sendNotification(String.format(getString(R.string.ticker_request), value));
                            fbase.child(key).removeValue();
                        } else if (key.contains(Constants.KEY_SHARE_FROM)) {
                            JSONObject jsonObject = JSONParser.getJSONObject(value);
                            String location = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_MARKER_TITLE);
                            String location_lat = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_MARKER_LATITUDE);
                            String location_long = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_MARKER_LONGITUDE);
                            String sender = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_SENDER_USER_NAME);
                            String senderfirebaseid = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_SENDER_FIREBASEID);
                            sendNotification(String.format(getString(R.string.ticker_meeting), sender, location));
                            fbase.child(key).removeValue();
                            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mainActivity.putExtra(Constants.KEY_MARKER_TITLE, location);
                            mainActivity.putExtra(Constants.KEY_MARKER_LATITUDE, location_lat);
                            mainActivity.putExtra(Constants.KEY_MARKER_LONGITUDE, location_long);
                            mainActivity.putExtra(Constants.KEY_SENDER_USER_NAME, sender);
                            mainActivity.putExtra(Constants.KEY_SENDER_USER_CODE, key.replace(Constants.KEY_SHARE_FROM, Constants.GLOBAL_BLANK));
                            mainActivity.putExtra(Constants.KEY_SENDER_FIREBASEID, senderfirebaseid);
                            startActivity(mainActivity);
                        } else if (key.contains(Constants.KEY_ACCEPT_SHARE)) {
                            JSONObject jsonObject = JSONParser.getJSONObject(value);
                            String location = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_MARKER_TITLE);
                            String receiver = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_RECEIVER_USER_NAME);
                            sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_RECEIVER_FIREBASEID);
                            sendNotification(String.format(getString(R.string.ticker_accept_meeting), receiver, location));
                            fbase.child(key).removeValue();
                        } else if (key.contains(Constants.KEY_CURRENT_GPS)) {
                            Intent intentGPS = new Intent();
                            JSONObject jsonObject = JSONParser.getJSONObject(value);
                            String current_gps_latitude = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_CURRENT_GPS_LATITUDE);
                            String current_gps_longitude = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_CURRENT_GPS_LONGITUDE);
                            String user_code = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_USER_CODE);
                            String current_speed = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_CURRENT_SPEED);
                            String sender_user_name = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_SENDER_USER_NAME);
                            String sender_firebaseid = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_SENDER_FIREBASEID);
                            String current_eta = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_CURRENT_ETA);
                            String timeStamp = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_FBASE_SERVER_TIMESTAMP);
                            String targetLocation = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject, Constants.KEY_TARGET_LOCATION);

                            if (connectedPeers.contains(user_code)) {

                                if (!ignoreList.contains(sender_user_name)) {

                                    intentGPS.putExtra(Constants.KEY_CURRENT_GPS_LATITUDE, current_gps_latitude);
                                    intentGPS.putExtra(Constants.KEY_CURRENT_GPS_LONGITUDE, current_gps_longitude);
                                    intentGPS.putExtra(Constants.KEY_USER_CODE, user_code);
                                    intentGPS.putExtra(Constants.KEY_SENDER_USER_NAME, sender_user_name);
                                    intentGPS.putExtra(Constants.KEY_CURRENT_SPEED, current_speed);
                                    intentGPS.putExtra(Constants.KEY_CURRENT_ETA, current_eta);
                                    intentGPS.putExtra(Constants.KEY_FBASE_SERVER_TIMESTAMP, timeStamp);
                                    intentGPS.putExtra(Constants.KEY_TARGET_LOCATION, targetLocation);
                                    intentGPS.setAction(Constants.INTENT_DRAW_GPS);
                                    sendBroadcast(intentGPS);
                                    if (!db.connectedContactExists(sender_user_name)) {
                                        int id = Integer.parseInt(user_code);
                                        Contact contact = new Contact(id, sender_user_name, sender_firebaseid);
                                        db.addConnectedContact(contact);
                                    }

                                } else
                                    Logger.print("Ignoring Received GPS data from " + sender_user_name);

                            }

                            if (!connectedPeers.contains(user_code)) connectedPeers.add(user_code);
                            fbase.child(key).removeValue();
                        } else if (key.contains(Constants.KEY_SEND_DISCONNECT_REQ)) {
                            sendNotification(String.format(getString(R.string.ticker_disconnect), value));
                            disconnectUser(value);
                            Intent intentDisconnect = new Intent();
                            intentDisconnect.putExtra(Constants.KEY_SENDER_USER_NAME, value);
                            intentDisconnect.setAction(Constants.INTENT_DISCONNECT);
                            sendBroadcast(intentDisconnect);
                            Contact contact = db.getListContactByUserName(value);
                            FirebaseAction.push(getApplicationContext(), contact.getFirebaseId(), Constants.KEY_RELEASE_GPS_RECEIVER_LOCK, userName);
                            fbase.child(key).removeValue();
                        } else if (key.contains(Constants.KEY_RELEASE_GPS_RECEIVER_LOCK)) {
                            if (ignoreList.contains(value)) ignoreList.remove(value);
                            fbase.child(key).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        return START_STICKY;
    }

    private void createUIForeground(Intent intent){
        builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(Constants.GLOBAL_BLANK));
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle(getString(R.string.app_name));
        Notification notification = builder.build();
        startForeground(R.string.app_name, notification);
    }

    private void sendNotification(String message) {
        if (builder!=null) {
            builder.setTicker(message);
            builder.setSubText(message);
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            Logger.print("sendNotification");
            startForeground(R.string.app_name, builder.build());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(Constants.LOCATION_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(Constants.LOCATION_REQUEST_INTERVAL);
        //locationRequest.setSmallestDisplacement(Constants.SMALLEST_DISPLACEMENT);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        catch (Exception ex) {}
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

}