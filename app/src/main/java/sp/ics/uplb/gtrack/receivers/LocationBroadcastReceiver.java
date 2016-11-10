package sp.ics.uplb.gtrack.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import sp.ics.uplb.gtrack.utilities.Logger;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.print("Main Thread received GPS coordinates");
    }
}
