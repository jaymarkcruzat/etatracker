package sp.ics.uplb.gtrack.utilities;

import org.json.JSONException;

public class JSONObject {

    public static String get(org.json.JSONObject jsonObject,String key) {
        String value = null;
        try {
            value = jsonObject.get(key).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static org.json.JSONObject put(org.json.JSONObject jsonObject,String key,String value) {
        try {
            return jsonObject.put(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
