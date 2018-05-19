package com.tcs.mmpl.customer.Goibibo.Flight;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tcs.mmpl.customer.Goibibo.Flight.Pojo.FlightDetails;
import com.tcs.mmpl.customer.Goibibo.Flight.Pojo.FlightTraveller;
import com.tcs.mmpl.customer.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hp on 2016-07-27.
 */
public class GoibiboFlightDatabaseHelper extends SQLiteOpenHelper {


    private static final int MODE_PRIVATE = 0;
    private SharedPreferences GoibiboPref;
    private SharedPreferences.Editor GoibiboEditor;
    private static final String LOG = "Goibibo";
    public static final String DATABASE_NAME = "goibibo_flight";// database name
    public static final int DATABASE_VERSION = 6;// database version
    private Context context;

    public GoibiboFlightDatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;
        GoibiboPref = context.getSharedPreferences(context.getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();
    }


    //Tables
    public static final String tbl_flight_info = "tbl_flight_info";
    public static final String tbl_flight_traveller = "tbl_flight_traveller";


    //Columns
    public static final String col_FLIGHT_BOOKINGDATA = "booking_data";
    public static final String col_FLIGHT_ORIGIN = "origin";
    public static final String col_FLIGHT_DEPT_DATE = "dept_date";
    public static final String col_FLIGHT_DEPT_TIME = "dept_time";
    public static final String col_FLIGHT_DESTINATION = "destination";
    public static final String col_FLIGHT_ARR_DATE = "arr_date";
    public static final String col_FLIGHT_ARR_TIME = "arr_time";
    public static final String col_FLIGHT_FLIGHTCODE = "flight_code";
    public static final String col_FLIGHT_FAREBASIS = "farebasis";
    public static final String col_FLIGHT_SEATINGCLASS = "seatingclass";
    public static final String col_FLIGHT_CINFO = "cinfo";
    public static final String col_FLIGHT_DURATION = "duration";
    public static final String col_FLIGHT_NO = "flightno";
    public static final String col_FLIGHT_STOPS = "stops";
    public static final String col_FLIGHT_WARNINGS = "warnings";
    public static final String col_FLIGHT_SEARCHKEY = "searchKey";
    public static final String col_FLIGHT_TOTAL_FARE= "total_fare";
    public static final String col_FLIGHT_AIRLINE = "airline";
    public static final String col_FLIGHT_CARRIERID = "carrier_id";
    public static final String col_FLIGHT_WAY = "way";



    public static final String col_FLIGHT_TRAVELLER_ID = "traveller_id";
    public static final String col_FLIGHT_TRAVELLER_FIRST_NAME = "traveller_first_name";
    public static final String col_FLIGHT_TRAVELLER_LAST_NAME = "traveller_last_name";
    public static final String col_FLIGHT_TRAVELLER_DATE_OF_BIRTH = "DateOfBirth";
    public static final String col_FLIGHT_TRAVELLER_TYPE = "Type";
    public static final String col_FLIGHT_TRAVELLER_TITLE = "traveller_title";
    public static final String col_FLIGHT_TRAVELLER_ETICKETNUMBER = "eticketnumber";



