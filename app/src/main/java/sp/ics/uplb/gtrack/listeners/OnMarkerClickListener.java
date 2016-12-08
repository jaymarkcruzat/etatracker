package sp.ics.uplb.gtrack.listeners;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import sp.ics.uplb.gtrack.R;
import sp.ics.uplb.gtrack.activities.MainActivity;
import sp.ics.uplb.gtrack.controllers.SharedPref;
import sp.ics.uplb.gtrack.utilities.Common;
import sp.ics.uplb.gtrack.utilities.Constants;

public class OnMarkerClickListener implements GoogleMap.OnMarkerClickListener {
    private MainActivity mainActivity = null;

    public OnMarkerClickListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle()!=null) {

            boolean isTarget = Common.isTargetLocation(mainActivity.mService,marker);
            mainActivity.markerButtonSet.setText(!isTarget ? Constants.BUTTON_TEXT_SET : Constants.BUTTON_TEXT_UNSET);
            mainActivity.markerButtonEdit.setVisibility(View.VISIBLE);
            mainActivity.markerButtonDelete.setVisibility(View.VISIBLE);
            mainActivity.markerButtonSet.setVisibility(View.VISIBLE);
            mainActivity.markerButtonShare.setVisibility(View.VISIBLE);
            mainActivity.markerButtonMove.setVisibility(View.VISIBLE);
            mainActivity.selectedMarker = marker;
            return false;

        }
        return true;
    }
}
