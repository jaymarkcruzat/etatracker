package sp.ics.uplb.gtrack;

import android.app.Application;
import com.firebase.client.Firebase;

public class Tracker extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(getApplicationContext());
    }
}