    //create statament
    private static final String CREATE_TABLE_tbl_flight_info = "CREATE TABLE IF NOT EXISTS  "
            + tbl_flight_info
            + "("
            + col_FLIGHT_BOOKINGDATA
            + " TEXT NOT NULL,"
            + col_FLIGHT_ORIGIN
            + " TEXT NOT NULL,"
            + col_FLIGHT_DEPT_DATE
            + " TEXT NOT NULL,"
            + col_FLIGHT_DEPT_TIME
            + " TEXT NOT NULL,"
            + col_FLIGHT_DESTINATION
            + " TEXT NOT NULL,"
            + col_FLIGHT_ARR_DATE
            + " TEXT NOT NULL,"
            + col_FLIGHT_ARR_TIME
            + " TEXT NOT NULL,"
            + col_FLIGHT_FLIGHTCODE
            + " TEXT DEFAULT NULL,"
            + col_FLIGHT_FAREBASIS
            + " TEXT NOT NULL,"
            + col_FLIGHT_SEATINGCLASS
            + " TEXT NOT NULL,"
            + col_FLIGHT_CINFO
            + " TEXT NOT NULL,"
            + col_FLIGHT_DURATION
            + " TEXT NOT NULL,"
            + col_FLIGHT_NO
            + " TEXT NOT NULL,"
            + col_FLIGHT_STOPS
            + " TEXT NOT NULL,"
            + col_FLIGHT_WARNINGS
            + " TEXT NOT NULL,"
            + col_FLIGHT_SEARCHKEY
            + " TEXT NOT NULL,"
            + col_FLIGHT_AIRLINE
            + " TEXT NOT NULL,"
            + col_FLIGHT_TOTAL_FARE
            + " TEXT NOT NULL,"
            +col_FLIGHT_CARRIERID
            + " TEXT NOT NULL,"
            + col_FLIGHT_WAY
            + " TEXT NOT NULL "
            + ")";


//    private static final String CREATE_TABLE_tbl_bus_info = "CREATE TABLE IF NOT EXISTS  "
//            + tbl_bus_info
//            + "("
//            + col_SKEY
//            + " TEXT DEFAULT NULL,"
//            + col_WAY
//            + " TEXT DEFAULT NULL,"
//            + col_ORIGIN
//            + " TEXT DEFAULT NULL,"
//            + col_DESTINATION
//            + " TEXT DEFAULT NULL,"
//            + col_BUSOPERATOR
//            + " TEXT DEFAULT NULL,"
//            + col_BOARDINGTIME
//            + " TEXT DEFAULT NULL,"
//            + col_BOARDINGPOINT
//            + " TEXT DEFAULT NULL,"
//            + col_SEATS
//            + " TEXT DEFAULT NULL,"
//            + col_TOTALPRICE
//            + " TEXT DEFAULT NULL,"
//            + col_DEPDATE
//            + " TEXT DEFAULT NULL,"
//            + col_ARRDATE
//            + " TEXT DEFAULT NULL,"
//            + col_BOARDINGID
//            + " TEXT DEFAULT NULL,"
//            + col_DURATION
//            + " TEXT NOT NULL,"
//            + col_STATUS
//            + " TEXT DEFAULT NULL "
//            + ")";
//
//    private static final String CREATE_TABLE_tbl_boarding_point = "CREATE TABLE IF NOT EXISTS  "
//            + tbl_boarding_point
//            + "("
//            + col_BPID
//            + " TEXT NOT NULL,"
//            + col_BPLOCATION
//            + " TEXT NOT NULL,"
//            + col_BPTIME
//            + " TEXT NOT NULL,"
//            + col_BPNAME
//            + " TEXT NOT NULL "
//            + ")";

