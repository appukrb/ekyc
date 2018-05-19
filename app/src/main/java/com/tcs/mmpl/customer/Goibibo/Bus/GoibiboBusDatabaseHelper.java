package com.tcs.mmpl.customer.Goibibo.Bus;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusBasicInfo;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusBoardingPoint;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusSeatMapDetails;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusTraveller;
import com.tcs.mmpl.customer.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hp on 2016-07-27.
 */
public class GoibiboBusDatabaseHelper extends SQLiteOpenHelper {


    private static final int MODE_PRIVATE = 0;
    private SharedPreferences GoibiboPref;
    private SharedPreferences.Editor GoibiboEditor;
    private static final String LOG = "Goibibo";
    public static final String DATABASE_NAME = "goibibo";// database name
    public static final int DATABASE_VERSION = 18;// database version
    private Context context;

    public GoibiboBusDatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;
        GoibiboPref = context.getSharedPreferences(context.getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();
    }


    //Tables
    public static final String tbl_seat_map = "tbl_seat_map";
    public static final String tbl_bus_info = "tbl_bus_info";
    public static final String tbl_boarding_point = "tbl_boarding_point";
    public static final String tbl_bus_traveller = "tbl_traveller";


    //Columns
    public static final String col_ID_SEATMAP = "id_seatmap";
    public static final String col_SERVICETAXPERCENTAGE = "ServiceTaxPercentage";
    public static final String col_ACTUALSEATFARE = "ActualSeatFare";
    public static final String col_SEATTYPE = "SeatType";
    public static final String col_ROWNO = "RowNo";
    public static final String col_DECK = "Deck";
    public static final String col_OPERATORSERVICECHARGEABSOLUTE = "operatorServiceChargeAbsolute";
    public static final String col_COLUMNNO = "ColumnNo";
    public static final String col_SERVICECHARGE = "serviceCharge";
    public static final String col_SEATFARE = "SeatFare";
    public static final String col_HEIGHT = "Height";
    public static final String col_WIDTH = "Width";
    public static final String col_LSEAT = "LSeat";
    public static final String col_BASEFARE = "BaseFare";
    public static final String col_SERVICETAX = "ServiceTax";
    public static final String col_SEATNAME = "SeatName";
    public static final String col_SEAT_STATUS = "seat_status";
    public static final String col_SEATSTATUSNUMBER = "SeatStatus";
    public static final String col_ROW_TYPE = "row_type";
    public static final String col_TRAVEL_TYPE = "travel_type";
    public static final String col_STATUSSEAT = "status";


    public static final String col_SKEY = "skey";
    public static final String col_WAY = "way";
    public static final String col_ORIGIN = "origin";
    public static final String col_DESTINATION = "destination";
    public static final String col_BUSOPERATOR = "busoperator";
    public static final String col_BOARDINGTIME = "boardingtime";
    public static final String col_BOARDINGPOINT = "boardingpoint";
    public static final String col_SEATS = "seats";
    public static final String col_TOTALPRICE = "totalprice";
    public static final String col_DEPDATE = "depdate";
    public static final String col_ARRDATE = "arrdate";
    public static final String col_BOARDINGID = "boardingid";
    public static final String col_DURATION = "duration";
    public static final String col_STATUS = "status";


    public static final String col_BPID = "bpid";
    public static final String col_BPLOCATION = "bplocation";
    public static final String col_BPTIME = "bptime";
    public static final String col_BPNAME = "bpname";


    public static final String col_BUS_TRAVELLER_ID = "traveller_id";
    public static final String col_BUS_TRAVELLER_FIRST_NAME = "traveller_first_name";
    public static final String col_BUS_TRAVELLER_MIDDLE_NAME = "traveller_middle_name";
    public static final String col_BUS_TRAVELLER_LAST_NAME = "traveller_last_name";
    public static final String col_BUS_TRAVELLER_AGE = "traveller_age";
    public static final String col_BUS_TRAVELLER_TITLE = "traveller_title";


