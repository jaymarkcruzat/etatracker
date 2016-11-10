package sp.ics.uplb.gtrack.utilities;

import org.json.*;

public class JSONArray {

    public static org.json.JSONObject getJSONObject(org.json.JSONArray jsonArray,int index) {
        try {
            return jsonArray.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