    private static final String CREATE_TABLE_tbl_bus_traveller = "CREATE TABLE IF NOT EXISTS  "
            + tbl_flight_traveller
            + "("
            + col_FLIGHT_TRAVELLER_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + col_FLIGHT_TRAVELLER_FIRST_NAME
            + " TEXT NOT NULL,"
            + col_FLIGHT_TRAVELLER_LAST_NAME
            + " TEXT NOT NULL,"
            + col_FLIGHT_TRAVELLER_DATE_OF_BIRTH
            + " TEXT NOT NULL,"
            + col_FLIGHT_TRAVELLER_TITLE
            + " TEXT NOT NULL,"
            + col_FLIGHT_TRAVELLER_ETICKETNUMBER
            + " TEXT NOT NULL,"
            + col_FLIGHT_TRAVELLER_TYPE
            + " TEXT NOT NULL "
            + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG, "On create method");

        db.execSQL(CREATE_TABLE_tbl_bus_traveller);
        db.execSQL(CREATE_TABLE_tbl_flight_info);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(LOG, "On upgrade method");

        db.execSQL("DROP TABLE IF EXISTS " + tbl_flight_traveller);
        db.execSQL("DROP TABLE IF EXISTS " + tbl_flight_info);


        onCreate(db);

    }

    //Insertion Starts Here


    public void insertFlightInfo(FlightDetails flightDetails,String way) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + tbl_flight_info + " WHERE " + col_FLIGHT_WAY + "='" + way + "'";

        Log.i(LOG,selectQuery);
        Log.i(LOG, flightDetails.getBookingdata());
                Cursor cursor = db.rawQuery(selectQuery, null);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd't'HHmm");
        SimpleDateFormat change_df = new SimpleDateFormat("dd-MM-yyyy");
        String strdepdate = "", strarrdate = "";
        try {
            Date d1 = df.parse(flightDetails.getDepdate());
            strdepdate = change_df.format(d1);

            Log.i(LOG,strdepdate);

            d1 = df.parse(flightDetails.getArrdate());
            strarrdate = change_df.format(d1);
            Log.i(LOG,strarrdate);


        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(col_FLIGHT_BOOKINGDATA, flightDetails.getBookingdata());
            values.put(col_FLIGHT_ORIGIN, flightDetails.getOrigin());
            values.put(col_FLIGHT_DEPT_DATE, strdepdate);
            values.put(col_FLIGHT_DEPT_TIME, flightDetails.getDeptime());
            values.put(col_FLIGHT_DESTINATION, flightDetails.getDestination());
            values.put(col_FLIGHT_ARR_DATE, strarrdate);
            values.put(col_FLIGHT_ARR_TIME, flightDetails.getArrtime());
            values.put(col_FLIGHT_FLIGHTCODE, flightDetails.getFlightcode());
            values.put(col_FLIGHT_FAREBASIS, flightDetails.getFarebasis());
            values.put(col_FLIGHT_SEATINGCLASS, flightDetails.getSeatingclass());
            values.put(col_FLIGHT_CINFO, flightDetails.getCINFO());
            values.put(col_FLIGHT_DURATION, flightDetails.getDuration());
            values.put(col_FLIGHT_NO, flightDetails.getFlightno());
            values.put(col_FLIGHT_STOPS, flightDetails.getStops());
            values.put(col_FLIGHT_WARNINGS, flightDetails.getWarnings());
            values.put(col_FLIGHT_SEARCHKEY, flightDetails.getSearchKey());
            values.put(col_FLIGHT_AIRLINE, flightDetails.getAirline());
            values.put(col_FLIGHT_CARRIERID, flightDetails.getCarrierid());
            values.put(col_FLIGHT_TOTAL_FARE, flightDetails.getTotalfare());
            values.put(col_FLIGHT_WAY, way);


            db.update(tbl_flight_info, values, col_FLIGHT_WAY + " = '" + way + "'", null);
            Log.i(LOG, "Flight Info Updated");
        } else {
            ContentValues values = new ContentValues();
            values.put(col_FLIGHT_BOOKINGDATA, flightDetails.getBookingdata());
            values.put(col_FLIGHT_ORIGIN, flightDetails.getOrigin());
            values.put(col_FLIGHT_DEPT_DATE, strdepdate);
            values.put(col_FLIGHT_DEPT_TIME, flightDetails.getDeptime());
            values.put(col_FLIGHT_DESTINATION, flightDetails.getDestination());
            values.put(col_FLIGHT_ARR_DATE, strarrdate);
            values.put(col_FLIGHT_ARR_TIME, flightDetails.getArrtime());
            values.put(col_FLIGHT_FLIGHTCODE, flightDetails.getFlightcode());
            values.put(col_FLIGHT_FAREBASIS, flightDetails.getFarebasis());
            values.put(col_FLIGHT_SEATINGCLASS, flightDetails.getSeatingclass());
            values.put(col_FLIGHT_CINFO, flightDetails.getCINFO());
            values.put(col_FLIGHT_DURATION, flightDetails.getDuration());
            values.put(col_FLIGHT_NO, flightDetails.getFlightno());
            values.put(col_FLIGHT_STOPS, flightDetails.getStops());
            values.put(col_FLIGHT_WARNINGS, flightDetails.getWarnings());
            values.put(col_FLIGHT_SEARCHKEY, flightDetails.getSearchKey());
            values.put(col_FLIGHT_AIRLINE, flightDetails.getAirline());
            values.put(col_FLIGHT_CARRIERID, flightDetails.getCarrierid());
            values.put(col_FLIGHT_TOTAL_FARE, flightDetails.getTotalfare());
            values.put(col_FLIGHT_WAY, way);

            db.insert(tbl_flight_info, null, values);

            Log.i(LOG, "Flight Info Inserted");
        }

        cursor.close();
        db.close();

    }



    public String insertFlightTraveller(FlightTraveller flightTraveller) {
        SQLiteDatabase db = this.getWritableDatabase();

        String title = "";

        if(flightTraveller.getTitle().trim().equalsIgnoreCase("Mr"))
        {
            title = "1";
        }
        else if(flightTraveller.getTitle().trim().equalsIgnoreCase("Mrs"))
        {
            title = "2";

        }
        else if(flightTraveller.getTitle().trim().equalsIgnoreCase("Miss"))
        {
            title = "3";

        }
        else if(flightTraveller.getTitle().trim().equalsIgnoreCase("Master"))
        {
            title = "4";

        }

        ContentValues values = new ContentValues();

        values.put(col_FLIGHT_TRAVELLER_FIRST_NAME, flightTraveller.getFirstName());
        values.put(col_FLIGHT_TRAVELLER_LAST_NAME, flightTraveller.getLastName());
        values.put(col_FLIGHT_TRAVELLER_TITLE, title);
        values.put(col_FLIGHT_TRAVELLER_DATE_OF_BIRTH, flightTraveller.getDateOfBirth());
        values.put(col_FLIGHT_TRAVELLER_ETICKETNUMBER, flightTraveller.getEticketnumber());
        values.put(col_FLIGHT_TRAVELLER_TYPE, flightTraveller.getType());

        long id = db.insert(tbl_flight_traveller, null, values);

        db.close();

        Log.i(LOG, "Inserted in table tbl_flight_traveller " + id);
        return String.valueOf(id);
    }

    //Insertion Ends Here

    //Select Starts Here


