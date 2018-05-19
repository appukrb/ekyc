package com.tcs.mmpl.customer.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyConnectionHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "MyConnectionHelper";

    public static final String DATABASE_NAME = "dbMyConnectionHelper";// database
    // name
    public static final int DATABASE_VERSION = 8;

    // table name: tbl_connection

    public static final String tbl_alerts_TABLE = "tbl_alerts";
    public static final String tbl_multibanner_TABLE = "tbl_multibanner";


    // .......................columns
    // name.......................................


    public static final String tbl_multibanner_bannerName = "bannerName";
    public static final String tbl_multibanner_bannerDownloadLink = "bannerDownloadLink";
    public static final String tbl_multibanner_bannerURL = "bannerURL";
    public static final String tbl_multibanner_flag = "flag";


    public static final String tbl_alerts_ID = "ID";
    public static final String tbl_alerts_ALERT_HEADING = "ALERTHEADING";
    public static final String tbl_alerts_ALERT = "ALERT";
    public static final String tbl_alerts_ALERT_TIME = "ALERTTIME";
    public static final String tbl_alerts_ALERT_ACTION = "ALERTACTION";
    public static final String tbl_alerts_ALERT_LINK = "ALERTLINK";
    public static final String tbl_alerts_ALERT_STATUS = "STATUS";


    // .............................. Table Create
    // Statements................................

    private static final String CREATE_TABLE_tbl_multibanner = "CREATE TABLE IF NOT EXISTS  "
            + tbl_multibanner_TABLE
            + "("
            + tbl_multibanner_bannerName
            + " TEXT,"
            + tbl_multibanner_bannerDownloadLink
            + " TEXT,"
            + tbl_multibanner_bannerURL
            + " TEXT,"
            + tbl_multibanner_flag
            + " TEXT "
            + ")";


    private static final String CREATE_TABLE_tbl_alerts= "CREATE TABLE IF NOT EXISTS  "
            + tbl_alerts_TABLE
            + "("
            + tbl_alerts_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + tbl_alerts_ALERT
            + " TEXT NOT NULL,"
            + tbl_alerts_ALERT_HEADING
            + " TEXT NOT NULL,"
            + tbl_alerts_ALERT_TIME
            + " TEXT NOT NULL,"
            + tbl_alerts_ALERT_ACTION
            + " TEXT NOT NULL,"
            + tbl_alerts_ALERT_LINK
            + " TEXT NOT NULL,"
            + tbl_alerts_ALERT_STATUS
            + " TEXT NOT NULL "
            + ")";

    public MyConnectionHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

//        Log.i(LOG, "On create method");

        db.execSQL(CREATE_TABLE_tbl_multibanner);
        db.execSQL(CREATE_TABLE_tbl_alerts);

    }
    public void fun_insertAll_tbl_alerts(String message,String alertTime,String heading,String action,String link) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(tbl_alerts_ALERT, message);
        values.put(tbl_alerts_ALERT_TIME, alertTime);
        values.put(tbl_alerts_ALERT_HEADING, heading);
        values.put(tbl_alerts_ALERT_ACTION, action);
        values.put(tbl_alerts_ALERT_LINK, link);
        values.put(tbl_alerts_ALERT_STATUS, "U");

        // insert rows
        db.insert(tbl_alerts_TABLE, null, values);

        db.close();
//        Log.i(LOG, "Inserted in table tbl_alerts ");
    }
    public void fun_insertAll_tbl_multibanner(String bannerName,String bannerDownloadLink,String bannerURL,String flag) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(tbl_multibanner_bannerName, bannerName);
        values.put(tbl_multibanner_bannerDownloadLink, bannerDownloadLink);
        values.put(tbl_multibanner_bannerURL, bannerURL);
        values.put(tbl_multibanner_flag, flag);
        // insert rows
        db.insert(tbl_multibanner_TABLE, null, values);

        db.close();
//        Log.i(LOG, "Inserted in table tbl_alerts ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + tbl_alerts_TABLE);
        onCreate(db);
    }


    public Cursor fun_selectDistinct_tbl_alerts() {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "  + tbl_alerts_TABLE +" ORDER BY "+ tbl_alerts_ID + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }
    public Cursor fun_selectDistinct_tbl_multibanner() {

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM "  + tbl_multibanner_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }
    public void fun_delete_multibanner() {
        SQLiteDatabase db = this.getReadableDatabase();
        int counter = 0;
        String deleteQuery = "DELETE FROM " + tbl_multibanner_TABLE ;
        db.execSQL(deleteQuery);
        db.close();
        //// System.out.println("Deleted alert");
    }

    public void fun_delete_Alerts(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        int counter = 0;
        String deleteQuery = "DELETE FROM " + tbl_alerts_TABLE +
                " WHERE "
                + tbl_alerts_ID + " IN(" + id + ")";

        Log.d("deleteQuery",deleteQuery);
        db.execSQL(deleteQuery);
        db.close();
        //// System.out.println("Deleted alert");
    }

    public int getUnreadCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT COUNT(*) FROM "  + tbl_alerts_TABLE + " WHERE " + tbl_alerts_ALERT_STATUS + "='U'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst())
        {
            return Integer.parseInt(cursor.getString(0));
        }
        else
        {
            return 0;
        }
    }

    public void updateAlert(int alertId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //// System.out.println("MDN"+mdn+" and NAme"+name);

        ContentValues values = new ContentValues();

        values.put(tbl_alerts_ALERT_STATUS, "R");

        db.update(tbl_alerts_TABLE, values, tbl_alerts_ID
                + " = " + alertId + "", null);

        db.close();

    }
    public void fun_delete_All_Alerts() {
        SQLiteDatabase db = this.getReadableDatabase();
        int counter = 0;
        String deleteQuery = "DELETE FROM " + tbl_alerts_TABLE ;
        db.execSQL(deleteQuery);
        db.close();
        //// System.out.println("Deleted alert");
    }


}