    //create statament
    private static final String CREATE_TABLE_tbl_seat_map = "CREATE TABLE IF NOT EXISTS  "
            + tbl_seat_map
            + "("
            + col_ID_SEATMAP
            + " INTEGER,"
            + col_SERVICETAXPERCENTAGE
            + " TEXT NOT NULL,"
            + col_ACTUALSEATFARE
            + " TEXT NOT NULL,"
            + col_SEATTYPE
            + " TEXT NOT NULL,"
            + col_ROWNO
            + " TEXT NOT NULL,"
            + col_DECK
            + " TEXT NOT NULL,"
            + col_OPERATORSERVICECHARGEABSOLUTE
            + " TEXT NOT NULL,"
            + col_COLUMNNO
            + " TEXT DEFAULT NULL,"
            + col_SERVICECHARGE
            + " TEXT NOT NULL,"
            + col_SEATFARE
            + " TEXT NOT NULL,"
            + col_HEIGHT
            + " TEXT NOT NULL,"
            + col_WIDTH
            + " TEXT NOT NULL,"
            + col_LSEAT
            + " TEXT NOT NULL,"
            + col_BASEFARE
            + " TEXT NOT NULL,"
            + col_SERVICETAX
            + " TEXT NOT NULL,"
            + col_SEATNAME
            + " TEXT NOT NULL,"
            + col_SEAT_STATUS
            + " TEXT NOT NULL,"
            + col_SEATSTATUSNUMBER
            + " TEXT NOT NULL,"
            + col_ROW_TYPE
            + " TEXT NOT NULL,"
            + col_TRAVEL_TYPE
            + " TEXT NOT NULL,"
            + col_STATUSSEAT
            + " TEXT NOT NULL "
            + ")";


    private static final String CREATE_TABLE_tbl_bus_info = "CREATE TABLE IF NOT EXISTS  "
            + tbl_bus_info
            + "("
            + col_SKEY
            + " TEXT DEFAULT NULL,"
            + col_WAY
            + " TEXT DEFAULT NULL,"
            + col_ORIGIN
            + " TEXT DEFAULT NULL,"
            + col_DESTINATION
            + " TEXT DEFAULT NULL,"
            + col_BUSOPERATOR
            + " TEXT DEFAULT NULL,"
            + col_BOARDINGTIME
            + " TEXT DEFAULT NULL,"
            + col_BOARDINGPOINT
            + " TEXT DEFAULT NULL,"
            + col_SEATS
            + " TEXT DEFAULT NULL,"
            + col_TOTALPRICE
            + " TEXT DEFAULT NULL,"
            + col_DEPDATE
            + " TEXT DEFAULT NULL,"
            + col_ARRDATE
            + " TEXT DEFAULT NULL,"
            + col_BOARDINGID
            + " TEXT DEFAULT NULL,"
            + col_DURATION
            + " TEXT NOT NULL,"
            + col_STATUS
            + " TEXT DEFAULT NULL "
            + ")";

    private static final String CREATE_TABLE_tbl_boarding_point = "CREATE TABLE IF NOT EXISTS  "
            + tbl_boarding_point
            + "("
            + col_BPID
            + " TEXT NOT NULL,"
            + col_BPLOCATION
            + " TEXT NOT NULL,"
            + col_BPTIME
            + " TEXT NOT NULL,"
            + col_BPNAME
            + " TEXT NOT NULL "
            + ")";

