package com.tcs.mmpl.customer.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "MyDBHelper";

    public static final String DATABASE_NAME = "mrupee";// database name
    public static final int DATABASE_VERSION = 2;

    // table name: tbl_user_master
    public static final String tbl_beneficiary_details_TABLE = "tbl_beneficiary_details";
    public static final String tbl_favourites_details_TABLE = "tbl_favourites_details";
    public static final String tbl_offers_TABLE = "tbl_offers";
    public static final String tbl_profile_image_TABLE = "tbl_profile_image";

    // .......................columns
    // name.......................................

    public static final String tbl_beneficiary_details_ID_BENEFICIARY = "ID_BENEFICIARY";
    public static final String tbl_beneficiary_details_BENEFICIARY_CODE = "BENEFICIARY_CODE";
    public static final String tbl_beneficiary_details_BENEFICIARY_NICK_NAME = "BENEFICIARY_NICK_NAME ";
    public static final String tbl_beneficiary_details_ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    public static final String tbl_beneficiary_details_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public static final String tbl_beneficiary_details_MOBILE_NUMBER = "MOBILE_NUMBER";
    public static final String tbl_beneficiary_details_BANK_TYPE = "BANK_TYPE";
    public static final String tbl_beneficiary_details_BANK_NAME = "BANK_NAME";
    public static final String tbl_beneficiary_details_BRANCH_NAME = "BRANCH_NAME";
    public static final String tbl_beneficiary_details_BENEFICIARY_AADHAAR_NUMBER = "BENEFICIARY_AADHAAR_NUMBER";
    public static final String tbl_beneficiary_details_BENEFICIARY_AADHAAR_STATUS = "BENEFICIARY_AADHAAR_STATUS";
    public static final String tbl_beneficiary_details_ROUTING_BANK_TYPE = "ROUTING_BANK_TYPE";
    public static final String tbl_beneficiary_details_VALIDATE_STATUS = "VALIDATE_STATUS";
    public static final String tbl_beneficiary_details_BENEFICIARY_NAME = "BENEFICIARY_NAME";
    public static final String tbl_beneficiary_details_IFSC_CODE = "IFSC_CODE";


    public static final String tbl_favourites_details_ID_FAVOURITES = "ID_FAVOURITES";
    public static final String tbl_favourites_details_PARAMETER_TYPE= "PARAMETER_TYPE";
    public static final String tbl_favourites_details_PARAMETER_VALUE = "PARAMETER_VALUE";
    public static final String tbl_favourites_details_FAVOURITES_URL = "FAVOURITES_URL";

    public static final String tbl_offers_ID = "IMAGE_ID";
    public static final String tbl_offers_IMAGE_BLOB = "IMAGE_BLOB";
    public static final String tbl_offers_IMAGE = "IMAGE";
    public static final String tbl_offers_OFFERS_URL = "OFFERS_URL";
    public static final String tbl_offers_OFFERS_NAME = "tbl_offer_name";

    public static  final String tbl_profile_image_IMAGE = "PROFILE_IMAGE";

