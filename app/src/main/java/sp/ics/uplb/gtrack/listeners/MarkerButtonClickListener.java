package sp.ics.uplb.gtrack.listeners;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sp.ics.uplb.gtrack.R;
import sp.ics.uplb.gtrack.activities.MainActivity;
import sp.ics.uplb.gtrack.controllers.Contact;
import sp.ics.uplb.gtrack.controllers.Markers;
import sp.ics.uplb.gtrack.utilities.Common;
import sp.ics.uplb.gtrack.utilities.Constants;
import sp.ics.uplb.gtrack.utilities.FirebaseAction;
import sp.ics.uplb.gtrack.utilities.Logger;

public class MarkerButtonClickListener implements Button.OnClickListener {
    private MainActivity mainActivity = null;
    private View newMarkerView = null;
    private TextView statusBarMain = null;

    public MarkerButtonClickListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.statusBarMain = (TextView) mainActivity.findViewById(R.id.status_bar_main);
    }

    private void setEnableMapComponents(boolean value) {
        try {
            mainActivity.googleMap.getUiSettings().setAllGesturesEnabled(value);
            mainActivity.myLocationView.setClickable(value);
            mainActivity.myLocationView.setEnabled(value);
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

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.deleteButton: {
                if (mainActivity.selectedMarker != null) {
                    LayoutInflater li = LayoutInflater.from(mainActivity);
                    View deleteMarkerView = li.inflate(R.layout.new_delete_marker_dialog, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mainActivity);
                    alertDialogBuilder.setView(deleteMarkerView);
                    alertDialogBuilder.setCancelable(false).setPositiveButton(Constants.GLOBAL_YES, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setEnableMapComponents(false);
                            final String title = mainActivity.selectedMarker.getTitle();
                            final String userCode = mainActivity.userCode;
                            AsyncTask deleteMarkerTask = new AsyncTask() {
                                @Override
                                protected void onPostExecute(Object jsonResponse) {
                                    super.onPostExecute(jsonResponse);
                                    String status = sp.ics.uplb.gtrack.utilities.JSONObject.get((JSONObject) jsonResponse,Constants.STATUS_MESSAGE);
                                    if (status != null) {
                                        if (status.equals(Constants.MESSAGE_DELETE_SUCCESSFUL)) {
                                            Iterator i = mainActivity.markersList.iterator();
                                            while (i.hasNext()) {
                                                Marker marker = (Marker) i.next();
                                                if (marker.equals(mainActivity.selectedMarker)) {
                                                    mainActivity.markersList.remove(marker);
                                                    break;
                                                }
                                            }

                                            if (mainActivity.selectedMarker!=null) {
                                                if (mainActivity.mService != null) {
                                                    if (Common.isTargetLocation(mainActivity.mService, mainActivity.selectedMarker))
                                                        mainActivity.mService.setTargetLocation(null, null);
                                                }
                                                mainActivity.selectedMarker.remove();
                                            }

                                            Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.message), mainActivity.getString(R.string.message_marker_deleted));
                                        } else if (status.contains(Constants.STATUS_ERROR))
                                            Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.error), Common.getErrorMessage(mainActivity,status));
                                    }
                                    setEnableMapComponents(true);
                                    Common.showProgress(mainActivity.getApplicationContext(), mainActivity.progressBar, false);
                                }
                                @Override
                                protected void onProgressUpdate(Object[] values) {
                                    super.onProgressUpdate(values);
                                    Common.showProgress(mainActivity.getApplicationContext(), mainActivity.progressBar, true);
                                    Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.message), values[0].toString());
                                }
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    publishProgress(mainActivity.getString(R.string.progress_deletingmarker));
                                    return Markers.deleteMarkerByTitleAndUserId(mainActivity,title,userCode);
                                }
                            };
                            deleteMarkerTask.execute((Void) null);
                        }
                    }).setNegativeButton(Constants.GLOBAL_NO, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                break;
            }
            case R.id.editButton: {
                if (mainActivity.selectedMarker != null) {
                    LayoutInflater li = LayoutInflater.from(v.getContext());
                    newMarkerView = li.inflate(R.layout.create_marker, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                    alertDialogBuilder.setView(newMarkerView);
                    ((EditText) newMarkerView.findViewById(R.id.markerTitle)).setText(mainActivity.selectedMarker.getTitle());
                    ((EditText) newMarkerView.findViewById(R.id.markerDescription)).setText(mainActivity.selectedMarker.getSnippet());
                    alertDialogBuilder.setCancelable(false).setPositiveButton(Constants.GLOBAL_OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setEnableMapComponents(false);
                            final String markerTitle = ((EditText) newMarkerView.findViewById(R.id.markerTitle)).getText().toString();
                            final String markerDescription = ((EditText) newMarkerView.findViewById(R.id.markerDescription)).getText().toString();
                            final String oldTitle = mainActivity.selectedMarker.getTitle();
                            final String userCode = mainActivity.userCode;
                            AsyncTask updateMarkerTask = new AsyncTask() {
                                @Override
                                protected void onPostExecute(Object jsonResponse) {
                                    super.onPostExecute(jsonResponse);
                                    String status = sp.ics.uplb.gtrack.utilities.JSONObject.get((JSONObject) jsonResponse,Constants.STATUS_MESSAGE);
                                    if (status != null) {
                                        if (status.equals(Constants.MESSAGE_UPDATE_SUCCESSFUL)) {
                                            String markerTitle = ((EditText) newMarkerView.findViewById(R.id.markerTitle)).getText().toString();
                                            String markerDescription = ((EditText) newMarkerView.findViewById(R.id.markerDescription)).getText().toString();
                                            mainActivity.selectedMarker.setTitle(markerTitle);
                                            mainActivity.selectedMarker.setSnippet(markerDescription);

                                            if (mainActivity.mService!=null && mainActivity.selectedMarker!=null) {
                                                if (Common.isTargetLocation(mainActivity.mService,mainActivity.selectedMarker)) {
                                                    mainActivity.mService.setTargetLocation(markerTitle,mainActivity.selectedMarker.getPosition());
                                                }
                                            }

                                            mainActivity.selectedMarker.showInfoWindow();
                                            Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.message), mainActivity.getString(R.string.message_marker_updated));
                                        } else if (status.contains(Constants.STATUS_ERROR))
                                            Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.error), Common.getErrorMessage(mainActivity,status));
                                    }
                                    setEnableMapComponents(true);
                                    Common.showProgress(mainActivity.getApplicationContext(), mainActivity.progressBar, false);
                                }
                                @Override
                                protected void onProgressUpdate(Object[] values) {
                                    super.onProgressUpdate(values);
                                    Common.showProgress(mainActivity.getApplicationContext(), mainActivity.progressBar, true);
                                    Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.message), values[0].toString());
                                }
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    publishProgress(mainActivity.getString(R.string.progress_updatingchanges));
                                    return Markers.setTitleAndDescriptionByTitleAndUserId(mainActivity, markerTitle, markerDescription, oldTitle, userCode);
                                }
                            };
                            updateMarkerTask.execute((Void) null);
                        }
                    }).setNegativeButton(Constants.GLOBAL_CANCEL, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                break;
            }
            case R.id.setButton: {
                final Marker selectedMarker = mainActivity.selectedMarker;
                if (selectedMarker!=null) {
                    LayoutInflater li = LayoutInflater.from(mainActivity);
                    View setMarkerView = li.inflate(R.layout.new_set_marker_dialog, null);
                    TextView textView = (TextView) setMarkerView.findViewById(R.id.new_set_marker_dialog);
                    final boolean isSet = mainActivity.markerButtonSet.getText().equals(Constants.BUTTON_TEXT_SET);
                    textView.setText((isSet ? mainActivity.getString(R.string.prompt_set_marker) : mainActivity.getString(R.string.prompt_stop_marker)));
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mainActivity);
                    alertDialogBuilder.setView(setMarkerView);
                    alertDialogBuilder.setCancelable(false).setPositiveButton(Constants.GLOBAL_YES, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setEnableMapComponents(false);
                            final String title = selectedMarker.getTitle();
                            final String userCode = mainActivity.userCode;
                            AsyncTask setMarkerTask = new AsyncTask() {
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
                                            Iterator i = mainActivity.markersList.iterator();
                                            while (i.hasNext()) {
                                                Marker marker = (Marker) i.next();
                                                if (!marker.equals(selectedMarker))
                                                    marker.setIcon(Common.NORMAL_MARKER);
                                            }
                                            mainActivity.markerButtonSet.setText(isSet ? Constants.BUTTON_TEXT_UNSET : Constants.BUTTON_TEXT_SET);
                                            selectedMarker.setIcon(isSet ? Common.TARGET_MARKER : Common.NORMAL_MARKER);

                                            if (mainActivity.mService!=null) {
                                                if (isSet) mainActivity.mService.setTargetLocation(selectedMarker.getTitle(),selectedMarker.getPosition());
                                                else mainActivity.mService.setTargetLocation(null,null);
                                            }

                                            Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.message), mainActivity.getString(isSet ? R.string.message_meeting_point_set : R.string.message_meeting_point_unset));
                                        } else if (status.contains(Constants.STATUS_ERROR))
                                            Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.error), Common.getErrorMessage(mainActivity,status));
                                    }
                                    setEnableMapComponents(true);
                                    Common.showProgress(mainActivity.getApplicationContext(), mainActivity.progressBar, false);
                                }
                                @Override
                                protected void onProgressUpdate(Object[] values) {
                                    super.onProgressUpdate(values);
                                    Common.showProgress(mainActivity.getApplicationContext(), mainActivity.progressBar, true);
                                    Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.message), values[0].toString());
                                }
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    publishProgress(mainActivity.getString(isSet ? R.string.progress_setting_meeting_point : R.string.progress_cancel_meeting));
                                    return Markers.setTargetByTitleAndUserId(mainActivity, Constants.GLOBAL_ZERO, title, userCode);
                                }
                            };
                            setMarkerTask.execute((Void)null);
                        }
                    }).setNegativeButton(Constants.GLOBAL_NO, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    break;
                }
            }
            case R.id.shareButton: {
                if (mainActivity.selectedMarker!=null) {
                    LayoutInflater li = LayoutInflater.from(mainActivity);
                    View shareMarkerView = li.inflate(R.layout.share_marker, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mainActivity);
                    alertDialogBuilder.setView(shareMarkerView);
                    List<Contact> contactList = mainActivity.db.getAllListContacts();
                    Iterator i = contactList.iterator();
                    ArrayList contactListAdapter = new ArrayList();
                    while (i.hasNext()) {
                        Contact contact = (Contact) i.next();
                        contactListAdapter.add(contact.getName());
                    }
                    Logger.print("contactListAdapter:=>"+contactListAdapter.toString());

                    ArrayAdapter adapter = new ArrayAdapter(shareMarkerView.getContext(),android.R.layout.simple_list_item_1,contactListAdapter);
                    final AutoCompleteTextView participantsTextView=(AutoCompleteTextView)shareMarkerView.findViewById(R.id.shareMarkerTextView);
                    participantsTextView.setAdapter(adapter);
                    participantsTextView.setThreshold(1);
                    participantsTextView.setAdapter(adapter);
                    alertDialogBuilder.setCancelable(false).setPositiveButton(Constants.GLOBAL_SHARE, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setEnableMapComponents(false);
                            final String share_recipient = participantsTextView.getText().toString();
                            final Context context = v.getContext();
                            final String marker_title = mainActivity.selectedMarker.getTitle();
                            final String marker_latitude = String.valueOf(mainActivity.selectedMarker.getPosition().latitude);
                            final String marker_longitude = String.valueOf(mainActivity.selectedMarker.getPosition().longitude);
                            AsyncTask shareTask = new AsyncTask() {
                                @Override
                                protected void onPostExecute(Object status) {
                                    super.onPostExecute(status);
                                    if (status != null) {
                                        Logger.print("status="+status);
                                        if (status.toString().equals(Constants.MESSAGE_FIREBASE_PUSH_SUCCESSFUL))
                                            Common.updateStatusBar(statusBarMain, ContextCompat.getColor(context, R.color.message), context.getString(R.string.message_meeting_request));
                                        else if (status.toString().contains(Constants.STATUS_ERROR))
                                            Common.updateStatusBar(statusBarMain, ContextCompat.getColor(context, R.color.error), Common.getErrorMessage(context, status.toString()));
                                    }
                                    setEnableMapComponents(true);
                                    Common.showProgress(mainActivity.getApplicationContext(), mainActivity.progressBar, false);
                                }
                                @Override
                                protected void onProgressUpdate(Object[] values) {
                                    super.onProgressUpdate(values);
                                    Common.showProgress(mainActivity.getApplicationContext(), mainActivity.progressBar, true);
                                    Common.updateStatusBar(statusBarMain, ContextCompat.getColor(context, R.color.message), values[0].toString());
                                }
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    if (mainActivity.db.listContactExists(share_recipient)) {
                                        publishProgress(context.getString(R.string.progress_sending_meeting_requests));
                                        try {
                                            Contact recipient = mainActivity.db.getListContactByUserName(share_recipient);
                                            Logger.print("Sending meeting request to "+recipient.getName()+":"+recipient.getFirebaseId());
                                            return FirebaseAction.push(context, recipient.getFirebaseId(), Constants.KEY_SHARE_FROM + mainActivity.userCode, new JSONObject().put(Constants.KEY_SENDER_USER_NAME, mainActivity.emailAddress).put(Constants.KEY_MARKER_TITLE, marker_title).put(Constants.KEY_MARKER_LATITUDE, marker_latitude).put(Constants.KEY_MARKER_LONGITUDE, marker_longitude).put(Constants.KEY_SENDER_FIREBASEID, mainActivity.firebaseid).toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    else return Constants.ERROR_RECIPIENT_NOT_ON_YOUR_CONTACT_LIST;
                                    return null;
                                }
                            };
                            shareTask.execute((Void) null);
                        }
                    }).setNegativeButton(Constants.GLOBAL_CANCEL, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                    break;
            }
            case R.id.moveButton: {
                if (mainActivity.selectedMarker!=null) {
                    LayoutInflater li = LayoutInflater.from(mainActivity);
                    View setMarkerView = li.inflate(R.layout.move_marker_dialog, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mainActivity);
                    alertDialogBuilder.setView(setMarkerView);
                    alertDialogBuilder.setCancelable(false).setPositiveButton(Constants.GLOBAL_YES, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setEnableMapComponents(false);
                            final LatLng target = mainActivity.googleMap.getCameraPosition().target;
                            final String title = mainActivity.selectedMarker.getTitle();
                            final String userCode = mainActivity.userCode;
                            AsyncTask moveMarkerTask = new AsyncTask() {
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
                                            final String markerTitle = mainActivity.selectedMarker.getTitle();
                                            final String markerDescription = mainActivity.selectedMarker.getSnippet();

                                            if (mainActivity.mService!=null && mainActivity.selectedMarker!=null) {
                                                if (Common.isTargetLocation(mainActivity.mService,mainActivity.selectedMarker)) {
                                                    mainActivity.mService.setTargetLocation(markerTitle,target);
                                                }
                                            }

                                            mainActivity.selectedMarker.remove();
                                            Marker marker = mainActivity.googleMap.addMarker(new MarkerOptions().position(target).title(markerTitle).snippet(markerDescription));
                                            mainActivity.markerButtonEdit.setVisibility(View.VISIBLE);
                                            mainActivity.markerButtonDelete.setVisibility(View.VISIBLE);
                                            mainActivity.markerButtonSet.setVisibility(View.VISIBLE);
                                            mainActivity.markerButtonShare.setVisibility(View.VISIBLE);
                                            mainActivity.markerButtonMove.setVisibility(View.VISIBLE);
                                            mainActivity.selectedMarker = marker;
                                            marker.showInfoWindow();
                                            Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.message), mainActivity.getString(R.string.message_marker_moved_success));
                                        } else if (status.contains(Constants.STATUS_ERROR))
                                            Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.error), Common.getErrorMessage(mainActivity,status));
                                    }
                                    setEnableMapComponents(true);
                                    Common.showProgress(mainActivity.getApplicationContext(), mainActivity.progressBar, false);
                                }
                                @Override
                                protected void onProgressUpdate(Object[] values) {
                                    super.onProgressUpdate(values);
                                    Common.showProgress(mainActivity.getApplicationContext(), mainActivity.progressBar, true);
                                    Common.updateStatusBar(statusBarMain, ContextCompat.getColor(mainActivity, R.color.message), values[0].toString());
                                }
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    publishProgress(mainActivity.getString(R.string.progress_moving_marker));
                                    return Markers.setLatlngByTitleAndUserId(mainActivity,target.latitude,target.longitude,title,userCode);
                                }
                            };
                            moveMarkerTask.execute((Void)null);
                        }
                    }).setNegativeButton(Constants.GLOBAL_NO, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                break;
            }
        }
    }
}
