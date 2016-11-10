package sp.ics.uplb.gtrack.listeners;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import sp.ics.uplb.gtrack.activities.MainActivity;
import sp.ics.uplb.gtrack.utilities.Logger;

public class OnCameraChangeListener implements GoogleMap.OnCameraChangeListener {
    private MainActivity mainActivity;

    public OnCameraChangeListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        mainActivity.currentZoom = cameraPosition.zoom;
        mainActivity.currentTilt = cameraPosition.tilt;
        mainActivity.currentBearing = cameraPosition.bearing;
        Logger.print("zoom="+cameraPosition.zoom);
        Logger.print("tilt="+cameraPosition.tilt);
        Logger.print("bearing="+cameraPosition.bearing);
    }
}
