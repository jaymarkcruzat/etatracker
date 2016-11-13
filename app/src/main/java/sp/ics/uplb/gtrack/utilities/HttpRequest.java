package sp.ics.uplb.gtrack.utilities;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import sp.ics.uplb.gtrack.R;

public class HttpRequest {

    /*sends http request via post method then returns response in JSON format*/
    public static JSONObject doPost(Context context,String link, String data) {
        URL url = null;
        URLConnection conn = null;
        OutputStreamWriter wr = null;
        BufferedReader reader = null;
        InputStream inputStream = null;
        Logger.print("link="+link+" data="+data);
        try {
            url = new URL(link);
        } catch (MalformedURLException ex) {}

        try {
            conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(Integer.parseInt(context.getString(R.string.server_conn_timeout)));
        } catch (IOException ex) {}

        try {
            wr = new OutputStreamWriter(conn.getOutputStream());
        }
        catch (Exception ex) {
            if (ex.getLocalizedMessage()!=null) {
                if (ex.getLocalizedMessage().contains("ENETUNREACH")) {
                    String response = "{\"code\":7,\"message\":\"error_network_unreachable\"}";
                    JSONObject jsonObject = JSONParser.getJSONObject(response);
                    return jsonObject;
                }
                else if (ex.getLocalizedMessage().contains("EHOSTUNREACH")) {
                    String response = "{\"code\":7,\"message\":\"error_network_unreachable\"}";
                    JSONObject jsonObject = JSONParser.getJSONObject(response);
                    return jsonObject;
                }
                else {
                    Logger.print("Response Error Message: "+ex.getMessage());
                    String response = "{\"code\":4,\"message\":\"error_server_down\"}";
                    JSONObject jsonObject = JSONParser.getJSONObject(response);
                    return jsonObject;
                }
            }
        }

        if (wr!=null) {
            try {
                wr.write(data);
                wr.flush();
            } catch (IOException ex) {}

            if (Common.isInternetConnectionAvailable()) {
                try {
                    inputStream = conn.getInputStream();
                } catch (IOException ex) {}
                reader = new BufferedReader(new InputStreamReader(inputStream));
            }
            else {
                String response = "{\"code\":7,\"message\":\"error_network_unreachable\"}";
                JSONObject jsonObject = JSONParser.getJSONObject(response);
                return jsonObject;
            }

            String line = null;
            StringBuilder sb = new StringBuilder();
            try {
                line = reader.readLine();
            } catch (IOException ex) {}
            while (line != null) {
                sb.append(line);
                try {
                    line = reader.readLine();
                } catch (IOException ex) {}
            }
            try {
                String response = sb.toString();
                Logger.print("real_response:"+response);
                if (response.length()>0 && response.charAt(0)!='{') {
                    if (response.contains("{\"code")) {
                        response = response.substring(response.indexOf("{\"code"));
                    }
                }
                if (JSONParser.validate(response)) {
                    JSONObject jsonObject = JSONParser.getJSONObject(response);
                    return jsonObject;
                }
                else {
                    response = "{\"code\":8,\"message\":\"error_system_admin\"}";
                    JSONObject jsonObject = JSONParser.getJSONObject(response);
                    return jsonObject;
                }
            } catch (Exception e) {
                String response = "{\"code\":8,\"message\":\"error_system_admin\"}";
                JSONObject jsonObject = JSONParser.getJSONObject(response);
                return jsonObject;
            }
        }
        else {
            String response = "{\"code\":8,\"message\":\"error_system_admin\"}";
            JSONObject jsonObject = JSONParser.getJSONObject(response);
            return jsonObject;
        }
    }
}
