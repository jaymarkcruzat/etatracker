package sp.ics.uplb.gtrack.activities;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import sp.ics.uplb.gtrack.R;
import sp.ics.uplb.gtrack.controllers.Contact;
import sp.ics.uplb.gtrack.controllers.Request;
import sp.ics.uplb.gtrack.controllers.SQLiteDatabaseHandler;
import sp.ics.uplb.gtrack.controllers.SharedPref;
import sp.ics.uplb.gtrack.utilities.Common;
import sp.ics.uplb.gtrack.utilities.Constants;
import sp.ics.uplb.gtrack.utilities.FirebaseAction;
import sp.ics.uplb.gtrack.utilities.JSONParser;
import sp.ics.uplb.gtrack.utilities.Logger;

public class UsersDirectoryActivity extends AppCompatActivity {
    private View progressView;
    private ArrayAdapter adapter = null;
    private ListView listView = null;
    private Bundle params = null;
    private String displayMode = null;
    private HashMap<String,JSONObject> userHashMap = new HashMap<String,JSONObject>();
    private ArrayList<String> userlist = new ArrayList<String>();
    private SQLiteDatabaseHandler db = null;
    private String userCode = null;
    private String userName = null;
    private String userFirebaseID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_directory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressView = findViewById(R.id.user_directory_progress);
        listView = (ListView) findViewById(R.id.user_list);
        params = getIntent().getExtras();
        displayMode = params.get(Constants.USER_LIST_DISPLAY_MODE).toString();
        userCode = SharedPref.getString(getApplicationContext(), Constants.SHARED_PREF, Constants.USER_CODE, null);
        userName = SharedPref.getString(getApplicationContext(), Constants.SHARED_PREF, Constants.USER_EMAIL, null);
        userFirebaseID = SharedPref.getString(getApplicationContext(), Constants.SHARED_PREF, Constants.USER_FIREBASEID, null);
        db = new SQLiteDatabaseHandler(this,userCode);

