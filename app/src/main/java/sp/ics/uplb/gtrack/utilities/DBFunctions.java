package sp.ics.uplb.gtrack.utilities;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

import sp.ics.uplb.gtrack.R;

public class DBFunctions {

    public static JSONObject sqlSelect(Context context,String sqlStatement) {
        String link, data, status=null;
        int MAX_ITERATION = Integer.parseInt(context.getString(R.string.sqltask_max_iter)), iter = 0;
        JSONObject jsonResponse = null;
        Logger.print("sqlStatement="+sqlStatement);
        while (status==null || (!status.equals(Constants.MESSAGE_SELECT_SUCCESSFUL) && status.equals(Constants.ERROR_NETWORK_UNREACHABLE) && (++iter <= MAX_ITERATION))) {
            link=context.getString(R.string.server_protocol) + context.getString(R.string.server_ip) + "/" + context.getString(R.string.app_name) + "/" + context.getString(R.string.php_select);
            try {
                data =  URLEncoder.encode(Constants.HTTP_POST_DBHOST, context.getString(R.string.encrypt_encoding))       + "=" + URLEncoder.encode(context.getString(R.string.server_ip), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_DBUSER, context.getString(R.string.encrypt_encoding))       + "=" + URLEncoder.encode(context.getString(R.string.server_dbuser), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_DBNAME, context.getString(R.string.encrypt_encoding))       + "=" + URLEncoder.encode(context.getString(R.string.server_dbname), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_DBPASSWORD, context.getString(R.string.encrypt_encoding))   + "=" + URLEncoder.encode(context.getString(R.string.server_dbpassword), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_SQLSTATEMENT, context.getString(R.string.encrypt_encoding)) + "=" + URLEncoder.encode(sqlStatement, context.getString(R.string.encrypt_encoding));
            }
            catch (java.io.UnsupportedEncodingException ex) {
                Logger.print(ex.getMessage());
                break;
            }
            jsonResponse = HttpRequest.doPost(context,link,data);
            try {
                status = jsonResponse.getString(Constants.STATUS_MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonResponse;
    }

    public static JSONObject sqlInsert(Context context,String sqlStatement) {
        String link, data, status=null;
        int MAX_ITERATION = Integer.parseInt(context.getString(R.string.sqltask_max_iter)), iter = 0;
        JSONObject jsonResponse = null;
        Logger.print("sqlStatement="+sqlStatement);
        while (status==null || (!status.equals(Constants.MESSAGE_INSERT_SUCCESSFUL) && status.equals(Constants.ERROR_NETWORK_UNREACHABLE) && (++iter <= MAX_ITERATION))) {
            link=context.getString(R.string.server_protocol) + context.getString(R.string.server_ip) + "/" + context.getString(R.string.app_name) + "/" + context.getString(R.string.php_insert);
            try {
                data =  URLEncoder.encode(Constants.HTTP_POST_DBHOST, context.getString(R.string.encrypt_encoding))       + "=" + URLEncoder.encode(context.getString(R.string.server_ip), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_DBUSER, context.getString(R.string.encrypt_encoding))       + "=" + URLEncoder.encode(context.getString(R.string.server_dbuser), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_DBNAME, context.getString(R.string.encrypt_encoding))       + "=" + URLEncoder.encode(context.getString(R.string.server_dbname), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_DBPASSWORD, context.getString(R.string.encrypt_encoding))   + "=" + URLEncoder.encode(context.getString(R.string.server_dbpassword), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_SQLSTATEMENT, context.getString(R.string.encrypt_encoding)) + "=" + URLEncoder.encode(sqlStatement, context.getString(R.string.encrypt_encoding));
            }
            catch (java.io.UnsupportedEncodingException ex) {
                Logger.print(ex.getMessage());
                break;
            }
            jsonResponse = HttpRequest.doPost(context,link,data);
            try {
                status = jsonResponse.getString(Constants.STATUS_MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonResponse;
    }

    public static JSONObject sqlUpdate(Context context,String sqlStatement) {
        String link, data, status=null;
        int MAX_ITERATION = Integer.parseInt(context.getString(R.string.sqltask_max_iter)), iter = 0;
        JSONObject jsonResponse = null;
        Logger.print("sqlStatement="+sqlStatement);
        while (status==null || (!status.equals(Constants.MESSAGE_UPDATE_SUCCESSFUL) && status.equals(Constants.ERROR_NETWORK_UNREACHABLE) && (++iter <= MAX_ITERATION))) {
            link=context.getString(R.string.server_protocol) + context.getString(R.string.server_ip) + "/" + context.getString(R.string.app_name) + "/" + context.getString(R.string.php_update);
            try {
                data =  URLEncoder.encode(Constants.HTTP_POST_DBHOST, context.getString(R.string.encrypt_encoding))       + "=" + URLEncoder.encode(context.getString(R.string.server_ip), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_DBUSER, context.getString(R.string.encrypt_encoding))       + "=" + URLEncoder.encode(context.getString(R.string.server_dbuser), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_DBNAME, context.getString(R.string.encrypt_encoding))       + "=" + URLEncoder.encode(context.getString(R.string.server_dbname), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_DBPASSWORD, context.getString(R.string.encrypt_encoding))   + "=" + URLEncoder.encode(context.getString(R.string.server_dbpassword), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_SQLSTATEMENT, context.getString(R.string.encrypt_encoding)) + "=" + URLEncoder.encode(sqlStatement, context.getString(R.string.encrypt_encoding));
            }
            catch (java.io.UnsupportedEncodingException ex) {
                Logger.print(ex.getMessage());
                break;
            }
            jsonResponse = HttpRequest.doPost(context,link,data);
            try {
                status = jsonResponse.getString(Constants.STATUS_MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonResponse;
    }

    public static JSONObject sqlDelete(Context context,String sqlStatement) {
        String link, data, status=null;
        JSONObject jsonResponse = null;
        int MAX_ITERATION = Integer.parseInt(context.getString(R.string.sqltask_max_iter)), iter = 0;
        Logger.print("sqlStatement="+sqlStatement);
        while (status==null || (!status.equals(Constants.MESSAGE_DELETE_SUCCESSFUL) && status.equals(Constants.ERROR_NETWORK_UNREACHABLE) && (++iter <= MAX_ITERATION))) {
            link=context.getString(R.string.server_protocol) + context.getString(R.string.server_ip) + "/" + context.getString(R.string.app_name) + "/" + context.getString(R.string.php_delete);
            try {
                data =  URLEncoder.encode(Constants.HTTP_POST_DBHOST, context.getString(R.string.encrypt_encoding))       + "=" + URLEncoder.encode(context.getString(R.string.server_ip), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_DBUSER, context.getString(R.string.encrypt_encoding))       + "=" + URLEncoder.encode(context.getString(R.string.server_dbuser), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_DBNAME, context.getString(R.string.encrypt_encoding))       + "=" + URLEncoder.encode(context.getString(R.string.server_dbname), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_DBPASSWORD, context.getString(R.string.encrypt_encoding))   + "=" + URLEncoder.encode(context.getString(R.string.server_dbpassword), context.getString(R.string.encrypt_encoding)) +"&"+
                        URLEncoder.encode(Constants.HTTP_POST_SQLSTATEMENT, context.getString(R.string.encrypt_encoding)) + "=" + URLEncoder.encode(sqlStatement, context.getString(R.string.encrypt_encoding));
            }
            catch (java.io.UnsupportedEncodingException ex) {
                Logger.print(ex.getMessage());
                break;
            }
            jsonResponse = HttpRequest.doPost(context,link,data);
            try {
                status = jsonResponse.getString(Constants.STATUS_MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonResponse;
    }

}