//	// table name: tbl_ota
//	public static final String tbl_ota_TABLE = "tbl_ota";
//	// .......................columns
//	// name.......................................
//	public static final String tbl_ota_ID_OTA = "ID_OTA";
//	public static final String tbl_ota_OTA = "OTA";
//	public static final String tbl_ota_ASSOCIATED_NUMBER = "ASSOCIATED_NUMBER";
//	public static final String tbl_ota_STATUS = "STATUS";
//	public static final String tbl_ota_UPDATED_DATE_TIME = "UPDATED_DATE_TIME";
//
//	// table name: tbl_service_type
//	public static final String tbl_service_type_TABLE = "tbl_service_type";
//	// .......................columns
//	// name.......................................
//	public static final String tbl_service_type_ID = "ID_PRIMARY";
//	public static final String tbl_service_type_ID_SERVICE_TYPE = "ID_SERVICE_TYPE";
//	public static final String tbl_service_type_SERVICE_TYPE_NAME = "SERVICE_TYPE_NAME";
//	public static final String tbl_service_type_DESCRIPTION = "DESCRIPTION";
//	public static final String tbl_service_type_STATUS = "STATUS";
//
//	// table name: tbl_product_type
//	public static final String tbl_product_type_TABLE = "tbl_product_type";
//	// .......................columns
//	// name.......................................
//	public static final String tbl_product_type_ID = "ID_PRIMARY";
//	public static final String tbl_product_type_ID_PRODUCT_TYPE = "ID_PRODUCT_TYPE";
//	public static final String tbl_product_type_PRODUCT_TYPE_NAME = "PRODUCT_TYPE_NAME";
//	public static final String tbl_product_type_DESCRIPTION = "DESCRIPTION";
//	public static final String tbl_product_type_STATUS = "STATUS";
//
//	// table name: tbl_bu_type
//	public static final String tbl_buservprod_link_TABLE = "tbl_buservprod_link";
//	// .......................columns
//	// name.......................................
//	public static final String tbl_buservprod_link_ID = "ID_PRIMARY";
//	public static final String tbl_buservprod_link_ID_LINK = "ID_LINK";
//	public static final String tbl_buservprod_link_ID_BU_TYPE = "ID_BU_TYPE";
//	public static final String tbl_buservprod_link_ID_SERVICE_TYPE = "ID_SERVICE_TYPE";
//	public static final String tbl_buservprod_link_ID_PRODUCT_TYPE = "ID_PRODUCT_TYPE";
//	public static final String tbl_buservprod_link_MEANING = "MEANING";
//	public static final String tbl_buservprod_link_STATUS = "STATUS";


    // .............................. Table Create
    // Statements................................

    private static final String CREATE_TABLE_tbl_beneficiary_details = "CREATE TABLE IF NOT EXISTS  "
            + tbl_beneficiary_details_TABLE
            + "("
            + tbl_beneficiary_details_ID_BENEFICIARY
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + tbl_beneficiary_details_BENEFICIARY_CODE
            + " TEXT NOT NULL,"
            + tbl_beneficiary_details_BENEFICIARY_NAME
            + " TEXT NOT NULL,"
            + tbl_beneficiary_details_BENEFICIARY_NICK_NAME
            + " TEXT NOT NULL,"
            + tbl_beneficiary_details_ACCOUNT_NUMBER
            + " TEXT NOT NULL,"
            + tbl_beneficiary_details_ACCOUNT_TYPE
            + " TEXT NOT NULL,"
            + tbl_beneficiary_details_BANK_NAME
            + " TEXT NOT NULL,"
            + tbl_beneficiary_details_BANK_TYPE
            + " TEXT DEFAULT NULL,"
            + tbl_beneficiary_details_MOBILE_NUMBER
            + " TEXT NOT NULL,"
            + tbl_beneficiary_details_BRANCH_NAME
            + " TEXT NOT NULL,"
            + tbl_beneficiary_details_IFSC_CODE
            + " TEXT NOT NULL,"
            + tbl_beneficiary_details_BENEFICIARY_AADHAAR_NUMBER
            + " TEXT NOT NULL,"
            + tbl_beneficiary_details_BENEFICIARY_AADHAAR_STATUS
            + " TEXT NOT NULL,"
            + tbl_beneficiary_details_ROUTING_BANK_TYPE
            + " TEXT NOT NULL,"
            + tbl_beneficiary_details_VALIDATE_STATUS
            + " TEXT NOT NULL " + ")";




    private static final String CREATE_TABLE_tbl_favourites_details = "CREATE TABLE IF NOT EXISTS  "
            + tbl_favourites_details_TABLE
            + "("
            + tbl_favourites_details_ID_FAVOURITES
            + " TEXT NOT NULL,"
            + tbl_favourites_details_PARAMETER_TYPE
            + " TEXT NOT NULL,"
            + tbl_favourites_details_PARAMETER_VALUE
            + " TEXT NOT NULL,"
            + tbl_favourites_details_FAVOURITES_URL
            + " TEXT NOT NULL " + ")";

    private static final String CREATE_TABLE_tbl_offers = "CREATE TABLE IF NOT EXISTS  "
            + tbl_offers_TABLE
            + "("
            + tbl_offers_ID
            + " TEXT NOT NULL,"
            + tbl_offers_IMAGE_BLOB
            + " BLOB,"
            + tbl_offers_IMAGE
            + " TEXT NOT NULL,"
            + tbl_offers_OFFERS_URL
            + " TEXT NOT NULL,"
            + tbl_offers_OFFERS_NAME
            + " TEXT NOT NULL "+ ")";


    private static final String CREATE_TABLE_tbl_profile_image = "CREATE TABLE IF NOT EXISTS  "
            + tbl_profile_image_TABLE
            + "("
            + tbl_profile_image_IMAGE
            + " BLOB " + ")";


    // .............................. Insert
    // Statements................................



    public void fun_insert_tbl_beneficiary_details(String beneficiaryCode,
                                                   String beneficiaryNickname, String accountNumber,
                                                   String accountType, String mobileNumber, String bankType,
                                                   String bankName, String branchName, String beneficiaryAadhaarNo, String aadharStatus, String routingBankType, String validateStatus, String beneficiaryName, String ifsccode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(tbl_beneficiary_details_BENEFICIARY_CODE, beneficiaryCode);
        values.put(tbl_beneficiary_details_BENEFICIARY_NICK_NAME, beneficiaryNickname);
        values.put(tbl_beneficiary_details_ACCOUNT_NUMBER, accountNumber);
        values.put(tbl_beneficiary_details_ACCOUNT_TYPE, accountType);
        values.put(tbl_beneficiary_details_MOBILE_NUMBER, mobileNumber);
        values.put(tbl_beneficiary_details_BANK_TYPE, bankType);
        values.put(tbl_beneficiary_details_BANK_NAME, bankName);
        values.put(tbl_beneficiary_details_BRANCH_NAME, branchName);
        values.put(tbl_beneficiary_details_BENEFICIARY_AADHAAR_NUMBER, beneficiaryAadhaarNo);
        values.put(tbl_beneficiary_details_BENEFICIARY_AADHAAR_STATUS, aadharStatus);
        values.put(tbl_beneficiary_details_ROUTING_BANK_TYPE, routingBankType);
        values.put(tbl_beneficiary_details_VALIDATE_STATUS, validateStatus);
        values.put(tbl_beneficiary_details_BENEFICIARY_NAME, beneficiaryName);
        values.put(tbl_beneficiary_details_IFSC_CODE, ifsccode);

        // insert row
        db.insert(tbl_beneficiary_details_TABLE, null, values);

        db.close();
        Log.i(LOG, "Inserted in table tbl_beneficiary_details ");
    }


    public void fun_insert_tbl_favourites_details(String favouriteID,
                                                   String parameterType, String parameterValue,
                                                   String favouriteURL) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(tbl_favourites_details_ID_FAVOURITES, favouriteID);
        values.put(tbl_favourites_details_PARAMETER_TYPE, parameterType);
        values.put(tbl_favourites_details_PARAMETER_VALUE, parameterValue);
        values.put(tbl_favourites_details_FAVOURITES_URL, favouriteURL);


        // insert row
        db.insert(tbl_favourites_details_TABLE, null, values);

        db.close();
        Log.i(LOG, "Inserted in table tbl_favourites_details ");
    }


    public void fun_insert_tbl_offers(String id, String image_url,String offer_url ,String offer_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(tbl_offers_ID, id);
        values.put(tbl_offers_IMAGE_BLOB, "");
        values.put(tbl_offers_IMAGE, image_url);
        values.put(tbl_offers_OFFERS_URL, offer_url);
        values.put(tbl_offers_OFFERS_NAME,offer_name);


        // insert row
        db.insert(tbl_offers_TABLE, null, values);

        db.close();
        Log.i(LOG, "Inserted in table tbl_offers_details ");
    }



    public void fun_insert_tbl_profileImage(byte image[]) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(tbl_profile_image_IMAGE, image);

        // insert row
        db.insert(tbl_profile_image_TABLE, null, values);

        db.close();
        Log.i(LOG, "Inserted in table tbl_favourites_details ");
    }


    // .............................. Select All
    // Statements................................


    public Cursor fun_select_tbl_beneficiary_details() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + tbl_beneficiary_details_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.i(LOG, "Selected all from table tbl_beneficiary_details_TABLE ");
        return cursor;
    }

    public Cursor fun_select_beneficiary(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + tbl_beneficiary_details_TABLE+" WHERE "
		+ tbl_beneficiary_details_ID_BENEFICIARY + " = " + id + "";
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.i(LOG, "Selected all from table tbl_beneficiary_details_TABLE ");
        return cursor;
    }

    public String fun_select_beneficiary_code(String accountnumber) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + tbl_beneficiary_details_TABLE+" WHERE "
                + tbl_beneficiary_details_ACCOUNT_NUMBER + " = '" + accountnumber + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst())
        {
            return cursor.getString(cursor.getColumnIndex(tbl_beneficiary_details_BENEFICIARY_CODE));
        }

        Log.i(LOG, "Selected all from table tbl_beneficiary_details_TABLE ");
        return "NA";
    }


    public Cursor fun_select_tbl_favourites_details() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + tbl_favourites_details_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.i(LOG, "Selected all from table tbl_favourites_details_TABLE ");
        return cursor;
    }


    public Cursor fun_select_tbl_Offers() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + tbl_offers_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.i(LOG, "Selected all from table tbl_favourites_Offers ");
        return cursor;
    }

    public Cursor fun_select_tbl_profileImage() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + tbl_profile_image_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.i(LOG, "Selected all from table tbl_profile_image ");
        return cursor;
    }

    // .............................Update the
    // tables.................................

    public void updateOffers(byte image[],String id) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();

        //// System.out.println(image);

        ContentValues values = new ContentValues();

        values.put(tbl_offers_IMAGE_BLOB,image);


        db.update(tbl_offers_TABLE, values, tbl_offers_ID
                + " = '" + id + "'", null);

        db.close();

    }

    public void updateProfileImage(byte image[]) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();

        //// System.out.println(image);

        ContentValues values = new ContentValues();

        values.put(tbl_profile_image_IMAGE,image);


        db.update(tbl_profile_image_TABLE, values, null, null);

        db.close();

    }

    // ..............................Delete the
    // tables................................

