package sp.ics.uplb.gtrack.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import sp.ics.uplb.gtrack.R;
import sp.ics.uplb.gtrack.adapters.CustomInfoWindowAdapter;
import sp.ics.uplb.gtrack.controllers.Contact;
import sp.ics.uplb.gtrack.controllers.Markers;
import sp.ics.uplb.gtrack.controllers.Meeting;
import sp.ics.uplb.gtrack.controllers.Request;
import sp.ics.uplb.gtrack.controllers.SQLiteDatabaseHandler;
import sp.ics.uplb.gtrack.controllers.SharedPref;
import sp.ics.uplb.gtrack.controllers.User;
import sp.ics.uplb.gtrack.listeners.MarkerButtonClickListener;
import sp.ics.uplb.gtrack.listeners.OnCameraChangeListener;
import sp.ics.uplb.gtrack.listeners.OnMarkerClickListener;
import sp.ics.uplb.gtrack.listeners.OnMarkerCloseListener;
import sp.ics.uplb.gtrack.menus.fab.MenusFragment;
import sp.ics.uplb.gtrack.services.FirebaseListenerService;
import sp.ics.uplb.gtrack.utilities.Common;
import sp.ics.uplb.gtrack.utilities.Constants;
import sp.ics.uplb.gtrack.utilities.FirebaseAction;
import sp.ics.uplb.gtrack.utilities.JSONParser;
import sp.ics.uplb.gtrack.utilities.Logger;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    public GoogleMap googleMap = null;
    public SharedPreferences sharedPreference = null;
    public SharedPreferences.Editor editor = null;
    public String emailAddress = null;
    public String userName = null;
    public String userCode = null;
    public String firebaseid = null;
    public Button markerButtonEdit = null;
    public Button markerButtonDelete = null;
    public Button markerButtonSet = null;
    public Button markerButtonShare = null;
    public Button markerButtonMove = null;
    public Marker selectedMarker = null;
    public TextView statusBarMain = null;
    public HashMap<String,JSONObject> contactListDetails = null;
    public ArrayList connectedUsersList = null;
    public float currentZoom = 15;
    public float currentTilt = 30;
    public float currentBearing = 1;
    private Bundle params = null;
    public SQLiteDatabaseHandler db = null;
    private LocationBroadcastReceiver locationBroadcastReceiver = null;
    private IntentFilter locationBroadcastIntentFilter = null;
    private SupportMapFragment mapFragment = null;
    public LatLng currentLocation = null;
    public ArrayList<Marker> markersList = new ArrayList<Marker>();
    public boolean markersLoaded = false;
    public boolean contactsLoaded = false;
    public Polyline polyline = null;
    public Circle circle = null;
    public Marker currentUserIcon = null;
    public LatLng targetLocationLatLng = null;
    public ImageView myLocationView = null;

    public HashMap<String,Button> connectedUsersButtons= null;
    public HashMap<String,EditText> connectedUsersTextPanel= null;
    public HashMap<String,LinearLayout> horizontalViews= null;
    private HashMap<String,Marker> connectedUsersMarkers = null;

    private static MainActivity mainActivityRunningInstance;

    public static MainActivity getInstance() {
        return mainActivityRunningInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityRunningInstance = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap);
        mapFragment.getMapAsync(this);
        sharedPreference = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        editor = sharedPreference.edit();
        emailAddress = sharedPreference.getString(Constants.USER_EMAIL, null);
        userName = sharedPreference.getString(Constants.USER_NAME, null);
        userCode = sharedPreference.getString(Constants.USER_CODE, null);
        firebaseid = sharedPreference.getString(Constants.USER_FIREBASEID, null);
        markerButtonEdit = (Button)findViewById(R.id.editButton);
        markerButtonDelete = (Button)findViewById(R.id.deleteButton);
        markerButtonSet = (Button)findViewById(R.id.setButton);
        markerButtonShare = (Button)findViewById(R.id.shareButton);
        markerButtonMove = (Button)findViewById(R.id.moveButton);
        MarkerButtonClickListener markerButtonClickListener = new MarkerButtonClickListener(this);
        markerButtonDelete.setOnClickListener(markerButtonClickListener);
        markerButtonEdit.setOnClickListener(markerButtonClickListener);
        markerButtonSet.setOnClickListener(markerButtonClickListener);
        markerButtonShare.setOnClickListener(markerButtonClickListener);
        markerButtonMove.setOnClickListener(markerButtonClickListener);
        statusBarMain = (TextView)findViewById(R.id.status_bar_main);
        params = getIntent().getExtras();
        contactListDetails = new HashMap<String,JSONObject>();
        connectedUsersList = new ArrayList();
        locationBroadcastReceiver = new LocationBroadcastReceiver();
        locationBroadcastIntentFilter = new IntentFilter();
        registerReceiver(locationBroadcastReceiver,locationBroadcastIntentFilter);
        connectedUsersMarkers = new HashMap<String,Marker>();
        connectedUsersButtons = new HashMap<String,Button>();
        connectedUsersTextPanel = new HashMap<String,EditText>();
        horizontalViews = new HashMap<String,LinearLayout>();
        myLocationView = (ImageView)findViewById(R.id.currentLocationButton);
        myLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLocation!=null) {
                    moveCamera(currentLocation);
                }
            }
        });

        if (params !=null && sharedPreference.getString(Constants.USER_EMAIL,null)==null) {
            editor.putString(Constants.USER_EMAIL, emailAddress  = params.getString(Constants.USER_EMAIL));
            editor.putString(Constants.USER_NAME, userName  = params.getString(Constants.USER_NAME));
            editor.putString(Constants.USER_CODE, userCode  = params.getString(Constants.USER_CODE));
            editor.putString(Constants.USER_FIREBASEID, firebaseid  = params.getString(Constants.USER_FIREBASEID));
            editor.apply();
        }

        db = new SQLiteDatabaseHandler(this,userCode);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.addView(getLayoutInflater().inflate(R.layout.auto_complete_search, null));
        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (place!=null) moveCamera(place.getLatLng());
            }
            @Override
            public void onError(Status status) {
            }
        });

        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new MenusFragment();
        ((MenusFragment)fragment).setMainActivity(this);
        fragmentTransaction.replace(R.id.fragment, fragment).commit();

        String target = SharedPref.getString(getApplicationContext(), Constants.SHARED_PREF, Constants.TARGET_LOCATION, null);
        String targetLatitude = SharedPref.getString(getApplicationContext(), Constants.SHARED_PREF, Constants.TARGET_LOCATION_LATITUDE, null);
        String targetLongitude = SharedPref.getString(getApplicationContext(), Constants.SHARED_PREF, Constants.TARGET_LOCATION_LONGITUDE, null);
        if (!isServiceActive(FirebaseListenerService.class)) {
            Intent intent = new Intent(getApplicationContext(), FirebaseListenerService.class);
            intent.putExtra(Constants.USER_EMAIL, emailAddress);
            intent.putExtra(Constants.USER_NAME, userName);
            intent.putExtra(Constants.USER_CODE, userCode);
            intent.putExtra(Constants.USER_FIREBASEID, firebaseid);
            intent.putExtra(Constants.TARGET_LOCATION, target);
            intent.putExtra(Constants.TARGET_LOCATION_LATITUDE, targetLatitude);
            intent.putExtra(Constants.TARGET_LOCATION_LONGITUDE, targetLongitude);
            startService(intent);
        }
        if (!Common.isNull(target) && !Common.isNull(targetLatitude) && !Common.isNull(targetLongitude)) targetLocationLatLng = new LatLng(Double.parseDouble(targetLatitude),Double.parseDouble(targetLongitude));

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPref.setString(getApplicationContext(), Constants.SHARED_PREF, getString(R.string.key_map_zoom_level), String.valueOf(currentZoom));
        SharedPref.setString(getApplicationContext(), Constants.SHARED_PREF, getString(R.string.key_map_tilt), String.valueOf(currentTilt));
        SharedPref.setString(getApplicationContext(), Constants.SHARED_PREF, getString(R.string.key_map_bearing), String.valueOf(currentBearing));
    }

    @Override
    protected void onPause() {
        mapFragment.onPause();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        mapFragment.onLowMemory();
        super.onLowMemory();
    }

    @Override
    protected void onResume() {
        mapFragment.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (locationBroadcastReceiver!=null) {
            unregisterReceiver(locationBroadcastReceiver);
            locationBroadcastReceiver=null;
        }
        if (selectedMarker!=null) selectedMarker.remove();
        if (currentUserIcon!=null) currentUserIcon.remove();
        googleMap.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        ((TextView) findViewById(R.id.userName)).setText(userName);
        ((TextView) findViewById(R.id.email)).setText(emailAddress);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_settings: {
                Intent settingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
                settingsActivity.putExtra(Constants.USER_EMAIL, emailAddress);
                settingsActivity.putExtra(Constants.USER_NAME, userName);
                startActivity(settingsActivity);
                break;
            }
            case R.id.nav_signout: {
                AsyncTask signOutTask = new AsyncTask() {
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
                                SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE).edit();
                                editor.remove(Constants.USER_EMAIL);
                                editor.remove(Constants.USER_NAME);
                                editor.remove(Constants.USER_CODE);
                                editor.remove(Constants.USER_FIREBASEID);
                                editor.clear();
                                editor.apply();
                                Intent notificationServiceIntent = new Intent(getApplicationContext(), FirebaseListenerService.class);
                                stopService(notificationServiceIntent);
                                finish();
                                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(loginActivity);
                                Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.message), getString(R.string.message_signout_successful));
                            } else if (status.contains(Constants.STATUS_ERROR))
                                Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.error), Common.getErrorMessage(getApplicationContext(),status));
                        }
                    }
                    @Override
                    protected void onProgressUpdate(Object[] values) {
                        super.onProgressUpdate(values);
                        Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.message), values[0].toString());
                    }
                    @Override
                    protected Object doInBackground(Object[] params) {
                        publishProgress(getString(R.string.progress_signingout));
                        return User.setUserStatusByUserName(getApplicationContext(),Constants.GLOBAL_ZERO,emailAddress);
                    }
                };
                signOutTask.execute((Void)null);
                break;
            }
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isServiceActive(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean containsMarker(String markerTitle) {
        Iterator i = markersList.iterator();
        while (i.hasNext()) {
            Marker marker = (Marker) i.next();
            if (marker.getTitle()!=null) {
                if (marker.getTitle().equals(markerTitle))
                    return true;
            }
        }
        return false;
    }

    private void initializeMap() {
        AsyncTask retrieveMarkersTask = new AsyncTask() {
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
                    String target = SharedPref.getString(getApplicationContext(), Constants.SHARED_PREF, Constants.TARGET_LOCATION, null);
                    if (status.equals(Constants.MESSAGE_SELECT_SUCCESSFUL)) {
                        String result = null;
                        try {
                            result = ((JSONObject) jsonResponse).get(Constants.JSON_RESULT).toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONArray jsonArray = JSONParser.getJSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String markerTitle = jsonObject.get("MKR_TITLE").toString();
                                String markerDescription = jsonObject.get("MKR_DESC").toString();
                                String point = jsonObject.get("MKR_LATLNG").toString();
                                point = point.replace("POINT(", "");
                                point = point.replace(")", "");
                                String[] coordinate = point.split(" ");
                                double latitude = Double.parseDouble(coordinate[0].trim());
                                double longitude = Double.parseDouble(coordinate[1].trim());
                                LatLng latLng = new LatLng(latitude, longitude);
                                Marker newMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title(markerTitle).snippet(markerDescription));
                                newMarker.setDraggable(false);
                                newMarker.showInfoWindow();
                                markerButtonSet.setText(target==null||!target.equals(markerTitle) ? Constants.BUTTON_TEXT_SET : Constants.BUTTON_TEXT_UNSET);
                                markerButtonEdit.setVisibility(View.VISIBLE);
                                markerButtonDelete.setVisibility(View.VISIBLE);
                                markerButtonSet.setVisibility(View.VISIBLE);
                                markerButtonShare.setVisibility(View.VISIBLE);
                                markerButtonMove.setVisibility(View.VISIBLE);
                                selectedMarker = newMarker;
                                markersList.add(newMarker);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.message), getString(R.string.message_markers_retrieved));
                        markersLoaded=true;
                        initializeContacts();
                    } else if (status.equals(Constants.MESSAGE_SELECT_EMPTY)) {
                        Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.message), getString(R.string.message_no_markers));
                        initializeContacts();
                    } else if (status.contains(Constants.STATUS_ERROR)) {
                        //Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.error), Common.getErrorMessage(getApplicationContext(), status));
                        initializeMap();
                    }
                }
            }
            @Override
            protected void onProgressUpdate(Object[] values) {
                super.onProgressUpdate(values);
                Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.message), values[0].toString());
            }
            @Override
            protected Object doInBackground(Object[] params) {
                publishProgress(getString(R.string.progress_collectingdata));
                return Markers.getAllByUserId(getApplicationContext(), userCode);
            }
        };
        retrieveMarkersTask.execute((Void) null);
    }

    private void initializeContacts() {
        AsyncTask init_contacts = new AsyncTask() {
            @Override
            protected void onPostExecute(Object jsonResponse) {
                super.onPostExecute(jsonResponse);
                String status;
                try {
                    status = ((JSONObject) jsonResponse).get(Constants.STATUS_MESSAGE).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                    status = null;
                }
                if (status != null) {
                    if (status.equals(Constants.MESSAGE_SELECT_SUCCESSFUL)) {
                        String result = null;
                        try {
                            result = ((JSONObject) jsonResponse).get(Constants.JSON_RESULT).toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONArray jsonArray = JSONParser.getJSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String userCode = jsonObject.get("USR_ID").toString();
                                String userName = jsonObject.get("USR_EML").toString();
                                String userFirebaseID = jsonObject.get("USR_FIREBASEID").toString();
                                if (!db.listContactExists(userName)) {
                                    Contact newContact = new Contact(Integer.parseInt(userCode),userName,userFirebaseID);
                                    db.addListContact(newContact);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.message), getString(R.string.message_contacts_retrieved));
                        contactsLoaded=true;
                    } else if (status.equals(Constants.MESSAGE_SELECT_EMPTY)) {
                        Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.message), getString(R.string.message_no_contacts));
                    } else if (status.contains(Constants.STATUS_ERROR)) {
                        //ommon.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.error), Common.getErrorMessage(getApplicationContext(), status));
                        initializeContacts();
                    }
                }
            }
            @Override
            protected void onProgressUpdate(Object[] values) {
                super.onProgressUpdate(values);
                Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.message), values[0].toString());
            }
            @Override
            protected Object doInBackground(Object[] params) {
                publishProgress(getString(R.string.progress_retrievingcontacts));
                return Request.getApprovedRequestsByUserId(getApplicationContext(), userCode);
            }
        };
        init_contacts.execute((Void) null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        Common.checkPermission(getApplicationContext());

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(false);

        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getLayoutInflater(), this));
        googleMap.setOnMarkerClickListener(new OnMarkerClickListener(this));
        googleMap.setOnInfoWindowCloseListener(new OnMarkerCloseListener(this));
        googleMap.setOnCameraChangeListener(new OnCameraChangeListener(this));
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (currentLocation != null) {
                    moveCamera(currentLocation);
                }
                return false;
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (params!=null) {
            String sender = (String) params.get(Constants.KEY_SENDER_USER_NAME);
            String location = (String) params.get(Constants.KEY_MARKER_TITLE);
            String location_lat = (String) params.get(Constants.KEY_MARKER_LATITUDE);
            String location_long = (String) params.get(Constants.KEY_MARKER_LONGITUDE);
            String userCode = (String) params.get(Constants.KEY_SENDER_USER_CODE);
            String senderfirebaseid = (String) params.get(Constants.KEY_SENDER_FIREBASEID);
            if (sender != null && location != null) {
                displayAcceptRejectMeetingDialog(sender, location, Double.parseDouble(location_lat), Double.parseDouble(location_long), userCode, senderfirebaseid);
            }
        }
    }

    private void displayAcceptRejectMeetingDialog(final String sender, final String location, final double location_lat, final double location_long, final String senderId, final String senderfirebaseid) {
        LayoutInflater li = LayoutInflater.from(this);
        View acceptRejectView = li.inflate(R.layout.acceptreject_meeting_dialog, null);
        TextView acceptRejectTextView = (TextView)acceptRejectView.findViewById(R.id.acceptreject_meeting_dialog);
        acceptRejectTextView.setText("You were invited by <" + sender + "> to meet at location <" + location + ">. Do you want to accept this request?");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(acceptRejectView);
        alertDialogBuilder.setCancelable(false).setPositiveButton(Constants.GLOBAL_ACCEPT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AsyncTask acceptInv = new AsyncTask() {
                    String markerId;
                    String markerTitle;
                    String markerDesc;
                    LatLng latLng;

                    @Override
                    protected void onPostExecute(Object jsonResponse) {
                        super.onPostExecute(jsonResponse);
                        try {
                            String status = (String) ((JSONObject) jsonResponse).get(Constants.STATUS_MESSAGE);
                            if (status.equals(Constants.MESSAGE_INSERT_SUCCESSFUL)) {
                                //get other participants user data
                                AsyncTask getParticipantsData = new AsyncTask() {
                                    @Override
                                    protected void onPostExecute(Object jsonResponse) {
                                        super.onPostExecute(jsonResponse);
                                        String status = null;
                                        JSONObject response = (JSONObject) jsonResponse;
                                        try {
                                            status = (String) response.get(Constants.STATUS_MESSAGE);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if (status != null) {
                                            if (status.equals(Constants.MESSAGE_SELECT_SUCCESSFUL)) {
                                                String results = null;
                                                try {
                                                    results = (String) response.get(Constants.JSON_RESULT);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                //sending gps data to participants
                                                JSONArray jsonArray = JSONParser.getJSONArray(results);
                                                for (int i=0; i < jsonArray.length(); i++) {
                                                    try {
                                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                        String userId = jsonObject.get("ID").toString();
                                                        String userName = jsonObject.get("USR_EML").toString();
                                                        String userFirebaseID = jsonObject.get("USR_FIREBASEID").toString();
                                                        Logger.print("SendGPSto:"+userId+"/ "+userName+"/ "+userFirebaseID);
                                                        if (userCode.equals(userId)) continue;
                                                        try {
                                                            FirebaseAction.push(getApplicationContext(), userFirebaseID, Constants.KEY_ACCEPT_SHARE + userCode, new JSONObject().put(Constants.KEY_RECEIVER_USER_NAME, emailAddress).put(Constants.KEY_MARKER_TITLE, location).put(Constants.KEY_RECEIVER_FIREBASEID, firebaseid).toString());
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        if (!db.connectedContactExists(userName)) {
                                                            int id = Integer.parseInt(userId);
                                                            Contact contact = new Contact(id, userName, userFirebaseID);
                                                            db.addConnectedContact(contact);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                //send gps data to the creator
                                                try {
                                                    FirebaseAction.push(getApplicationContext(), senderfirebaseid, Constants.KEY_ACCEPT_SHARE + userCode, new JSONObject().put(Constants.KEY_RECEIVER_USER_NAME, emailAddress).put(Constants.KEY_MARKER_TITLE, location).put(Constants.KEY_RECEIVER_FIREBASEID, firebaseid).toString());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                if (!db.connectedContactExists(sender)) {
                                                    int id = Integer.parseInt(senderId);
                                                    Contact contact = new Contact(id, sender, senderfirebaseid);
                                                    db.addConnectedContact(contact);
                                                }
                                                //save shared marker to remote server
                                                saveMarkerToRemoteServer(sender,markerTitle,markerDesc,latLng.latitude,latLng.longitude);
                                            } else if (status.contains(Constants.STATUS_ERROR)) {
                                                Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.error), Common.getErrorMessage(getApplicationContext(), status));
                                            }
                                        }
                                    }
                                    @Override
                                    protected Object doInBackground(Object[] params) {
                                        JSONObject response = Meeting.getParticipantsByMarkerId(getApplicationContext(), markerId);
                                        return response;
                                    }
                                };
                                getParticipantsData.execute((Void) null);
                            } else if (status.contains(Constants.STATUS_ERROR)) {
                                Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.error), Common.getErrorMessage(getApplicationContext(), status));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onProgressUpdate(Object[] values) {
                        super.onProgressUpdate(values);
                        Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.message), values[0].toString());
                    }

                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            publishProgress(getString(R.string.progress_processing_sender_request));
                            JSONObject response = Markers.getIdByTitleAndUserId(getApplicationContext(), location, senderId);
                            String status = (String) response.get(Constants.STATUS_MESSAGE);
                            if (status != null) {
                                if (status.equals(Constants.MESSAGE_SELECT_SUCCESSFUL)) {
                                    String results = (String) response.get(Constants.JSON_RESULT);
                                    JSONObject data = JSONParser.getJSONArray(results).getJSONObject(0);
                                    markerId = (String) data.get("ID");
                                    markerTitle = (String) data.get("MKR_TITLE");
                                    markerDesc = (String) data.get("MKR_DESC");
                                    String markerLatlng = (String) data.get("MKR_LATLNG");
                                    markerLatlng = markerLatlng.replace("POINT(", "");
                                    markerLatlng = markerLatlng.replace(")", "");
                                    String[] coordinate = markerLatlng.split(" ");
                                    double latitude = Double.parseDouble(coordinate[0].trim());
                                    double longitude = Double.parseDouble(coordinate[1].trim());
                                    latLng = new LatLng(latitude, longitude);
                                    JSONObject checkDuplicateResponse = Meeting.getIdByMarkerIdAndParticipant(getApplicationContext(), markerId, userCode);
                                    status = (String) checkDuplicateResponse.get(Constants.STATUS_MESSAGE);
                                    if (status.equals(Constants.MESSAGE_SELECT_EMPTY))
                                        return Meeting.create(getApplicationContext(), markerId, userCode, Constants.GLOBAL_ONE);
                                    else
                                        return sp.ics.uplb.gtrack.utilities.JSONObject.put(new JSONObject(), Constants.STATUS_MESSAGE, Constants.MESSAGE_INSERT_SUCCESSFUL);
                                } else if (status.equals(Constants.MESSAGE_SELECT_EMPTY)) {
                                    Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.error), getString(R.string.error_marker_already_deleted));
                                } else if (status.contains(Constants.STATUS_ERROR)) {
                                    Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.error), Common.getErrorMessage(getApplicationContext(), status));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                acceptInv.execute((Void) null);
            }
        }).setNegativeButton(Constants.GLOBAL_REJECT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.message), getString(R.string.message_meeting_request_rejected) + Constants.GLOBAL_SPACE + "<" + sender + ">");
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void saveMarkerToRemoteServer(final String sender,final String markerTitle,final String markerDesc,final double latitude,final double longitude) {
        Logger.print("saveMarkerToRemoteServer: ");
        AsyncTask saveMarkerToRemoteServerTask = new AsyncTask() {
            @Override
            protected void onPostExecute(Object jsonResponse) {
                super.onPostExecute(jsonResponse);
                try {
                    String status = (String) ((JSONObject) jsonResponse).get(Constants.STATUS_MESSAGE);
                    if (status.equals(Constants.MESSAGE_INSERT_SUCCESSFUL)) {
                        //display shared location
                        Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.message), getString(R.string.message_meeting_request_accepted) + Constants.GLOBAL_SPACE  + sender + ".");
                        LatLng latLng = new LatLng(latitude,longitude);
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).snippet(markerDesc).title(markerTitle).draggable(false).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                        marker.showInfoWindow();
                        moveCamera(latLng);
                    } else if (status.contains(Constants.STATUS_ERROR)) {
                        Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.error), Common.getErrorMessage(getApplicationContext(), status));
                    }
                }
                catch (JSONException ex) {
                }
            }
            @Override
            protected void onProgressUpdate(Object[] values) {
                super.onProgressUpdate(values);
                Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.message), values[0].toString());
            }
            @Override
            protected Object doInBackground(Object[] params) {
                publishProgress(getString(R.string.progress_savingmarker));
                return Markers.create(getApplicationContext(),markerTitle,markerDesc,latitude,longitude,Constants.GLOBAL_ZERO,userCode);
            }
        };
        saveMarkerToRemoteServerTask.execute((Void) null);
    }

    private void removeUserComponents(String userName) {
        Logger.print("removeUserComponents: "+userName);
        Button userButton = connectedUsersButtons.containsKey(userName) ? connectedUsersButtons.get(userName) : null;
        EditText userText = connectedUsersTextPanel.containsKey(userName) ? connectedUsersTextPanel.get(userName) : null;
        LinearLayout horizontalView = horizontalViews.containsKey(userName) ? horizontalViews.get(userName) : null;
        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.user_panel_id);
        Marker userMarker = connectedUsersMarkers.containsKey(userName) ? connectedUsersMarkers.get(userName) : null;

        if (userButton!=null) connectedUsersButtons.remove(userName);
        if (userText!=null) connectedUsersTextPanel.remove(userName);
        if (userMarker!=null) userMarker.remove();
        if (horizontalView!=null) {
            horizontalViews.remove(userName);
            horizontalView.removeAllViews();
            if (parentLayout!=null) parentLayout.removeView(horizontalView);
        }
        Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getApplicationContext(), R.color.message), getString(R.string.message_user_is_now_disconnected) + " " + userName + ".");
    }

    private void sendDisconnectIntent(String userNameToBeDisconnected) {
        Logger.print("sendDisconnectIntent: " + userNameToBeDisconnected);
        String target = SharedPref.getString(getApplicationContext(), Constants.SHARED_PREF, Constants.TARGET_LOCATION, null);
        Intent intent = new Intent(getApplicationContext(), FirebaseListenerService.class);
        intent.putExtra(Constants.USER_EMAIL, emailAddress);
        intent.putExtra(Constants.USER_NAME, userName);
        intent.putExtra(Constants.USER_CODE, userCode);
        intent.putExtra(Constants.USER_FIREBASEID, firebaseid);
        intent.putExtra(Constants.TARGET_LOCATION, target);
        intent.putExtra(Constants.DISCONNECT, userNameToBeDisconnected);
        startService(intent);
    }

    public void updateUserPanel(final String userName,int userCode, final double latitude, final double longitude, double speed, double eta, String timeStamp, String targetLocation) {
        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.user_panel_id);

        Button newButton = connectedUsersButtons.containsKey(userName) ? connectedUsersButtons.get(userName) : null;
        if (newButton==null) {
            newButton = new Button(this);
            newButton.setPadding(5, 2, 5, 2);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50,50);
            params.leftMargin=10;
            params.topMargin=2;
            params.bottomMargin=2;
            params.gravity=Gravity.CENTER_VERTICAL;
            newButton.setLayoutParams(params);
            newButton.setBackgroundColor(Color.GREEN);
            newButton.setText(String.valueOf(userName.charAt(0)).toUpperCase());
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveCamera(new LatLng(latitude, longitude));
                }
            });
            newButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final String userNameTemp = userName;
                    LayoutInflater li = LayoutInflater.from(getInstance());
                    View deleteMarkerView = li.inflate(R.layout.disconnect_user, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getInstance());
                    alertDialogBuilder.setView(deleteMarkerView);
                    alertDialogBuilder.setCancelable(false).setPositiveButton(Constants.GLOBAL_YES, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sendDisconnectIntent(userNameTemp);
                            removeUserComponents(userNameTemp);
                        }
                    }).setNegativeButton(Constants.GLOBAL_NO, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return false;
                }
            });
            connectedUsersButtons.put(userName,newButton);
        }

        EditText editText = connectedUsersTextPanel.containsKey(userName) ? connectedUsersTextPanel.get(userName) : null;
        if (editText==null) {
            editText = new EditText(this);
            editText.setCursorVisible(false);
            editText.setFocusable(false);
            editText.setLines(3);
            editText.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(10);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin=5;
            layoutParams.gravity=Gravity.CENTER_VERTICAL;
            editText.setLayoutParams(layoutParams);
            connectedUsersTextPanel.put(userName,editText);
        }
        editText.setText(userName + "→[ " + targetLocation + " ]" + Constants.GLOBAL_NEW_LINE +
                "Speed: " + speed + " m/s ETA: " + eta + " min" + Constants.GLOBAL_NEW_LINE +
                "Received: " + timeStamp);
        editText.invalidate();

        LinearLayout horizontalView = horizontalViews.containsKey(userName) ? horizontalViews.get(userName) : null;
        if (horizontalView==null) {
            horizontalView = new LinearLayout(this);
            horizontalView.setOrientation(LinearLayout.HORIZONTAL);
            horizontalView.setGravity(Gravity.CENTER_VERTICAL);
            horizontalView.setBaselineAligned(true);
            horizontalView.addView(newButton);
            horizontalView.addView(editText);
            horizontalViews.put(userName,horizontalView);
            parentLayout.addView(horizontalView);
        }
    }

    public void moveCamera(LatLng target) {
        if (googleMap!=null && target!=null) {
            String mapZoomLevel = sharedPreference.getString(getString(R.string.key_map_zoom_level), null);
            String mapTilt = sharedPreference.getString(getString(R.string.key_map_tilt), null);
            String mapBearing = sharedPreference.getString(getString(R.string.key_map_bearing), null);
            float zoom = mapZoomLevel == null ? 12 : Float.parseFloat(mapZoomLevel);
            float tilt = mapTilt == null ? 30 : Float.parseFloat(mapTilt);
            float bearing = mapBearing == null ? 1 : Float.parseFloat(mapBearing);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(target).zoom(zoom).tilt(tilt == 0 ? 30 : tilt).bearing(bearing == 0 ? 1 : bearing).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void removePolyline() {
        if (polyline!=null) {
            polyline.remove();
            polyline = null;
        }
    }

    private void redrawPolyline(ArrayList route) {
        Logger.print("redrawPolyline");
        if (polyline!=null) {
            polyline.remove();
        }
        polyline = googleMap.addPolyline(new PolylineOptions().addAll(route).width(2).color(Color.CYAN));
    }

    private void redrawCircle(final LatLng latLng) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (circle!=null) circle.remove();
                circle = googleMap.addCircle(new CircleOptions().center(latLng).radius(4).strokeColor(Color.BLUE).fillColor(Color.CYAN));
            }
        });
    }

    private void redrawCurrentUserIcon(final LatLng latLng) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (currentUserIcon != null) currentUserIcon.remove();
                currentUserIcon = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.current_user)).position(latLng));
                currentUserIcon.setDraggable(false);
            }
        });
    }

    public static class LocationBroadcastReceiver extends BroadcastReceiver {

        public LocationBroadcastReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle data = intent.getExtras();
            final double latitude;
            final double longitude;
            final double speed;
            final double eta;
            final String userCode;
            final String userName;
            final String timeStamp;
            final String targetLocation;
            final String action = intent.getAction();
            if (data!=null) {

                MainActivity mainActivityInstance = MainActivity.getInstance();
                if (action.equals(Constants.INTENT_DRAW_GPS)) {

                    latitude = Double.parseDouble((String) data.get(Constants.KEY_CURRENT_GPS_LATITUDE));
                    longitude = Double.parseDouble((String) data.get(Constants.KEY_CURRENT_GPS_LONGITUDE));
                    speed = Double.parseDouble((String) data.get(Constants.KEY_CURRENT_SPEED));
                    eta = Double.parseDouble((String) data.get(Constants.KEY_CURRENT_ETA));
                    userCode = (String) data.get(Constants.KEY_USER_CODE);
                    userName = (String) data.get(Constants.KEY_SENDER_USER_NAME);
                    timeStamp = (String) data.get(Constants.KEY_FBASE_SERVER_TIMESTAMP);
                    targetLocation = (String) data.get(Constants.KEY_TARGET_LOCATION);
                    Logger.print("GPS Data Received: Target=" + targetLocation + " lat=" + latitude + " long=" + longitude + " userCode=" + userCode + " userName=" + userName);
                    if (mainActivityInstance != null) {
                        if (userCode != null) {
                            if (mainActivityInstance.userCode.equals(userCode)) {
                                if (mainActivityInstance != null) {
                                    if (mainActivityInstance.currentLocation == null && mainActivityInstance.googleMap != null) {
                                        mainActivityInstance.currentLocation = new LatLng(latitude, longitude);
                                        mainActivityInstance.moveCamera(mainActivityInstance.currentLocation);
                                        mainActivityInstance.initializeMap();
                                    }
                                    if (mainActivityInstance.currentLocation!=null) {
                                        mainActivityInstance.redrawCurrentUserIcon(mainActivityInstance.currentLocation);
                                        if (mainActivityInstance.targetLocationLatLng!=null && mainActivityInstance.markersLoaded) {
                                            ArrayList<LatLng> route = new ArrayList<>();
                                            route.add(mainActivityInstance.currentLocation);
                                            route.add(mainActivityInstance.targetLocationLatLng);
                                            mainActivityInstance.redrawPolyline(route);
                                        }
                                    }
                                }
                            } else mainActivityInstance.updateUserPosition(latitude, longitude, userCode, userName, speed, eta, timeStamp, targetLocation);
                        }
                    }
                }
                else if (action.equals(Constants.INTENT_DISCONNECT)) {
                    userName = (String) data.get(Constants.KEY_SENDER_USER_NAME);
                    mainActivityInstance.removeUserComponents(userName);
                }
            }
        }

    }

    public void updateUserPosition(final double latitude, final double longitude, final String userCode, final String userName, final double speed, final double eta, final String timeStamp, final String targetLocation) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (connectedUsersMarkers != null) {
                    if (connectedUsersMarkers.containsKey(userName)) {
                        Marker peerMarker = (Marker) connectedUsersMarkers.get(userName);
                        connectedUsersMarkers.remove(userName);
                        peerMarker.remove();
                        Marker newPeerMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.peer)).position(new LatLng(latitude, longitude)));
                        newPeerMarker.setDraggable(false);
                        connectedUsersMarkers.put(userName, newPeerMarker);
                        updateUserPanel(userName, Integer.parseInt(userCode), latitude, longitude, speed, eta, timeStamp, targetLocation);
                    } else {
                        Marker newPeerMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.peer)).position(new LatLng(latitude, longitude)));
                        newPeerMarker.setDraggable(false);
                        connectedUsersMarkers.put(userName, newPeerMarker);
                        updateUserPanel(userName, Integer.parseInt(userCode), latitude, longitude, speed, eta, timeStamp, targetLocation);
                    }
                }
            }
        });

    }

}