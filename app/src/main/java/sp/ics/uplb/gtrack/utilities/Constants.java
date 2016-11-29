package sp.ics.uplb.gtrack.utilities;

public class Constants {

    public static final String DEFAULT_ZOOM                                 = "15";
    public static final int FIREBASE_PRIORITY_HIGH                          = 9999;
    public static final int END_ROUTE                                       = -1;
    public static final long LOCATION_REQUEST_INTERVAL                      = 5000;
    public static final long MIN_DISTANCE_TO_REACH_TARGET                   = 1;
    public static final float SMALLEST_DISPLACEMENT                         = 1;

    public static final String INTENT_DRAW_GPS                              = "sp.ics.uplb.gtrack.DRAW_GPS_INTENT";
    public static final String INTENT_DISCONNECT                            = "sp.ics.uplb.gtrack.DISCONNECT";
    public static final String INTENT_SET_TARGET                            = "sp.ics.uplb.gtrack.SET_TARGET";

    public static final String GLOBAL_BLANK                                 = "";
    public static final String GLOBAL_SPACE                                 = " ";
    public static final String GLOBAL_ZERO                                  = "0";
    public static final float GLOBAL_TRANSPARENT                            = 0;
    public static final String GLOBAL_ONE                                   = "1";
    public static final String GLOBAL_NEW_LINE                              = "\n";
    public static final String GLOBAL_COMMA                                 = ",";
    public static final String GLOBAL_YES                                   = "YES";
    public static final String GLOBAL_NO                                    = "NO";
    public static final String GLOBAL_OK                                    = "OK";
    public static final String GLOBAL_CANCEL                                = "CANCEL";
    public static final String GLOBAL_SEND                                  = "SEND";
    public static final String GLOBAL_SHARE                                 = "SHARE";
    public static final String GLOBAL_ACCEPT                                = "ACCEPT";
    public static final String GLOBAL_REJECT                                = "REJECT";
    public static final String GLOBAL_NONE                                  = "NONE";

    public static final String FIREBASE_APP                                 = "https://android-projects-1295.firebaseio.com/";
    public static final String FIREBASE_KEY                                 = "firebase_key";
    public static final String FIREBASE_VALUE                               = "firebase_value";
    public static final String FIREBASE_VALUE_SEPARATOR                     = "/";
    public static final String SHARED_PREF                                  = "trackerapp";

    public static final String USER_EMAIL                                   = "user_email";
    public static final String USER_NAME                                    = "user_name";
    public static final String USER_CODE                                    = "user_code";
    public static final String USER_FIREBASEID                              = "user_firebaseid";
    public static final String TARGET_LOCATION                              = "target_location";
    public static final String TARGET_LOCATION_LATITUDE                     = "target_location_latitude";
    public static final String TARGET_LOCATION_LONGITUDE                    = "target_location_longitude";
    public static final String DISCONNECT                                   = "disconnect";

    public static final String STATUS_MESSAGE                               = "message";
    public static final String STATUS_ERROR                                 = "error";

    public static final String KEY_REQUEST_FROM                             = "request_from";
    public static final String KEY_ACCEPT_REQUEST                           = "accept_request";
    public static final String KEY_SHARE_FROM                               = "share_from";
    public static final String KEY_ACCEPT_SHARE                             = "accept_share";
    public static final String KEY_MARKER_TITLE                             = "marker_title";
    public static final String KEY_MARKER_LATITUDE                          = "marker_latitude";
    public static final String KEY_MARKER_LONGITUDE                         = "marker_longitude";
    public static final String KEY_SENDER_USER_NAME                         = "sender_user_name";
    public static final String KEY_SENDER_USER_CODE                         = "sender_user_code";
    public static final String KEY_SENDER_FIREBASEID                        = "sender_firebaseid";
    public static final String KEY_RECEIVER_FIREBASEID                      = "receiver_firebaseid";
    public static final String KEY_RECEIVER_USER_NAME                       = "receiver_user_name";
    public static final String KEY_CURRENT_GPS                              = "current_gps";
    public static final String KEY_CURRENT_GPS_LATITUDE                     = "current_gps_lat";
    public static final String KEY_CURRENT_GPS_LONGITUDE                    = "current_gps_lng";
    public static final String KEY_CURRENT_SPEED                            = "current_speed";
    public static final String KEY_CURRENT_ETA                              = "current_eta";
    public static final String KEY_ACK_GPS_RECEIVED                         = "ack_gps_received";
    public static final String KEY_USER_CODE                                = "user_code";
    public static final String KEY_USER_NAME                                = "user_name";
    public static final String KEY_USER_FIREBASEID                          = "user_firebaseid";
    public static final String KEY_SEND_DISCONNECT_REQ                      = "send_disconnect_req";
    public static final String KEY_RELEASE_GPS_RECEIVER_LOCK                = "release_gps_receiver_lock";
    public static final String KEY_FBASE_SERVER_TIMESTAMP                   = "fbase_server_timestamp";
    public static final String KEY_TARGET_LOCATION                          = "target_location";
    public static final String KEY_LOG_MESSAGE                              = "log_message";

