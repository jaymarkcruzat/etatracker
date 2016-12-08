package sp.ics.uplb.gtrack.menus.fab;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sp.ics.uplb.gtrack.R;
import sp.ics.uplb.gtrack.activities.MainActivity;
import sp.ics.uplb.gtrack.activities.UsersDirectoryActivity;
import sp.ics.uplb.gtrack.controllers.Markers;
import sp.ics.uplb.gtrack.controllers.Request;
import sp.ics.uplb.gtrack.controllers.User;
import sp.ics.uplb.gtrack.utilities.Common;
import sp.ics.uplb.gtrack.utilities.Constants;
import sp.ics.uplb.gtrack.utilities.FirebaseAction;
import sp.ics.uplb.gtrack.utilities.JSONParser;

public class MenusFragment extends Fragment {

    private FloatingActionMenu faMenu;
    private FloatingActionButton createMarker;
    private FloatingActionButton sendRequest;
    private FloatingActionButton usersDirectory;
    private FloatingActionButton acceptRejectRequest;
    private List<FloatingActionMenu> menus = new ArrayList<>();
    private Handler mUiHandler = new Handler();
    private View newMarkerView = null;
    private String userCode = null;
    private MainActivity mainActivity = null;
    private TextView statusBarMain = null;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menus_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statusBarMain = (TextView)getActivity().findViewById(R.id.status_bar_main);
        faMenu = (FloatingActionMenu) view.findViewById(R.id.faMenu);
        createMarker = (FloatingActionButton) view.findViewById(R.id.createMarker);
        sendRequest = (FloatingActionButton) view.findViewById(R.id.sendRequest);
        usersDirectory = (FloatingActionButton) view.findViewById(R.id.usersDirectory);
        acceptRejectRequest = (FloatingActionButton) view.findViewById(R.id.acceptRejectRequest);

        createMarker.setColorNormal(ContextCompat.getColor(getActivity(), R.color.lightblue));
        createMarker.setColorRipple(ContextCompat.getColor(getActivity(), R.color.lightblue));
        createMarker.setColorPressed(ContextCompat.getColor(getActivity(), R.color.lightblue));

        sendRequest.setColorNormal(ContextCompat.getColor(getActivity(), R.color.lightblue));
        sendRequest.setColorRipple(ContextCompat.getColor(getActivity(), R.color.lightblue));
        sendRequest.setColorPressed(ContextCompat.getColor(getActivity(), R.color.lightblue));

        usersDirectory.setColorNormal(ContextCompat.getColor(getActivity(), R.color.lightblue));
        usersDirectory.setColorRipple(ContextCompat.getColor(getActivity(), R.color.lightblue));
        usersDirectory.setColorPressed(ContextCompat.getColor(getActivity(), R.color.lightblue));

        acceptRejectRequest.setColorNormal(ContextCompat.getColor(getActivity(), R.color.lightblue));
        acceptRejectRequest.setColorRipple(ContextCompat.getColor(getActivity(), R.color.lightblue));
        acceptRejectRequest.setColorPressed(ContextCompat.getColor(getActivity(), R.color.lightblue));

        faMenu.setMenuButtonColorNormal(ContextCompat.getColor(getActivity(), R.color.skyblue));


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createMarker.setOnClickListener(clickListener);
        sendRequest.setOnClickListener(clickListener);
        usersDirectory.setOnClickListener(clickListener);
        acceptRejectRequest.setOnClickListener(clickListener);