        setTitle(Constants.GLOBAL_BLANK);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                userHashMap.clear();
                userlist.clear();
                int adapter_res_id = displayMode.equals(Constants.USER_LIST_ACCEPT_REJECT) ? R.layout.adapter_acceptreject : R.layout.adapter_contact_list;
                listView.setAdapter(new ArrayAdapter<String>(UsersDirectoryActivity.this, adapter_res_id, new ArrayList<String>().toArray(new String[0])));
                AsyncTask loadUsersTask = createUsersDirectoryTask();
                Common.showProgress(getApplicationContext(), progressView, true);
                loadUsersTask.execute((Void) null);
                }
            });
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AsyncTask createUsersDirectoryTask = createUsersDirectoryTask();
        Common.showProgress(getApplicationContext(), progressView, true);
        userHashMap.clear();
        createUsersDirectoryTask.execute((Void) null);
    }

    private AsyncTask createUsersDirectoryTask() {
        return new AsyncTask() {
            @Override
            protected void onPostExecute(Object jsonResponse) {
                super.onPostExecute(jsonResponse);
                Common.showProgress(getApplicationContext(), progressView, false);
                String status = sp.ics.uplb.gtrack.utilities.JSONObject.get((JSONObject) jsonResponse,Constants.STATUS_MESSAGE);
                if (status != null) {
                    if (status.equals(Constants.MESSAGE_SELECT_SUCCESSFUL)) {
                        String result = sp.ics.uplb.gtrack.utilities.JSONObject.get((JSONObject) jsonResponse,Constants.JSON_RESULT);
                        JSONArray jsonArray = JSONParser.getJSONArray(result);
                        userlist = new ArrayList<String>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = sp.ics.uplb.gtrack.utilities.JSONArray.getJSONObject(jsonArray,i);
                            String userEmail = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject,"USR_EML");
                            userlist.add(userEmail);
                            userHashMap.put(userEmail, jsonObject);
                        }
                        int adapter_res_id = displayMode.equals(Constants.USER_LIST_ACCEPT_REJECT) ? R.layout.adapter_acceptreject : R.layout.adapter_contact_list;
                        adapter = new ArrayAdapter<String>(UsersDirectoryActivity.this, adapter_res_id, R.id.userItem, userlist);
                        listView.setAdapter(adapter);
                    }
                } else if (status.equals(Constants.MESSAGE_SELECT_EMPTY)) {
                    Snackbar.make(listView, getString(R.string.message_no_records_found), Snackbar.LENGTH_LONG).show();
                } else if (status.contains(Constants.STATUS_ERROR)) {
                    Snackbar.make(listView,Common.getErrorMessage(getApplicationContext(), status),Snackbar.LENGTH_LONG).show();
                }
            }
            @Override
            protected Object doInBackground(Object[] params) {
                publishProgress(getString(R.string.progress_loading_user_directory));
                String userCode = SharedPref.getString(getApplicationContext(),Constants.SHARED_PREF,Constants.USER_CODE,null);
                return displayMode.equals(Constants.USER_LIST_ACCEPT_REJECT) ? Request.getPendingRequestsByUserId(getApplicationContext(),userCode) : Request.getApprovedRequestsByUserId(getApplicationContext(),userCode);
            }
        };
    }

    public void acceptRequest(View v) {
        LinearLayout linearLayout2 = (LinearLayout) v.getParent();
        LinearLayout rootLinearLayout = (LinearLayout) linearLayout2.getParent();
        LinearLayout linearLayout1 = (LinearLayout) rootLinearLayout.getChildAt(0);
        final View progressBar = linearLayout2.getChildAt(0);
        final TextView userDetailsView = (TextView) linearLayout1.getChildAt(1);
        final String name = userDetailsView.getText().toString();
        Common.showProgress(getApplicationContext(), progressBar, true);
        final JSONObject jsonObject = userHashMap.get(name);
        final String requestID = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject,"ID");
        AsyncTask acceptRequestTask = new AsyncTask() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            protected void onPostExecute(Object jsonResponse) {
                super.onPostExecute(jsonResponse);
                String status = sp.ics.uplb.gtrack.utilities.JSONObject.get((JSONObject) jsonResponse,Constants.STATUS_MESSAGE);
                if (status != null) {
                    if (status.equals(Constants.MESSAGE_UPDATE_SUCCESSFUL)) {
                        if (!db.listContactExists(name)) {
                            String firebaseid = null, userid = null;
                            try {
                                firebaseid = jsonObject.get("USR_FIREBASEID").toString();
                                userid = jsonObject.get("USR_ID").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Contact newContact = new Contact(Integer.parseInt(userid),name,firebaseid);
                            db.addListContact(newContact);
                            try {
                                FirebaseAction.push(getApplicationContext(), firebaseid, Constants.KEY_ACCEPT_REQUEST + userCode,
                                        new JSONObject().put(Constants.KEY_USER_CODE,userCode)
                                                        .put(Constants.KEY_USER_NAME, userName)
                                                        .put(Constants.KEY_USER_FIREBASEID, userFirebaseID)
                                                        .toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        userHashMap.remove(userName);
                        userlist.remove(userDetailsView.getText().toString());
                        adapter.notifyDataSetChanged();
                        Snackbar.make(listView,userName+" "+getString(R.string.message_has_been_added),Snackbar.LENGTH_LONG).show();
                    } else if (status.contains(Constants.STATUS_ERROR)) {
                        Snackbar.make(listView,Common.getErrorMessage(getApplicationContext(), status),Snackbar.LENGTH_LONG).show();
                    }
                }
                Common.showProgress(getApplicationContext(), progressBar, false);
            }
            @Override
            protected Object doInBackground(Object[] params) {
                return Request.setRequestStatusById(getApplicationContext(),Constants.GLOBAL_ONE,requestID);
            }
        };
        acceptRequestTask.execute((Void)null);
    }

    public void rejectRequest(View v) {
        LinearLayout linearLayout2 = (LinearLayout) v.getParent();
        LinearLayout rootLinearLayout = (LinearLayout) linearLayout2.getParent();
        LinearLayout linearLayout1 = (LinearLayout) rootLinearLayout.getChildAt(0);
        final View progressBar = linearLayout2.getChildAt(0);
        final TextView userDetailsView = (TextView) linearLayout1.getChildAt(1);
        final String userName = userDetailsView.getText().toString();
        Common.showProgress(getApplicationContext(), progressBar, true);
        JSONObject jsonObject = userHashMap.get(userName);
        final String requestID = sp.ics.uplb.gtrack.utilities.JSONObject.get(jsonObject,"ID");
        AsyncTask rejectRequestTask = new AsyncTask() {
            @Override
            protected void onPostExecute(Object jsonResponse) {
                super.onPostExecute(jsonResponse);
                String status = sp.ics.uplb.gtrack.utilities.JSONObject.get((JSONObject) jsonResponse,Constants.STATUS_MESSAGE);
                if (status != null) {
                    if (status.equals(Constants.MESSAGE_DELETE_SUCCESSFUL)) {
                        userHashMap.remove(userName);
                        userlist.remove(userDetailsView.getText().toString());
                        adapter.notifyDataSetChanged();
                        Snackbar.make(listView,getString(R.string.message_reject_successful)+Constants.GLOBAL_SPACE+userName+" request.",Snackbar.LENGTH_LONG).show();
                    } else if (status.contains(Constants.STATUS_ERROR)) {
                        Snackbar.make(listView,Common.getErrorMessage(getApplicationContext(), status),Snackbar.LENGTH_LONG).show();
                    }
                }
                Common.showProgress(getApplicationContext(), progressBar, false);
            }
            @Override
            protected Object doInBackground(Object[] params) {
                return Request.deleteById(getApplicationContext(),requestID);
            }
        };
        rejectRequestTask.execute((Void)null);
    }
}