    public static final String HTTP_POST_USERNAME                           = "username";
    public static final String HTTP_POST_PASSWORD                           = "password";
    public static final String HTTP_POST_EMAIL                              = "email";
    public static final String HTTP_POST_DEVICEID                           = "deviceid";
    public static final String HTTP_POST_DBHOST                             = "dbhost";
    public static final String HTTP_POST_DBUSER                             = "dbuser";
    public static final String HTTP_POST_DBNAME                             = "dbname";
    public static final String HTTP_POST_DBPASSWORD                         = "dbpassword";
    public static final String HTTP_POST_SQLSTATEMENT                       = "sqlStatement";

    public static final String JSON_MESSAGE                                 = "message";
    public static final String JSON_USERNAME                                = "username";
    public static final String JSON_USERID                                  = "userid";
    public static final String JSON_FIREBASEID                              = "firebaseid";
    public static final String JSON_RESULT                                  = "result";
    public static final String JSON_DATA                                    = "data";

    public static final String ERROR_INCORRECT_PASSWORD                     = "error_incorrect_password";
    public static final String ERROR_REG_FAILED                             = "error_reg_failed";
    public static final String ERROR_SERVER_DOWN                            = "error_server_down";
    public static final String ERROR_UPDATE_FAILED                          = "error_update_failed";
    public static final String ERROR_NETWORK_UNREACHABLE                    = "error_network_unreachable";
    public static final String ERROR_SYSTEM_ADMIN                           = "error_system_admin";
    public static final String ERROR_INSERT_FAILED                          = "error_insert_failed";
    public static final String ERROR_DELETE_FAILED                          = "error_delete_failed";
    public static final String ERROR_SELECT_FAILED                          = "error_select_failed";
    public static final String ERROR_INVALID_EMAIL                          = "error_invalid_email";
    public static final String ERROR_INVALID_PASSWORD                       = "error_invalid_password";
    public static final String ERROR_EMAIL_REQUIRED                         = "error_email_required";
    public static final String ERROR_PASSWORD_REQUIRED                      = "error_password_required";
    public static final String ERROR_GPS_DISABLED                           = "error_gps_disabled";
    public static final String ERROR_PARTICIPANT_NOT_YET_REGISTERED         = "error_participant_not_yet_registered";
    public static final String ERROR_REQUEST_ALREADY_SENT                   = "error_request_already_sent";
    public static final String ERROR_PARTICIPANT_CANT_BE_OWN_EMAIL          = "error_participant_cant_be_own_email";
    public static final String ERROR_UNABLE_TO_GENERATE_ID                  = "error_unable_to_generate_id";
    public static final String ERROR_PASSWORD_ENCRYPTION                    = "error_password_encryption";
    public static final String ERROR_MULTIPLE_LOGIN_INVALID                 = "error_multiple_login_not_allowed";
    public static final String ERROR_RECIPIENT_NOT_ON_YOUR_CONTACT_LIST     = "error_recipient_not_on_your_contact_list";
    public static final String ERROR_DEVICE_IS_ALREADY_REGISTERED           = "error_device_is_already_registered";

    public static final String MESSAGE_LOGIN_SUCCESSFUL                     = "message_login_successful";
    public static final String MESSAGE_REG_SUCCESSFUL                       = "message_reg_successful";
    public static final String MESSAGE_UPDATE_SUCCESSFUL                    = "message_update_successful";
    public static final String MESSAGE_INSERT_SUCCESSFUL                    = "message_insert_successful";
    public static final String MESSAGE_DELETE_SUCCESSFUL                    = "message_delete_successful";
    public static final String MESSAGE_SELECT_SUCCESSFUL                    = "message_select_successful";
    public static final String MESSAGE_SELECT_EMPTY                         = "message_select_empty";
    public static final String MESSAGE_FIREBASE_PUSH_SUCCESSFUL             = "message_firebase_push_successful";

    public static final String BUTTON_TEXT_SET                              = "GO";
    public static final String BUTTON_TEXT_UNSET                            = "STOP";

    public static final String USER_LIST_DISPLAY_MODE                       = "user_list_display_mode";
    public static final String USER_LIST_ACCEPT_REJECT                      = "Incoming Requests";
    public static final String USER_LIST_CONTACTS                           = "Contacts";

    public static final int DATABASE_VERSION                                = 1;
    public static final String DATABASE_NAME                                = "ApplicationDB";
    public static final String TABLE_CONTACTS_CONNECTED                     = "contacts_connected";
    public static final String TABLE_CONTACTS_LIST                          = "contacts_list";
    public static final String TABLE_NODE                                   = "node";

    public static final String KEY_ID                                       = "id";
    public static final String KEY_NAME                                     = "name";
    public static final String KEY_FIREBASEID                               = "firebaseid";
    public static final String KEY_NODE_LATITUDE                            = "node_latitude";
    public static final String KEY_NODE_LONGITUDE                           = "node_longitude";
    public static final String KEY_NODE_DEST_LATITUDE                       = "node_dest_latitude";
    public static final String KEY_NODE_DEST_LONGITUDE                      = "node_dest_longitude";
    public static final String KEY_NODE_NEXT                                = "node_next";
}