//	public void fun_delete_othermdns(String mdn) {
//
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		String deleteQuery = "DELETE FROM "+tbl_temp_other_mdns_TABLE+" WHERE "
//		+ tbl_temp_other_mdns_OTHER_MDNS + " = '" + mdn + "'";
//
//		db.execSQL(deleteQuery);
//
//		// create new tables
//		onCreate(db);
//
//		db.close();
//	}


	public void fun_deleteAll_tbl_beneficiary_details() {

		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + tbl_beneficiary_details_TABLE);

		// create new tables
		onCreate(db);
		db.close();
	}

    public void fun_deleteAll_tbl_offers() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tbl_offers_TABLE);

        // create new tables
        onCreate(db);
        db.close();
    }

    public void fun_deleteAll_tbl_favourites_details() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tbl_favourites_details_TABLE);

        // create new tables
        onCreate(db);
        db.close();
    }

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG, "On create method");
        db.execSQL(CREATE_TABLE_tbl_beneficiary_details);
        db.execSQL(CREATE_TABLE_tbl_favourites_details);
        db.execSQL(CREATE_TABLE_tbl_offers);
        db.execSQL(CREATE_TABLE_tbl_profile_image);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(LOG, "On upgrade method");
        db.execSQL("DROP TABLE IF EXISTS " + tbl_beneficiary_details_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + tbl_favourites_details_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + tbl_offers_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + tbl_profile_image_TABLE);

        onCreate(db);
    }

}
