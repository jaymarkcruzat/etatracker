package sp.ics.uplb.gtrack.listeners;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;

import sp.ics.uplb.gtrack.R;
import sp.ics.uplb.gtrack.activities.MainActivity;
import sp.ics.uplb.gtrack.utilities.Constants;

public class GooglePlaceSelectionListener implements PlaceSelectionListener {
    private MainActivity mainActivity = null;

    public GooglePlaceSelectionListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onPlaceSelected(Place place) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), mainActivity.sharedPreference.getString(mainActivity.getString(R.string.key_map_zoom_level), null) == null ? 12 : Float.parseFloat(mainActivity.sharedPreference.getString(mainActivity.getString(R.string.key_map_zoom_level), null)));
        mainActivity.googleMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onError(Status status) {
    }
}