    private static final String CREATE_TABLE_tbl_bus_traveller = "CREATE TABLE IF NOT EXISTS  "
            + tbl_bus_traveller
            + "("
            + col_BUS_TRAVELLER_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + col_BUS_TRAVELLER_FIRST_NAME
            + " TEXT NOT NULL,"
            + col_BUS_TRAVELLER_MIDDLE_NAME
            + " TEXT NOT NULL,"
            + col_BUS_TRAVELLER_LAST_NAME
            + " TEXT NOT NULL,"
            + col_BUS_TRAVELLER_AGE
            + " TEXT NOT NULL,"
            + col_BUS_TRAVELLER_TITLE
            + " TEXT NOT NULL "
            + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG, "On create method");
        db.execSQL(CREATE_TABLE_tbl_seat_map);
        db.execSQL(CREATE_TABLE_tbl_bus_info);
        db.execSQL(CREATE_TABLE_tbl_boarding_point);
        db.execSQL(CREATE_TABLE_tbl_bus_traveller);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(LOG, "On upgrade method");
        db.execSQL("DROP TABLE IF EXISTS " + tbl_seat_map);
        db.execSQL("DROP TABLE IF EXISTS " + tbl_bus_info);
        db.execSQL("DROP TABLE IF EXISTS " + tbl_boarding_point);
        db.execSQL("DROP TABLE IF EXISTS " + tbl_bus_traveller);


        onCreate(db);

    }

    //Insertion Starts Here
    public void insertSeatMap(BusSeatMapDetails busSeatMapDetails) {
        SQLiteDatabase db = this.getWritableDatabase();

        String travel_type = GoibiboPref.getString(context.getResources().getString(R.string.way), "");
        Log.i(LOG,travel_type);

        ContentValues values = new ContentValues();
        values.put(col_ID_SEATMAP, busSeatMapDetails.getId());
        values.put(col_SERVICETAXPERCENTAGE, busSeatMapDetails.getServiceTaxPercentage());
        values.put(col_ACTUALSEATFARE, busSeatMapDetails.getActualSeatFare());
        values.put(col_SEATTYPE, busSeatMapDetails.getSeatType());
        values.put(col_ROWNO, busSeatMapDetails.getRowNo());
        values.put(col_DECK, busSeatMapDetails.getDeck());
        values.put(col_OPERATORSERVICECHARGEABSOLUTE, busSeatMapDetails.getOperatorServiceChargeAbsolute());
        values.put(col_COLUMNNO, busSeatMapDetails.getColumnNo());
        values.put(col_SERVICECHARGE, busSeatMapDetails.getServiceCharge());
        values.put(col_SEATFARE, busSeatMapDetails.getSeatFare());
        values.put(col_HEIGHT, busSeatMapDetails.getHeight());
        values.put(col_WIDTH, busSeatMapDetails.getWidth());
        values.put(col_LSEAT, busSeatMapDetails.getLSeat());
        values.put(col_BASEFARE, busSeatMapDetails.getBaseFare());
        values.put(col_SERVICETAX, busSeatMapDetails.getServiceTax());
        values.put(col_SEATNAME, busSeatMapDetails.getSeatName());
        values.put(col_SEAT_STATUS, busSeatMapDetails.getSeat_status());
        values.put(col_SEATSTATUSNUMBER, busSeatMapDetails.getSeatStatus());
        values.put(col_ROW_TYPE, busSeatMapDetails.getRow_type());
        values.put(col_TRAVEL_TYPE, travel_type);
        values.put(col_STATUSSEAT, "D");

        // insert row
        db.insert(tbl_seat_map, null, values);

        db.close();
        Log.i(LOG, "Inserted in table tbl_seat_map ");
    }

    public void insertBusInfo(BusBasicInfo busBasicInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT " + col_SKEY + " FROM " + tbl_bus_info + " WHERE " + col_WAY + "='" + busBasicInfo.getWay() + "'";

        Log.i(LOG,selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        SimpleDateFormat change_df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        String strdepdate = "", strarrdate = "";
        try {
            Date d1 = df.parse(busBasicInfo.getDeparturedate());
            strdepdate = change_df.format(d1);

            d1 = df.parse(busBasicInfo.getArrivaldate());
            strarrdate = change_df.format(d1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //// System.out.println("depdate::" + strdepdate);
        //// System.out.println("arrdate::" + strarrdate);

        if (cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(col_SKEY, busBasicInfo.getSkey());
            values.put(col_WAY, busBasicInfo.getWay());
            values.put(col_ORIGIN, busBasicInfo.getOrigin());
            values.put(col_DESTINATION, busBasicInfo.getDestination());
            values.put(col_BUSOPERATOR, busBasicInfo.getBusoperator());
            values.put(col_BOARDINGPOINT, busBasicInfo.getBoardingpoint());
            values.put(col_BOARDINGTIME, busBasicInfo.getBoardingtime());
            values.put(col_SEATS, busBasicInfo.getSeats());
            values.put(col_TOTALPRICE, busBasicInfo.getTotalprice());
            values.put(col_DEPDATE, strdepdate);
            values.put(col_ARRDATE, strarrdate);
            values.put(col_DURATION, busBasicInfo.getDuration());
            values.put(col_STATUS, "D");


            db.update(tbl_bus_info, values, col_WAY + " = '" + busBasicInfo.getWay() + "'", null);
            Log.i(LOG, "Bus Info Updated");
        } else {
            ContentValues values = new ContentValues();
            values.put(col_SKEY, busBasicInfo.getSkey());
            values.put(col_WAY, busBasicInfo.getWay());
            values.put(col_ORIGIN, busBasicInfo.getOrigin());
            values.put(col_DESTINATION, busBasicInfo.getDestination());
            values.put(col_BUSOPERATOR, busBasicInfo.getBusoperator());
            values.put(col_BOARDINGPOINT, busBasicInfo.getBoardingpoint());
            values.put(col_BOARDINGTIME, busBasicInfo.getBoardingtime());
            values.put(col_SEATS, busBasicInfo.getSeats());
            values.put(col_TOTALPRICE, busBasicInfo.getTotalprice());
            values.put(col_DEPDATE, strdepdate);
            values.put(col_ARRDATE, strarrdate);
            values.put(col_DURATION, busBasicInfo.getDuration());
            values.put(col_STATUS, "D");


            db.insert(tbl_bus_info, null, values);

            Log.i(LOG, "Bus Info Inserted");
        }

        cursor.close();
        db.close();

    }

    public void insertBoardingPoint(String bplocation, String bptime, String bpname) {
        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        SimpleDateFormat change_df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        String boardingtime = "";
        try {
            Date d1 = df.parse(bptime);
            boardingtime = change_df.format(d1);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(col_BPID, bpname.split("-")[0]);
        values.put(col_BPLOCATION, bplocation);
        values.put(col_BPTIME, boardingtime);
        values.put(col_BPNAME, bpname);
        db.insert(tbl_boarding_point, null, values);
        db.close();
        Log.i(LOG, "Inserted in table tbl_boarding_point ");
    }


    public String insertBusTraveller(BusTraveller busTraveller) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(col_BUS_TRAVELLER_FIRST_NAME, busTraveller.getFirstname());
        values.put(col_BUS_TRAVELLER_MIDDLE_NAME, busTraveller.getMiddlename());
        values.put(col_BUS_TRAVELLER_LAST_NAME, busTraveller.getLastname());
        values.put(col_BUS_TRAVELLER_AGE, busTraveller.getAge());
        values.put(col_BUS_TRAVELLER_TITLE, busTraveller.getTitle());

        long id = db.insert(tbl_bus_traveller, null, values);

        db.close();

        Log.i(LOG, "Inserted in table tbl_bus_traveller " + id);
        return String.valueOf(id);
    }

    //Insertion Ends Here

    //Select Starts Here
    public boolean isSeatSelected(int id) {

        String way = GoibiboPref.getString(context.getString(R.string.way),"");
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tbl_seat_map + " WHERE " + col_ID_SEATMAP + "=" + id + " AND "+ col_TRAVEL_TYPE + "='"+way+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }


    }

    public int getOnwardSeatCount() {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT count(*) FROM " + tbl_seat_map + " WHERE " + col_TRAVEL_TYPE + "='" + context.getResources().getString(R.string.onw) + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            String count = cursor.getString(0);
            cursor.close();
            return Integer.parseInt(count);
        } else {
            cursor.close();
            return 0;
        }

    }
    public int getReturnSeatCount() {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT count(*) FROM " + tbl_seat_map + " WHERE " + col_TRAVEL_TYPE + "='" + context.getResources().getString(R.string.ret) + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            String count = cursor.getString(0);
            cursor.close();
            return Integer.parseInt(count);
        } else {
            cursor.close();
            return 0;
        }

    }

    public ArrayList<String> getBusInfoSeatAndTotal() {
        ArrayList<String> listSeatandTotal = new ArrayList<>();

        String way = GoibiboPref.getString(context.getResources().getString(R.string.way), "");

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tbl_bus_info + " WHERE " + col_WAY + "='" + way + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            //// System.out.println("Seats:::" + cursor.getString(cursor.getColumnIndex(col_SEATS)));
            //// System.out.println("Price:::" + cursor.getString(cursor.getColumnIndex(col_TOTALPRICE)));
            if(!cursor.getString(cursor.getColumnIndex(col_SEATS)).equalsIgnoreCase("NA") && !cursor.getString(cursor.getColumnIndex(col_TOTALPRICE)).equalsIgnoreCase("NA"))
            {
                listSeatandTotal.add(cursor.getString(cursor.getColumnIndex(col_SEATS)));
                listSeatandTotal.add(cursor.getString(cursor.getColumnIndex(col_TOTALPRICE)));
            }


        }

        cursor.close();


        return listSeatandTotal;



    }

    public int getSeatCount() {

        String way = GoibiboPref.getString(context.getResources().getString(R.string.way),"");
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT count(*) FROM " + tbl_seat_map + " WHERE " + col_TRAVEL_TYPE + "='" + way + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            String count = cursor.getString(0);
            cursor.close();
            return Integer.parseInt(count);
        } else {
            cursor.close();
            return 0;
        }

    }

    public int getTravellerSeatCount() {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT count(*) FROM " + tbl_seat_map + " WHERE " + col_TRAVEL_TYPE + "='" + context.getResources().getString(R.string.onw) + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            String count = cursor.getString(0);
            cursor.close();
            return Integer.parseInt(count);
        } else {
            cursor.close();
            return 0;
        }

    }

    public int getTravellerCount() {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT count(*) FROM " + tbl_bus_traveller;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            String count = cursor.getString(0);
            cursor.close();
            return Integer.parseInt(count);
        } else {
            cursor.close();
            return 0;
        }

    }

    public ArrayList<BusTraveller> getTravellerDetails() {

        ArrayList<BusTraveller> busTravellerArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tbl_bus_traveller;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BusTraveller busTraveller = new BusTraveller();
                busTraveller.setFirstname(cursor.getString(cursor.getColumnIndex(col_BUS_TRAVELLER_FIRST_NAME)));
                busTraveller.setMiddlename(cursor.getString(cursor.getColumnIndex(col_BUS_TRAVELLER_MIDDLE_NAME)));
                busTraveller.setLastname(cursor.getString(cursor.getColumnIndex(col_BUS_TRAVELLER_LAST_NAME)));
                busTraveller.setAge(cursor.getString(cursor.getColumnIndex(col_BUS_TRAVELLER_AGE)));
                busTraveller.setTitle(cursor.getString(cursor.getColumnIndex(col_BUS_TRAVELLER_TITLE)));


                busTravellerArrayList.add(busTraveller);

            }while (cursor.moveToNext());
        }

        return busTravellerArrayList;
    }


    public int checkOnwardBusInfoStatus() {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT count(*) FROM " + tbl_bus_info + " WHERE " + col_WAY + "='" + context.getResources().getString(R.string.onw) + "' AND " + col_STATUS + "='A'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int count = Integer.parseInt(cursor.getString(0));
            cursor.close();
            return count;
        } else {
            cursor.close();
            return 0;
        }

    }

    public int checkReturnBusInfoStatus() {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT count(*) FROM " + tbl_bus_info + " WHERE " + col_WAY + "'" + context.getResources().getString(R.string.ret) + "' AND " + col_STATUS + "='A'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int count = Integer.parseInt(cursor.getString(0));
            cursor.close();
            return count;
        } else {
            cursor.close();
            return 0;
        }

    }

    public BusBasicInfo getOnwardBusInfo() {
        String way = context.getResources().getString(R.string.onw);
        BusBasicInfo busBasicInfo = new BusBasicInfo();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tbl_bus_info + " WHERE " + col_WAY + "='" + way + "' AND " + col_STATUS + "='A'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                busBasicInfo.setOrigin(cursor.getString(cursor.getColumnIndex(col_ORIGIN)));
                busBasicInfo.setDestination(cursor.getString(cursor.getColumnIndex(col_DESTINATION)));
                busBasicInfo.setBusoperator(cursor.getString(cursor.getColumnIndex(col_BUSOPERATOR)));
                busBasicInfo.setBoardingpoint(cursor.getString(cursor.getColumnIndex(col_BOARDINGPOINT)));
                busBasicInfo.setBoardingtime(cursor.getString(cursor.getColumnIndex(col_BOARDINGTIME)));
                busBasicInfo.setSeats(cursor.getString(cursor.getColumnIndex(col_SEATS)));
                busBasicInfo.setTotalprice(cursor.getString(cursor.getColumnIndex(col_TOTALPRICE)));
                busBasicInfo.setDeparturedate(cursor.getString(cursor.getColumnIndex(col_DEPDATE)));
                busBasicInfo.setArrivaldate(cursor.getString(cursor.getColumnIndex(col_ARRDATE)));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return busBasicInfo;


    }

    public BusBasicInfo getReturnBusInfo() {
        String way = context.getResources().getString(R.string.ret);
        BusBasicInfo busBasicInfo = new BusBasicInfo();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tbl_bus_info + " WHERE " + col_WAY + "='" + way + "' AND " + col_STATUS + "='A'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                busBasicInfo.setOrigin(cursor.getString(cursor.getColumnIndex(col_ORIGIN)));
                busBasicInfo.setDestination(cursor.getString(cursor.getColumnIndex(col_DESTINATION)));
                busBasicInfo.setBusoperator(cursor.getString(cursor.getColumnIndex(col_BUSOPERATOR)));
                busBasicInfo.setBoardingpoint(cursor.getString(cursor.getColumnIndex(col_BOARDINGPOINT)));
                busBasicInfo.setBoardingtime(cursor.getString(cursor.getColumnIndex(col_BOARDINGTIME)));
                busBasicInfo.setSeats(cursor.getString(cursor.getColumnIndex(col_SEATS)));
                busBasicInfo.setTotalprice(cursor.getString(cursor.getColumnIndex(col_TOTALPRICE)));
                busBasicInfo.setDeparturedate(cursor.getString(cursor.getColumnIndex(col_DEPDATE)));
                busBasicInfo.setArrivaldate(cursor.getString(cursor.getColumnIndex(col_ARRDATE)));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return busBasicInfo;


    }

    public ArrayList<String> getSkey(String way) {

        ArrayList<String> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tbl_bus_info + " WHERE " + col_WAY + "='" + way + "' AND " + col_STATUS + "='A'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            list.add(cursor.getString(cursor.getColumnIndex(col_SKEY)));
            list.add(cursor.getString(cursor.getColumnIndex(col_BOARDINGID)));
        }

        cursor.close();
        db.close();

        return list;


    }

    public String getSeatName(String way) {
        String seatname = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + col_SEATNAME + " FROM " + tbl_seat_map +" WHERE "+col_TRAVEL_TYPE +"='"+way+"'" ;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                seatname += cursor.getString(cursor.getColumnIndex(col_SEATNAME))+",";

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return seatname.substring(0,seatname.length()-1);
    }
    public int getTotalAmount() {
        int total_amount = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + col_SEATFARE + " FROM " + tbl_seat_map;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                total_amount += Integer.parseInt(cursor.getString(cursor.getColumnIndex(col_SEATFARE)));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return total_amount;
    }

    public ArrayList<BusSeatMapDetails> getSeatDetails(String way) {

        ArrayList<BusSeatMapDetails> busSeatMapDetailsArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tbl_seat_map + " WHERE " + col_TRAVEL_TYPE + "='"+way+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                BusSeatMapDetails busSeatMapDetails = new BusSeatMapDetails();
                busSeatMapDetails.setSeatName(cursor.getString(cursor.getColumnIndex(col_SEATNAME)));
                busSeatMapDetails.setBaseFare(cursor.getString(cursor.getColumnIndex(col_BASEFARE)));
                busSeatMapDetails.setServiceTax(cursor.getString(cursor.getColumnIndex(col_SERVICETAX)));
                busSeatMapDetails.setServiceTaxPercentage(cursor.getString(cursor.getColumnIndex(col_SERVICETAXPERCENTAGE)));
                busSeatMapDetails.setServiceCharge(cursor.getString(cursor.getColumnIndex(col_SERVICECHARGE)));
                busSeatMapDetails.setActualSeatFare(cursor.getString(cursor.getColumnIndex(col_ACTUALSEATFARE)));
                busSeatMapDetails.setSeatFare(cursor.getString(cursor.getColumnIndex(col_SEATFARE)));

                busSeatMapDetailsArrayList.add(busSeatMapDetails);


            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return busSeatMapDetailsArrayList;
    }

    public ArrayList<BusBoardingPoint> getBoardingPointAll() {

        ArrayList<BusBoardingPoint> boardingPointArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tbl_boarding_point;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                BusBoardingPoint busBoardingPoint = new BusBoardingPoint();
                busBoardingPoint.setBPId(cursor.getString(0));
                busBoardingPoint.setBPLocation(cursor.getString(1));
                busBoardingPoint.setBPTime(cursor.getString(2));
                busBoardingPoint.setBPName(cursor.getString(3));
                boardingPointArrayList.add(busBoardingPoint);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return boardingPointArrayList;
    }
    //Select Ends Here


    //Update Starts Here

    public void updateSeatStatus() {
        String way = GoibiboPref.getString(context.getResources().getString(R.string.way), "");
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(col_STATUSSEAT, "A");

        db.update(tbl_seat_map, values, col_TRAVEL_TYPE + " = '" + way + "'", null);
    }

    public void updateBusTravellerInfo(BusTraveller busTraveller, String tag) {

        int id = Integer.parseInt(tag);

        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(col_BUS_TRAVELLER_FIRST_NAME, busTraveller.getFirstname());
        values.put(col_BUS_TRAVELLER_MIDDLE_NAME, busTraveller.getMiddlename());
        values.put(col_BUS_TRAVELLER_LAST_NAME, busTraveller.getLastname());
        values.put(col_BUS_TRAVELLER_AGE, busTraveller.getAge());
        values.put(col_BUS_TRAVELLER_TITLE, busTraveller.getTitle());


        db.update(tbl_bus_traveller, values, col_BUS_TRAVELLER_ID + " = " + id, null);

        Log.i(LOG, "Updated the bus traveller info" + id);


    }

    public void updateBusInfoSeatAndTotal(String seat, String totalprice) {

        String way = GoibiboPref.getString(context.getResources().getString(R.string.way), "");
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(col_SEATS, seat);
        values.put(col_TOTALPRICE, totalprice);

        db.update(tbl_bus_info, values, col_WAY + " = '" + way + "'", null);


    }

    public void updateBusInfo(String boardingpoint, String boardingtime, String BPid) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        SimpleDateFormat change_df = new SimpleDateFormat("hh:mm");

        try {
            Date d1 = df.parse(boardingtime);
            boardingtime = change_df.format(d1);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        String way = GoibiboPref.getString(context.getResources().getString(R.string.way), "");
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(col_BOARDINGPOINT, boardingpoint);
        values.put(col_BOARDINGTIME, boardingtime);
        values.put(col_BOARDINGID, BPid);
        values.put(col_STATUS, "A");

        db.update(tbl_bus_info, values, col_WAY + " = '" + way + "'", null);


    }

    //Update Ends Here

    //Delete Starts Here
    public void deleteSeatMap(int id) {
        String way = GoibiboPref.getString(context.getString(R.string.way),"");
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + tbl_seat_map + " WHERE " + col_ID_SEATMAP + "=" + id + " AND "+ col_TRAVEL_TYPE + "='"+way+"'";
        db.execSQL(deleteQuery);
        db.close();
    }


    public void deleteSeatMapAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tbl_seat_map);
        db.close();
    }

    public void deletbusInfoAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tbl_bus_info);
        db.close();
    }


    public void deletebusTraveller() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tbl_bus_traveller);
        db.close();
    }

    public void deletebusBoardingPoint() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tbl_boarding_point);
        db.close();
    }

    //Delete Ends Here



}
