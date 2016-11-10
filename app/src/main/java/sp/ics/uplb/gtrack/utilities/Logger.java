package sp.ics.uplb.gtrack.utilities;

import android.util.Log;

public class Logger {
    final static String tag = "Debug";

    /*displays message to the console using 'Debug' as tag*/
    public static void print(String msg) {
        Log.d(tag,msg);
    }
}
