package sp.ics.uplb.gtrack.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {

    /*converts a string into a JSONObject*/
    public static JSONObject getJSONObject(String response) {
        JSONObject jsonObject = null;
        try {
            Logger.print("response="+response);
            jsonObject = new JSONObject(response);
        } catch (JSONException e) {}
        return jsonObject;
    }

    /*converts a string into a JSONArray*/
    public static JSONArray getJSONArray(String result) {
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        try {
            jsonObject = new JSONObject(result);
            jsonArray = jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    /*checks if the response is a valid JSON String*/
    public static boolean validate(String response) {
        if (response!=null) {
            if (response.length()>0) {
                if (response.charAt(0)=='{') return true;
                else {
                    Logger.print(response);
                    return false;
                }
            }
            else return false;
        }
        else return false;
    }
}
