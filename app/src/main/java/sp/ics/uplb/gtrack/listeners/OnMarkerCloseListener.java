package sp.ics.uplb.gtrack.listeners;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import sp.ics.uplb.gtrack.R;
import sp.ics.uplb.gtrack.activities.MainActivity;

public class OnMarkerCloseListener implements GoogleMap.OnInfoWindowCloseListener {
    private MainActivity mainActivity = null;

    public OnMarkerCloseListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    /*hide edit, set and delete buttons when the marker is unfocused*/
    public void onInfoWindowClose(Marker marker) {
        mainActivity.markerButtonEdit.setVisibility(View.INVISIBLE);
        mainActivity.markerButtonDelete.setVisibility(View.INVISIBLE);
        mainActivity.markerButtonSet.setVisibility(View.INVISIBLE);
        mainActivity.markerButtonShare.setVisibility(View.INVISIBLE);
        mainActivity.markerButtonMove.setVisibility(View.INVISIBLE);
        mainActivity.selectedMarker = null;
    }
}