        int delay = 400;
        for (final FloatingActionMenu menu : menus) {
            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    menu.showMenuButton(true);
                }
            }, delay);
            delay += 150;
        }
    }

    private void setEnableMapComponents(boolean value) {
        try {
            mainActivity.googleMap.getUiSettings().setAllGesturesEnabled(value);
            mainActivity.myLocationView.setClickable(value);
            mainActivity.markerButtonSet.setEnabled(value);
            mainActivity.markerButtonDelete.setEnabled(value);
            mainActivity.markerButtonEdit.setEnabled(value);
            mainActivity.markerButtonMove.setEnabled(value);
            mainActivity.markerButtonShare.setEnabled(value);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.createMarker: {
                LayoutInflater li = LayoutInflater.from(getContext());
                newMarkerView = li.inflate(R.layout.create_marker, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(newMarkerView);;
                userCode = getActivity().getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE).getString(Constants.USER_CODE, null);
                alertDialogBuilder.setCancelable(false).setPositiveButton(Constants.GLOBAL_OK, new DialogInterface.OnClickListener() {

                public void onClick(final DialogInterface dialog, int id) {
                final String markerTitle = ((EditText) newMarkerView.findViewById(R.id.markerTitle)).getText().toString();
                final String markerDescription = ((EditText) newMarkerView.findViewById(R.id.markerDescription)).getText().toString();
                //if (!mainActivity.containsMarker(markerTitle)) {
                    faMenu.close(true);
                    final LatLng target = mainActivity.googleMap.getCameraPosition().target;
                    AsyncTask createMarkerTask = new AsyncTask() {
                    @Override
                    protected void onPostExecute(Object jsonResponse) {
                    super.onPostExecute(jsonResponse);
                    String status = sp.ics.uplb.gtrack.utilities.JSONObject.get((JSONObject) jsonResponse,Constants.STATUS_MESSAGE);
                    if (status != null) {
                        if (status.equals(Constants.MESSAGE_INSERT_SUCCESSFUL)) {
                        String markerTitle = ((EditText) newMarkerView.findViewById(R.id.markerTitle)).getText().toString();
                        String markerDescription = ((EditText) newMarkerView.findViewById(R.id.markerDescription)).getText().toString();
                        Marker marker = mainActivity.googleMap.addMarker(new MarkerOptions().position(target).title(markerTitle).snippet(markerDescription));
                        marker.setDraggable(false);
                        marker.showInfoWindow();
                        Button markerButtonEdit = (Button) mainActivity.findViewById(R.id.editButton);
                        Button markerButtonDelete = (Button) mainActivity.findViewById(R.id.deleteButton);
                        Button markerButtonSet = (Button) mainActivity.findViewById(R.id.setButton);
                        Button markerButtonShare = (Button) mainActivity.findViewById(R.id.shareButton);
                        Button markerButtonMove = (Button) mainActivity.findViewById(R.id.moveButton);
                        markerButtonSet.setText(Constants.BUTTON_TEXT_SET);
                        markerButtonEdit.setVisibility(View.VISIBLE);
                        markerButtonDelete.setVisibility(View.VISIBLE);
                        markerButtonSet.setVisibility(View.VISIBLE);
                        markerButtonShare.setVisibility(View.VISIBLE);
                        markerButtonMove.setVisibility(View.VISIBLE);
                        mainActivity.selectedMarker = marker;
                        mainActivity.markersList.add(marker);
                        Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.message), mainActivity.getString(R.string.message_marker_created));
                    } else if (status.contains(Constants.STATUS_ERROR))
                        Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.error), Common.getErrorMessage(mainActivity,status));
                    }
                    setEnableMapComponents(true);
                    }
                    @Override
                    protected void onProgressUpdate(Object[] values) {
                    super.onProgressUpdate(values);
                    Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.message), values[0].toString());
                    setEnableMapComponents(false);
                    }
                    @Override
                    protected Object doInBackground(Object[] params) {
                    publishProgress(mainActivity.getString(R.string.progress_savingmarker));
                    return Markers.create(getContext(),markerTitle,markerDescription,target.latitude,target.longitude,Constants.GLOBAL_ZERO,userCode);
                    }
                    };
                    createMarkerTask.execute((Void)null);
                /*} else {
                    new AlertDialog.Builder(getContext()).setMessage(getString(R.string.error_duplicate_marker_not_allowed)).setPositiveButton(Constants.GLOBAL_OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            v.performClick();
                        }
                }).show();
                }*/
                }
                }).setNegativeButton(Constants.GLOBAL_CANCEL, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    faMenu.close(true);
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            }
            case R.id.sendRequest: {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                final View view = layoutInflater.inflate(R.layout.send_request, null);
                AlertDialog.Builder inputDialogBuilder = new AlertDialog.Builder(getContext());
                inputDialogBuilder.setView(view);
                inputDialogBuilder.setCancelable(false).setPositiveButton(Constants.GLOBAL_SEND, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                final String emailAddressParticipant = ((EditText) view.findViewById(R.id.emailAddressTextView)).getText().toString();
                if (emailAddressParticipant != null && emailAddressParticipant.length() > 0) {
                if (Common.isEmailValid(emailAddressParticipant)) {
                if (!emailAddressParticipant.equals(mainActivity.emailAddress)) {
                    AsyncTask checkIfUserRegisteredTask = new AsyncTask() {
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
                    if (status.equals(Constants.MESSAGE_SELECT_SUCCESSFUL)) {
                        String result = null, recipient = null;
                        try {
                        result = ((JSONObject) jsonResponse).get(Constants.JSON_RESULT).toString();
                        JSONArray jsonArray = JSONParser.getJSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            recipient = jsonObject.get("ID").toString();
                            break;
                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final String finalRecipient = recipient;
                        final String userCode = mainActivity.userCode;
                        AsyncTask checkIfRequestAlreadySentTask = new AsyncTask() {
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
                        if (status.equals(Constants.MESSAGE_SELECT_SUCCESSFUL)) {
                            Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getActivity().getApplicationContext(), R.color.error), getString(R.string.error_request_already_sent));
                        } else if (status.equals(Constants.MESSAGE_SELECT_EMPTY)) {
                            final String userCode = mainActivity.userCode;
                            AsyncTask sendRequest = new AsyncTask() {
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
                            if (status.equals(Constants.MESSAGE_INSERT_SUCCESSFUL)) {
                                AsyncTask sendNotificationTask = new AsyncTask() {
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
                                if (status.equals(Constants.MESSAGE_SELECT_SUCCESSFUL)) {
                                    String result;
                                    String firebaseid = null;
                                    try {
                                    result = ((JSONObject) jsonResponse).get(Constants.JSON_RESULT).toString();
                                    JSONArray jsonArray = JSONParser.getJSONArray(result);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        firebaseid = jsonObject.get("USR_FIREBASEID").toString();
                                        break;
                                    }
                                    final String finalFirebaseid = firebaseid;
                                    AsyncTask pushToFirebaseTask = new AsyncTask() {
                                    String status = null;
                                    @Override
                                    protected void onPostExecute(Object jsonResponse) {
                                    if (status.equals(Constants.MESSAGE_FIREBASE_PUSH_SUCCESSFUL)) Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.message), String.format(mainActivity.getString(R.string.message_request_send_successful),emailAddressParticipant));
                                    else Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getActivity().getApplicationContext(), R.color.error), Common.getErrorMessage(getActivity().getApplicationContext(), status));
                                    }
                                    @Override
                                    protected Object doInBackground(Object[] params) {
                                    status = FirebaseAction.push(getContext(), finalFirebaseid, Constants.KEY_REQUEST_FROM + mainActivity.userCode, mainActivity.emailAddress);
                                    return null;
                                    }
                                    };
                                    pushToFirebaseTask.execute((Void)null);
                                    } catch (JSONException e) {e.printStackTrace();}
                                }else
                                if (status.contains(Constants.STATUS_ERROR)) Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getActivity().getApplicationContext(), R.color.error), Common.getErrorMessage(getActivity().getApplicationContext(), status));
                                }
                                }
                                @Override
                                protected Object doInBackground(Object[] params){
                                return User.getFirebaseIdByUserName(getContext(),emailAddressParticipant);
                                }
                                };
                                sendNotificationTask.execute((Void)null);
                            }else
                            if (status.contains(Constants.STATUS_ERROR)) Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.error), Common.getErrorMessage(mainActivity, status));
                            }
                            }
                            @Override
                            protected void onProgressUpdate (Object[]values){
                            super.onProgressUpdate(values);
                            Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.message), values[0].toString());
                            }
                            @Override
                            protected Object doInBackground(Object[]params){
                            publishProgress(mainActivity.getString(R.string.progress_sendingrequest));
                            return Request.create(mainActivity,userCode,finalRecipient,Constants.GLOBAL_ZERO);
                            }
                            };
                            sendRequest.execute((Void)null);
                            }else
                            if (status.contains(Constants.STATUS_ERROR))
                            Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getActivity().getApplicationContext(), R.color.error), Common.getErrorMessage(getActivity().getApplicationContext(), status));
                        }
                        }
                        @Override
                        protected void onProgressUpdate (Object[]values){
                        super.onProgressUpdate(values);
                        Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getActivity().getApplicationContext(), R.color.message), values[0].toString());
                        }
                        @Override
                        protected Object doInBackground
                        (Object[]params){
                        publishProgress(getString(R.string.progress_checkingduplicaterequest));
                        return Request.getDuplicateRequestByUserIdAndRecipient(getActivity().getApplicationContext(),userCode,finalRecipient);
                        }
                        };
                        checkIfRequestAlreadySentTask.execute((Void)null);
                        Common.updateStatusBar(statusBarMain,ContextCompat.getColor(getActivity().getApplicationContext(),R.color.message),getString(R.string.message_select_successful));
                    }
                    else
                    if (status.equals(Constants.MESSAGE_SELECT_EMPTY)) {
                        Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getActivity().getApplicationContext(), R.color.error), getString(R.string.error_participant_not_yet_registered));
                    } else if (status.contains(Constants.STATUS_ERROR))
                        Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getActivity().getApplicationContext(), R.color.error), Common.getErrorMessage(getActivity().getApplicationContext(), status));
                    }
                    }
                    @Override
                    protected void onProgressUpdate (Object[]values){
                    super.onProgressUpdate(values);
                    Common.updateStatusBar(statusBarMain, ContextCompat.getColor(getActivity().getApplicationContext(), R.color.message), values[0].toString());
                    }
                    @Override
                    protected Object doInBackground (Object[]params){
                    publishProgress(getString(R.string.progress_validatingaccount));
                    return User.getIdByUserName(getActivity().getApplicationContext(),emailAddressParticipant);
                    }
                    };
                    checkIfUserRegisteredTask.execute((Void)null);
                    }
                    else Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.error), getString(R.string.error_participant_cant_be_own_email));
                } else Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.error), getString(R.string.error_invalid_email));
                } else Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.error), getString(R.string.error_email_required));
                faMenu.close(true);
                }
                }).setNegativeButton(Constants.GLOBAL_CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        faMenu.close(true);
                    }
                });
                inputDialogBuilder.show();
                break;
            }
            case R.id.usersDirectory: {
                Intent usersDirectoryActivity = new Intent(mainActivity, UsersDirectoryActivity.class);
                usersDirectoryActivity.putExtra(Constants.USER_LIST_DISPLAY_MODE,Constants.USER_LIST_CONTACTS);
                startActivity(usersDirectoryActivity);
                break;
            }
            case R.id.acceptRejectRequest: {
                Intent usersDirectoryActivity = new Intent(mainActivity, UsersDirectoryActivity.class);
                usersDirectoryActivity.putExtra(Constants.USER_NAME,mainActivity.emailAddress);
                usersDirectoryActivity.putExtra(Constants.USER_FIREBASEID,mainActivity.firebaseid);
                usersDirectoryActivity.putExtra(Constants.USER_LIST_DISPLAY_MODE,Constants.USER_LIST_ACCEPT_REJECT);
                startActivity(usersDirectoryActivity);
                break;
            }
        }
        }
    };
}