package sp.ics.uplb.gtrack.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import sp.ics.uplb.gtrack.utilities.Constants;
import sp.ics.uplb.gtrack.utilities.Logger;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    public SQLiteDatabaseHandler(Context context,String userCode) {
        super(context, Constants.DATABASE_NAME + userCode, null, Constants.DATABASE_VERSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Logger.print("SQLite DbName: "+this.getDatabaseName()+"has been created.");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.print("SQLiteDatabaseHandler onCreate");
        String CREATE_CONTACTS_CONNECTED_TABLE = "CREATE TABLE " + Constants.TABLE_CONTACTS_CONNECTED + "(" + Constants.KEY_ID + " INTEGER PRIMARY KEY," + Constants.KEY_NAME + " TEXT," + Constants.KEY_FIREBASEID + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_CONNECTED_TABLE);
        String CREATE_CONTACTS_LIST_TABLE = "CREATE TABLE " + Constants.TABLE_CONTACTS_LIST + "(" + Constants.KEY_ID + " INTEGER PRIMARY KEY," + Constants.KEY_NAME + " TEXT," + Constants.KEY_FIREBASEID + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_LIST_TABLE);
        String CREATE_NODE_TABLE = "CREATE TABLE " + Constants.TABLE_NODE + "(" + Constants.KEY_ID + " INTEGER PRIMARY KEY," + Constants.KEY_NODE_LATITUDE + " DOUBLE," + Constants.KEY_NODE_LONGITUDE + " DOUBLE," + Constants.KEY_NODE_DEST_LATITUDE + " DOUBLE," + Constants.KEY_NODE_DEST_LONGITUDE + " DOUBLE," +Constants.KEY_NODE_NEXT + " INTEGER" + ")";
        db.execSQL(CREATE_NODE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_CONTACTS_CONNECTED);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_CONTACTS_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NODE);
        onCreate(db);
    }

    //CONTACTS_CONNECTED TABLE

    public void addConnectedContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_ID, contact.getID());
        values.put(Constants.KEY_NAME, contact.getName());
        values.put(Constants.KEY_FIREBASEID, contact.getFirebaseId());
        db.insert(Constants.TABLE_CONTACTS_CONNECTED, null, values);
        db.close();
    }

    public boolean connectedContactExists(String name) {
        String selectQuery = "SELECT  * FROM " + Constants.TABLE_CONTACTS_CONNECTED + " WHERE " + Constants.KEY_NAME + "='" + name +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount()<=0) {
            cursor.close();
            return false;
        }
        return true;
    }

    public List<Contact> getAllConnectedContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        String selectQuery = "SELECT  * FROM " + Constants.TABLE_CONTACTS_CONNECTED;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setFirebaseId(cursor.getString(2));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        return contactList;
    }

    public void deleteConnectedContact(String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_CONTACTS_CONNECTED, Constants.KEY_NAME + " = ?", new String[]{userName});
        db.close();
    }

    //CONTACTS_LIST TABLE

    public void addListContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_ID, contact.getID());
        values.put(Constants.KEY_NAME, contact.getName());
        values.put(Constants.KEY_FIREBASEID, contact.getFirebaseId());
        db.insert(Constants.TABLE_CONTACTS_LIST, null, values);
        db.close();
    }

    public Contact getListContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_CONTACTS_LIST, new String[]{Constants.KEY_ID, Constants.KEY_NAME, Constants.KEY_FIREBASEID}, Constants.KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),cursor.getString(1), cursor.getString(2));
        return contact;
    }

    public Contact getListContactByUserName(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_CONTACTS_LIST, new String[]{Constants.KEY_ID, Constants.KEY_NAME, Constants.KEY_FIREBASEID}, Constants.KEY_NAME + "=?", new String[]{userName}, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),cursor.getString(1), cursor.getString(2));
        return contact;
    }

    public boolean listContactExists(String name) {
        String selectQuery = "SELECT  * FROM " + Constants.TABLE_CONTACTS_LIST + " WHERE " + Constants.KEY_NAME + "='" + name +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount()<=0) {
            cursor.close();
            return false;
        }
        return true;
    }

    public List<Contact> getAllListContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        String selectQuery = "SELECT  * FROM " + Constants.TABLE_CONTACTS_LIST;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setFirebaseId(cursor.getString(2));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        return contactList;
    }

    public int getListContactsCount() {
        String countQuery = "SELECT  * FROM " + Constants.TABLE_CONTACTS_LIST;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    public int updateListContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_NAME, contact.getName());
        values.put(Constants.KEY_FIREBASEID, contact.getFirebaseId());
        return db.update(Constants.TABLE_CONTACTS_LIST, values, Constants.KEY_ID + " = ?",new String[]{String.valueOf(contact.getID())});
    }

    public void deleteListContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_CONTACTS_LIST, Constants.KEY_ID + " = ?", new String[]{String.valueOf(contact.getID())});
        db.close();
    }

    //NODE

    public Node addNode(Node node) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_NODE_LATITUDE, node.getLatitude());
        values.put(Constants.KEY_NODE_LONGITUDE, node.getLongitude());
        values.put(Constants.KEY_NODE_DEST_LATITUDE, node.getDestLatitude());
        values.put(Constants.KEY_NODE_DEST_LONGITUDE, node.getDestLongitude());
        values.put(Constants.KEY_NODE_NEXT, node.getNodeNext());
        long primarykey = db.insert(Constants.TABLE_NODE, null, values);
        node.setId(primarykey);
        db.close();
        return node;
    }

    public int updateNodeNext(Node node) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_NODE_NEXT, node.getNodeNext());
        return db.update(Constants.TABLE_NODE, values, Constants.KEY_ID + " = ?",new String[]{String.valueOf(node.getId())});
    }

    public List<Node> getNodesAdjacentTo(Node refNode) {
        List<Node> nodeList = new ArrayList<Node>();
        String selectQuery = "SELECT  * FROM " + Constants.TABLE_NODE + " WHERE "+  Constants.KEY_NODE_LATITUDE         +"=? "+"AND "+
                                                                                    Constants.KEY_NODE_LONGITUDE        +"=? "+"AND "+
                                                                                    Constants.KEY_NODE_DEST_LATITUDE    +"=? "+"AND "+
                                                                                    Constants.KEY_NODE_DEST_LONGITUDE   +"=? ";
        String []selectArgs = new String[]{String.valueOf(refNode.getLatitude()),String.valueOf(refNode.getLongitude()),String.valueOf(refNode.getDestLatitude()),String.valueOf(refNode.getDestLongitude())};
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, selectArgs);
        if (cursor.moveToFirst()) {
            do {
                Node node = new Node();
                node.setId(Long.parseLong(cursor.getString(0)));
                node.setLatitude(Double.parseDouble(cursor.getString(1)));
                node.setLongitude(Double.parseDouble(cursor.getString(2)));
                node.setDestLatitude(Double.parseDouble(cursor.getString(3)));
                node.setDestLongitude(Double.parseDouble(cursor.getString(4)));
                node.setNodeNext(Long.parseLong(cursor.getString(5)));
                nodeList.add(node);
            } while (cursor.moveToNext());
        }
        return nodeList;
    }

}