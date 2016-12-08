package sp.ics.uplb.gtrack.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import java.util.Timer;
import java.util.TimerTask;

import sp.ics.uplb.gtrack.R;
import sp.ics.uplb.gtrack.activities.MainActivity;
import sp.ics.uplb.gtrack.controllers.SharedPref;
import sp.ics.uplb.gtrack.utilities.Common;
import sp.ics.uplb.gtrack.utilities.Constants;
import sp.ics.uplb.gtrack.utilities.Logger;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View contentsView;
    private MainActivity context;

    public CustomInfoWindowAdapter(LayoutInflater layoutInflater,MainActivity context){
        contentsView = layoutInflater.inflate(R.layout.custom_info_contents, null);
        this.context = context;
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView tvTitle = ((TextView)contentsView.findViewById(R.id.title));
        tvTitle.setText(marker.getTitle());
        TextView tvSnippet = ((TextView)contentsView.findViewById(R.id.snippet));
        tvSnippet.setText(marker.getSnippet());
        boolean isTarget = Common.isTargetLocation(context.mService,marker);
        marker.setIcon(BitmapDescriptorFactory.fromResource(!isTarget ? R.drawable.marker : R.drawable.target_marker));
        return contentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}