//    public int getTravellerSeatCount() {
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        String selectQuery = "SELECT count(*) FROM " + tbl_seat_map + " WHERE " + col_TRAVEL_TYPE + "='" + context.getResources().getString(R.string.onw) + "'";
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        if (cursor.moveToFirst()) {
//            String count = cursor.getString(0);
//            cursor.close();
//            return Integer.parseInt(count);
//        } else {
//            cursor.close();
//            return 0;
//        }
//
//    }
//
//    public int getTravellerCount() {
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        String selectQuery = "SELECT count(*) FROM " + tbl_bus_traveller;
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        if (cursor.moveToFirst()) {
//            String count = cursor.getString(0);
//            cursor.close();
//            return Integer.parseInt(count);
//        } else {
//            cursor.close();
//            return 0;
//        }
//
//    }
//
    public ArrayList<FlightTraveller> getFlightTravellerDetails() {

        ArrayList<FlightTraveller> flightTravellerArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tbl_flight_traveller;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                FlightTraveller flightTraveller = new FlightTraveller();
                flightTraveller.setFirstName(cursor.getString(cursor.getColumnIndex(col_FLIGHT_TRAVELLER_FIRST_NAME)));
                flightTraveller.setLastName(cursor.getString(cursor.getColumnIndex(col_FLIGHT_TRAVELLER_LAST_NAME)));
                flightTraveller.setTitle(cursor.getString(cursor.getColumnIndex(col_FLIGHT_TRAVELLER_TITLE)));
                flightTraveller.setDateOfBirth(cursor.getString(cursor.getColumnIndex(col_FLIGHT_TRAVELLER_DATE_OF_BIRTH)));
                flightTraveller.setEticketnumber(cursor.getString(cursor.getColumnIndex(col_FLIGHT_TRAVELLER_ETICKETNUMBER)));
                flightTraveller.setType(cursor.getString(cursor.getColumnIndex(col_FLIGHT_TRAVELLER_TYPE)));

                flightTravellerArrayList.add(flightTraveller);

            }while (cursor.moveToNext());
        }

        return flightTravellerArrayList;
    }

    public int getFlightTravellerCount() {

        ArrayList<FlightTraveller> flightTravellerArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT count(*) FROM " + tbl_flight_traveller;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
             return Integer.parseInt(cursor.getString(0));
        }
        else
        {
            return  0;
        }


    }

    public int getFlightOnwardCount() {

        String way = context.getResources().getString(R.string.onw);

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT count(*) FROM " + tbl_flight_info + " WHERE "+col_FLIGHT_WAY+"='"+way+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            return  Integer.parseInt(cursor.getString(0));
        }
        else
            return 0;
    }

    public FlightDetails getFlightOnwardInfo() {

        String way = context.getResources().getString(R.string.onw);

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tbl_flight_info + " WHERE "+col_FLIGHT_WAY+"='"+way+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        FlightDetails flightDetails = new FlightDetails();
        if (cursor.moveToFirst()) {


            flightDetails.setBookingdata(cursor.getString(cursor.getColumnIndex(col_FLIGHT_BOOKINGDATA)));
            flightDetails.setOrigin(cursor.getString(cursor.getColumnIndex(col_FLIGHT_ORIGIN)));
            flightDetails.setDepdate(cursor.getString(cursor.getColumnIndex(col_FLIGHT_DEPT_DATE)));
            flightDetails.setDeptime(cursor.getString(cursor.getColumnIndex(col_FLIGHT_DEPT_TIME)));
            flightDetails.setDestination(cursor.getString(cursor.getColumnIndex(col_FLIGHT_DESTINATION)));
            flightDetails.setArrdate(cursor.getString(cursor.getColumnIndex(col_FLIGHT_ARR_DATE)));
            flightDetails.setArrtime(cursor.getString(cursor.getColumnIndex(col_FLIGHT_ARR_TIME)));
            flightDetails.setFlightcode(cursor.getString(cursor.getColumnIndex(col_FLIGHT_FLIGHTCODE)));
            flightDetails.setFarebasis(cursor.getString(cursor.getColumnIndex(col_FLIGHT_FAREBASIS)));
            flightDetails.setSeatingclass(cursor.getString(cursor.getColumnIndex(col_FLIGHT_SEATINGCLASS)));
            flightDetails.setCINFO(cursor.getString(cursor.getColumnIndex(col_FLIGHT_CINFO)));
            flightDetails.setDuration(cursor.getString(cursor.getColumnIndex(col_FLIGHT_DURATION)));
            flightDetails.setFlightno(cursor.getString(cursor.getColumnIndex(col_FLIGHT_NO)));
            flightDetails.setStops(cursor.getString(cursor.getColumnIndex(col_FLIGHT_STOPS)));
            flightDetails.setWarnings(cursor.getString(cursor.getColumnIndex(col_FLIGHT_WARNINGS)));
            flightDetails.setSearchKey(cursor.getString(cursor.getColumnIndex(col_FLIGHT_SEARCHKEY)));
            flightDetails.setTotalfare(cursor.getString(cursor.getColumnIndex(col_FLIGHT_TOTAL_FARE)));
            flightDetails.setAirline(cursor.getString(cursor.getColumnIndex(col_FLIGHT_AIRLINE)));
            flightDetails.setCarrierid(cursor.getString(cursor.getColumnIndex(col_FLIGHT_CARRIERID)));

        }
        return  flightDetails;

    }

    public int getFlightReturnCount() {
        String way = context.getResources().getString(R.string.ret);

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT count(*) FROM " + tbl_flight_info + " WHERE "+col_FLIGHT_WAY+"='"+way+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            return  Integer.parseInt(cursor.getString(0));
        }
        else
        return 0;

    }

    public FlightDetails getFlightReturnInfo() {

        String way = context.getResources().getString(R.string.ret);

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tbl_flight_info + " WHERE "+col_FLIGHT_WAY+"='"+way+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        FlightDetails flightDetails = new FlightDetails();
        if (cursor.moveToFirst()) {

            flightDetails.setBookingdata(cursor.getString(cursor.getColumnIndex(col_FLIGHT_BOOKINGDATA)));
            flightDetails.setOrigin(cursor.getString(cursor.getColumnIndex(col_FLIGHT_ORIGIN)));
            flightDetails.setDepdate(cursor.getString(cursor.getColumnIndex(col_FLIGHT_DEPT_DATE)));
            flightDetails.setDeptime(cursor.getString(cursor.getColumnIndex(col_FLIGHT_DEPT_TIME)));
            flightDetails.setDestination(cursor.getString(cursor.getColumnIndex(col_FLIGHT_DESTINATION)));
            flightDetails.setArrdate(cursor.getString(cursor.getColumnIndex(col_FLIGHT_ARR_DATE)));
            flightDetails.setArrtime(cursor.getString(cursor.getColumnIndex(col_FLIGHT_ARR_TIME)));
            flightDetails.setFlightcode(cursor.getString(cursor.getColumnIndex(col_FLIGHT_FLIGHTCODE)));
            flightDetails.setFarebasis(cursor.getString(cursor.getColumnIndex(col_FLIGHT_FAREBASIS)));
            flightDetails.setSeatingclass(cursor.getString(cursor.getColumnIndex(col_FLIGHT_SEATINGCLASS)));
            flightDetails.setCINFO(cursor.getString(cursor.getColumnIndex(col_FLIGHT_CINFO)));
            flightDetails.setDuration(cursor.getString(cursor.getColumnIndex(col_FLIGHT_DURATION)));
            flightDetails.setFlightno(cursor.getString(cursor.getColumnIndex(col_FLIGHT_NO)));
            flightDetails.setStops(cursor.getString(cursor.getColumnIndex(col_FLIGHT_STOPS)));
            flightDetails.setWarnings(cursor.getString(cursor.getColumnIndex(col_FLIGHT_WARNINGS)));
            flightDetails.setSearchKey(cursor.getString(cursor.getColumnIndex(col_FLIGHT_SEARCHKEY)));
            flightDetails.setTotalfare(cursor.getString(cursor.getColumnIndex(col_FLIGHT_TOTAL_FARE)));
            flightDetails.setAirline(cursor.getString(cursor.getColumnIndex(col_FLIGHT_AIRLINE)));
            flightDetails.setCarrierid(cursor.getString(cursor.getColumnIndex(col_FLIGHT_CARRIERID)));




        }
        return  flightDetails;

    }

    //Select Ends Here


    //Update Starts Here



    public void updateFlightTravellerInfo(FlightTraveller flightTraveller, String tag) {

        int id = Integer.parseInt(tag);

        String title = "";

        if(flightTraveller.getTitle().trim().equalsIgnoreCase("Mr"))
        {
            title = "1";
        }
        else if(flightTraveller.getTitle().trim().equalsIgnoreCase("Mrs"))
        {
            title = "2";

        }
        else if(flightTraveller.getTitle().trim().equalsIgnoreCase("Miss"))
        {
            title = "3";

        }
        else if(flightTraveller.getTitle().trim().equalsIgnoreCase("Master"))
        {
            title = "4";

        }

        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(col_FLIGHT_TRAVELLER_FIRST_NAME, flightTraveller.getFirstName());
        values.put(col_FLIGHT_TRAVELLER_LAST_NAME, flightTraveller.getLastName());
        values.put(col_FLIGHT_TRAVELLER_TITLE, title);
        values.put(col_FLIGHT_TRAVELLER_DATE_OF_BIRTH, flightTraveller.getDateOfBirth());
        values.put(col_FLIGHT_TRAVELLER_ETICKETNUMBER, flightTraveller.getEticketnumber());
        values.put(col_FLIGHT_TRAVELLER_TYPE, flightTraveller.getType());

        db.update(tbl_flight_traveller, values, col_FLIGHT_TRAVELLER_ID + " = " + id, null);

        Log.i(LOG, "Updated the bus traveller info" + id);


    }



    //Update Ends Here


    //Delete Starts Here

    public void deleteFlightInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tbl_flight_info);
        db.close();
    }

    public void deleteFlightTraveller() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tbl_flight_traveller);
        db.close();
    }


    //Delete Ends Here

}
