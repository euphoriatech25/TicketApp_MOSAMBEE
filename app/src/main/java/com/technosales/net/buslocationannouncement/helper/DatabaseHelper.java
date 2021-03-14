/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.technosales.net.buslocationannouncement.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.technosales.net.buslocationannouncement.network.RouteStation;
import com.technosales.net.buslocationannouncement.network.TicketInfoDataPush;
import com.technosales.net.buslocationannouncement.pojo.AdvertiseList;
import com.technosales.net.buslocationannouncement.pojo.BlockListModel;
import com.technosales.net.buslocationannouncement.pojo.HelperList;
import com.technosales.net.buslocationannouncement.pojo.PassengerCountList;
import com.technosales.net.buslocationannouncement.pojo.PriceList;
import com.technosales.net.buslocationannouncement.pojo.RouteStationList;
import com.technosales.net.buslocationannouncement.pojo.TicketInfoList;
import com.technosales.net.buslocationannouncement.trackcar.Position;
import com.technosales.net.buslocationannouncement.trackcar.TrackingService;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.DEVICE_ID;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.MODE;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "traccar.db";

    public static final String ROUTE_STATION_TABLE = "route_station";
    public static final String STATION_ID = "station_id";
    public static final String STATION_ORDER = "station_order";
    public static final String STATION_NAME = "name_nepali";
    public static final String STATION_LAT = "latitude";
    public static final String STATION_LNG = "longitude";
    public static final String STATION_DISTANCE = "station_distance";
    public static final String PRICE_TABLE = "price_table";
    public static final String PRICE_VALUE = "price_value";
    public static final String PRICE_DISCOUNT_VALUE = "price_discount_value";
    public static final String PRICE_DISTANCE = "price_distance";
    public static final String PRICE_MIN_DISTANCE = "price_min_distance";
    public static final String TICKET_TABLE = "ticket_table";
    public static final String TICKET_TABLE_TXT = "ticket_table_txt";
    public static final String BLOCK_LIST_TABLE_TXT = "block_list_table_txt";
    public static final String PASSENGER_COUNT_TABLE = "passenger_count_txt";
    public static final String TICKET_ID = "ticket_id";
    public static final String TRANSACTION_TYPE = "transactionType";
    public static final String TRANSACTION_MEDIUM = "transactionMedium";
    public static final String TRANSACTION_AMOUNT = "transactionAmount";
    public static final String TRANSACTION_FEE = "transactionFee";
    public static final String TRANSACTION_COMMISSION = "transactionCommission";
    public static final String IS_ONLINE = "isOnline";
    public static final String DEVICE_TIME = "device_time";
    public static final String OFFLINE_REF_ID = "offlineRefId";
    public static final String USER_TYPE = "userType";
    public static final String LAT = "lat";
    public static final String lNG = "lng";
    public static final String STATUS = "status";
    public static final String REFERENCE_ID = "referenceId";
    public static final String REFERENCEHASH = "referenceHash";
    public static final String PASSENGER_ID = "passenger_id";
    public static final String IDENTIFICATION = "identification";
    public static final String MOBILE_NO = "mobile_no";
    public static final String HELPER_TABLE = "helper_table";
    public static final String HELPER_ID = "helper_id";
    public static final String HELPER_NAME = "helper_name";
    public static final String ADVERTISEMENT_TABLE = "ad_table";
    public static final String ADVERTISEMENT_ID = "ad_id";
    public static final String ADVERTISEMENT_STATIONS = "ad_stations";
    public static final String ADVERTISEMENT_FILE = "ad_file";
    public static final String ADVERTISEMENT_COUNT = "ad_count";
    public static final String ADVERTISEMENT_TYPE = "ad_type";
    private static final String STATION_NAME_ENG = "name_english";

    private static final String PASSENGER_STATION_POSITION = "passenger_station_position";
    private static final String PASSENGER_DIRECTION = "passenger_direction";
    private final Context context;
    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE position (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "deviceId TEXT," +
                "time INTEGER," +
                "latitude REAL," +
                "longitude REAL," +
                "altitude REAL," +
                "speed REAL," +
                "course REAL," +
                "accuracy REAL," +
                "battery REAL," +
                "mock INTEGER)");

        db.execSQL("CREATE TABLE ticket_table (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                TICKET_ID + " TEXT," +
                TRANSACTION_TYPE + " TEXT," +
                TRANSACTION_MEDIUM + " TEXT," +
                TRANSACTION_AMOUNT + " TEXT," +
                TRANSACTION_FEE + " TEXT," +
                TRANSACTION_COMMISSION + " TEXT," +

                IS_ONLINE + " TEXT," +
                DEVICE_TIME + " TEXT," +
                OFFLINE_REF_ID + " TEXT," +
                USER_TYPE + " TEXT," +
                LAT + " TEXT," +
                lNG + " TEXT," +
                STATUS + " TEXT," +
                REFERENCE_ID + " TEXT," +

                REFERENCEHASH + " TEXT," +
                PASSENGER_ID + " TEXT," +
                DEVICE_ID + " TEXT," +
                HELPER_ID + " TEXT)");

        db.execSQL("CREATE TABLE ticket_table_txt (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                TICKET_ID + " TEXT," +
                TRANSACTION_TYPE + " TEXT," +
                TRANSACTION_MEDIUM + " TEXT," +
                TRANSACTION_AMOUNT + " TEXT," +
                TRANSACTION_FEE + " TEXT," +
                TRANSACTION_COMMISSION + " TEXT," +

                IS_ONLINE + " TEXT," +
                DEVICE_TIME + " TEXT," +
                OFFLINE_REF_ID + " TEXT," +
                USER_TYPE + " TEXT," +
                LAT + " TEXT," +
                lNG + " TEXT," +
                STATUS + " TEXT," +
                REFERENCE_ID + " TEXT," +

                REFERENCEHASH + " TEXT," +
                PASSENGER_ID + " TEXT," +
                DEVICE_ID + " TEXT," +
                HELPER_ID + " TEXT)");

        db.execSQL("CREATE TABLE price_table (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                PRICE_VALUE + " TEXT," +
                PRICE_DISCOUNT_VALUE + " TEXT," +
                PRICE_MIN_DISTANCE + " INTEGER," +
                PRICE_DISTANCE + " INTEGER)");

        db.execSQL("CREATE TABLE route_station (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                STATION_ID + " TEXT," +
                STATION_ORDER + " INTEGER," +
                STATION_NAME + " TEXT," +
                STATION_NAME_ENG + " TEXT," +
                STATION_LAT + " TEXT," +
                STATION_LNG + " TEXT," +
                STATION_DISTANCE + " FLOAT)");
        db.execSQL("CREATE TABLE helper_table (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                HELPER_ID + " TEXT," +
                HELPER_NAME + " TEXT)");

        db.execSQL("CREATE TABLE " + ADVERTISEMENT_TABLE + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                ADVERTISEMENT_ID + " TEXT," +
                ADVERTISEMENT_STATIONS + " TEXT," +
                ADVERTISEMENT_FILE + " TEXT," +
                ADVERTISEMENT_TYPE + " INTEGER," +
                ADVERTISEMENT_COUNT + " INTEGER)");


        db.execSQL("CREATE TABLE " + BLOCK_LIST_TABLE_TXT + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                IDENTIFICATION + " TEXT," +
                MOBILE_NO + " TEXT)");


        db.execSQL("CREATE TABLE " + PASSENGER_COUNT_TABLE + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                 PASSENGER_STATION_POSITION + " TEXT," +
                PASSENGER_DIRECTION + " TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAndCreate(db);

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAndCreate(db);

    }

    private void dropAndCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS position;");
        db.execSQL("DROP TABLE IF EXISTS " + TICKET_TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + PRICE_TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + HELPER_TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + ROUTE_STATION_TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TICKET_TABLE_TXT + ";");
        db.execSQL("DROP TABLE IF EXISTS " + ADVERTISEMENT_TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + BLOCK_LIST_TABLE_TXT + ";");
        db.execSQL("DROP TABLE IF EXISTS " + PASSENGER_COUNT_TABLE + ";");
        onCreate(db);
    }

    public void insertPosition(Position position) {
        ContentValues values = new ContentValues();
        values.put("deviceId", position.getDeviceId());
        values.put("time", position.getTime().getTime());
        values.put("latitude", position.getLatitude());
        values.put("longitude", position.getLongitude());
        values.put("altitude", position.getAltitude());
        values.put("speed", position.getSpeed());
        values.put("course", position.getCourse());
        values.put("accuracy", position.getAccuracy());
        values.put("battery", position.getBattery());
        values.put("mock", position.getMock() ? 1 : 0);

        Log.i("dbValues", values + "");

        db.insertOrThrow("position", null, values);
    }

    public void insertStations(RouteStationList routeStationList) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        contentValues.put(STATION_ID, routeStationList.station_id);
        contentValues.put(STATION_ORDER, routeStationList.station_order);
        contentValues.put(STATION_NAME, routeStationList.station_name);
        contentValues.put(STATION_NAME_ENG, routeStationList.station_name_eng);
        contentValues.put(STATION_LAT, routeStationList.station_lat);
        contentValues.put(STATION_LNG, routeStationList.station_lng);
        contentValues.put(STATION_DISTANCE, routeStationList.station_distance);
        sqLiteDatabase.insert(ROUTE_STATION_TABLE, null, contentValues);
        Log.i("routeStation", "" + routeStationList.station_name + ":" + routeStationList.station_distance);
    }

    public void insertHelpers(ContentValues contentValues) {
        Log.i("helperValue", "" + contentValues.toString());
        getWritableDatabase().insert(HELPER_TABLE, null, contentValues);
    }

    public void insertPrice(ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Log.i("getValue", "" + contentValues.toString());
        sqLiteDatabase.insert(PRICE_TABLE, null, contentValues);
    }

    public void insertAdv(ContentValues contentValues) {
        getWritableDatabase().insert(ADVERTISEMENT_TABLE, null, contentValues);
        Log.i("insertAd", "" + contentValues.toString());
    }

    public void insertTicketInfo(TicketInfoList ticketInfoList) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        contentValues.put(TICKET_ID, ticketInfoList.ticket_id);
        contentValues.put(TRANSACTION_TYPE, ticketInfoList.transactionType);
        contentValues.put(TRANSACTION_MEDIUM, ticketInfoList.transactionMedium);
        contentValues.put(TRANSACTION_AMOUNT, ticketInfoList.transactionAmount);
        contentValues.put(TRANSACTION_FEE, ticketInfoList.transactionFee);
        contentValues.put(TRANSACTION_COMMISSION, ticketInfoList.transactionCommission);
        contentValues.put(IS_ONLINE, ticketInfoList.isOnline);
        contentValues.put(DEVICE_TIME, ticketInfoList.device_time);

        contentValues.put(OFFLINE_REF_ID, ticketInfoList.offlineRefId);
        contentValues.put(USER_TYPE, ticketInfoList.userType);
        contentValues.put(LAT, ticketInfoList.lat);
        contentValues.put(lNG, ticketInfoList.lng);
        contentValues.put(STATUS, ticketInfoList.status);
        contentValues.put(REFERENCE_ID, ticketInfoList.referenceId);
        contentValues.put(REFERENCEHASH, ticketInfoList.referenceHash);
        contentValues.put(PASSENGER_ID, ticketInfoList.passenger_id);
        contentValues.put(HELPER_ID, ticketInfoList.helper_id);
        contentValues.put(DEVICE_ID, ticketInfoList.device_id);
        sqLiteDatabase.insert(TICKET_TABLE, null, contentValues);


        insertTicketTxt(contentValues);

       /* boolean datasending = context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).getBoolean(UtilStrings.DATA_SENDING, false);
        if (!datasending) {
            ticketInfoLists();
        }*/
    }

    public void insertBlockList(BlockListModel blockListModel) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        contentValues.put(IDENTIFICATION, blockListModel.identificationId);
        contentValues.put(MOBILE_NO, blockListModel.mobileNo);
        sqLiteDatabase.insert(BLOCK_LIST_TABLE_TXT, null, contentValues);
    }


    public void insertPassengerCountList(PassengerCountList passengerCountList) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        contentValues.put(PASSENGER_STATION_POSITION, passengerCountList.passenger_station_position);
        contentValues.put(PASSENGER_DIRECTION, passengerCountList.passenger_direction);
        sqLiteDatabase.insert(PASSENGER_COUNT_TABLE, null, contentValues);
        Log.i(TAG, "insertPassengerCountList: "+passengerCountList.passenger_station_position+":: "+passengerCountList.passenger_direction);
    }

    public float distancesFromStart() {
        float total = 0;
        Cursor c = getReadableDatabase().rawQuery("SELECT " + STATION_DISTANCE + " FROM " + ROUTE_STATION_TABLE, null);
        try {
            while (c.moveToNext()) {
                total = c.getFloat(c.getColumnIndex(STATION_DISTANCE));

            }
        } finally {
            if (c != null)
                c.close();
        }

/*
        Log.i("routeStation", "total-" +total);
*/
        return total;
    }

    public double recentStationLat(int order) {
        double lat = 0.0;
        Cursor c = getReadableDatabase().rawQuery("SELECT " + STATION_LAT + " FROM " + ROUTE_STATION_TABLE + " WHERE " + STATION_ORDER + " =" + order, null);
        try {
            while (c.moveToNext()) {
                lat = Double.parseDouble(c.getString(c.getColumnIndex(STATION_LAT)));
            }
        } finally {
            if (c != null)
                c.close();
        }

        return lat;
    }

    public double recentStationLng(int order) {
        double lat = 0.0;
        Cursor c = getReadableDatabase().rawQuery("SELECT " + STATION_LNG + " FROM " + ROUTE_STATION_TABLE + " WHERE " + STATION_ORDER + " =" + order, null);
        try {
            while (c.moveToNext()) {
                lat = Double.parseDouble(c.getString(c.getColumnIndex(STATION_LNG)));
            }
        }finally {
            if (c != null)
            c.close();
        }

        return lat;
    }

    public void insertTicketTxt(ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.insert(TICKET_TABLE_TXT, null, contentValues);
        /*writeToFile();*/ // need at the end of the day
    }

    public List<TicketInfoList> listTickets() {
        List<TicketInfoList> ticketInfoLists = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TICKET_TABLE, null);

        try {
            while (cursor.moveToNext()) {
                TicketInfoList ticketInfoList = new TicketInfoList();
                ticketInfoList.ticket_id = cursor.getString(cursor.getColumnIndex(TICKET_ID));
                ticketInfoList.transactionType = cursor.getString(cursor.getColumnIndex(TRANSACTION_TYPE));
                ticketInfoList.transactionMedium = cursor.getString(cursor.getColumnIndex(TRANSACTION_MEDIUM));
                ticketInfoList.transactionAmount = cursor.getString(cursor.getColumnIndex(TRANSACTION_AMOUNT));
                ticketInfoList.transactionFee = cursor.getString(cursor.getColumnIndex(TRANSACTION_FEE));
                ticketInfoList.transactionCommission = cursor.getString(cursor.getColumnIndex(TRANSACTION_COMMISSION));
                ticketInfoList.isOnline = cursor.getString(cursor.getColumnIndex(IS_ONLINE));

                ticketInfoList.device_time = cursor.getString(cursor.getColumnIndex(DEVICE_TIME));
                ticketInfoList.offlineRefId = cursor.getString(cursor.getColumnIndex(OFFLINE_REF_ID));
                ticketInfoList.userType = cursor.getString(cursor.getColumnIndex(USER_TYPE));
                ticketInfoList.lat = cursor.getString(cursor.getColumnIndex(LAT));
                ticketInfoList.lng = cursor.getString(cursor.getColumnIndex(lNG));

                ticketInfoList.status = cursor.getString(cursor.getColumnIndex(STATUS));
                ticketInfoList.referenceId = cursor.getString(cursor.getColumnIndex(REFERENCE_ID));
                ticketInfoList.referenceHash = cursor.getString(cursor.getColumnIndex(REFERENCEHASH));
                ticketInfoList.passenger_id = cursor.getString(cursor.getColumnIndex(PASSENGER_ID));
                ticketInfoList.helper_id = cursor.getString(cursor.getColumnIndex(HELPER_ID));
                ticketInfoList.device_id = cursor.getString(cursor.getColumnIndex(DEVICE_ID));
                ticketInfoLists.add(ticketInfoList);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return ticketInfoLists;

    }

    public List<BlockListModel> listBlockList() {
        List<BlockListModel> blockInfoLists = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + BLOCK_LIST_TABLE_TXT, null);
        try {


            while (cursor.moveToNext()) {
                BlockListModel blockListModel = new BlockListModel();
                blockListModel.identificationId = cursor.getString(cursor.getColumnIndex(IDENTIFICATION));
                blockListModel.mobileNo = cursor.getString(cursor.getColumnIndex(MOBILE_NO));
                blockInfoLists.add(blockListModel);
            }
        }finally {
            if (cursor != null)
                cursor.close();
        }
        return blockInfoLists;

    }

    public int remainingAmount() {
        int amount = 0;
        int ticketAmount;
        List<TicketInfoList> ticketInfoLists = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TICKET_TABLE, null);
        try {
            while (cursor.moveToNext()) {
                TicketInfoList ticketInfoList = new TicketInfoList();
                ticketInfoList.ticket_id = cursor.getString(cursor.getColumnIndex(TICKET_ID));
                ticketInfoList.transactionType = cursor.getString(cursor.getColumnIndex(TRANSACTION_TYPE));
                ticketInfoList.transactionMedium = cursor.getString(cursor.getColumnIndex(TRANSACTION_MEDIUM));
                ticketInfoList.transactionAmount = cursor.getString(cursor.getColumnIndex(TRANSACTION_AMOUNT));
                ticketInfoList.transactionFee = cursor.getString(cursor.getColumnIndex(TRANSACTION_FEE));
                ticketInfoList.transactionCommission = cursor.getString(cursor.getColumnIndex(TRANSACTION_COMMISSION));
                ticketInfoList.isOnline = cursor.getString(cursor.getColumnIndex(IS_ONLINE));

                ticketInfoList.device_time = cursor.getString(cursor.getColumnIndex(DEVICE_TIME));
                ticketInfoList.offlineRefId = cursor.getString(cursor.getColumnIndex(OFFLINE_REF_ID));
                ticketInfoList.userType = cursor.getString(cursor.getColumnIndex(USER_TYPE));
                ticketInfoList.lat = cursor.getString(cursor.getColumnIndex(LAT));
                ticketInfoList.lng = cursor.getString(cursor.getColumnIndex(lNG));

                ticketInfoList.status = cursor.getString(cursor.getColumnIndex(STATUS));
                ticketInfoList.referenceId = cursor.getString(cursor.getColumnIndex(REFERENCE_ID));
                ticketInfoList.referenceHash = cursor.getString(cursor.getColumnIndex(REFERENCEHASH));
                ticketInfoList.passenger_id = cursor.getString(cursor.getColumnIndex(PASSENGER_ID));
                ticketInfoList.helper_id = cursor.getString(cursor.getColumnIndex(HELPER_ID));
                ticketInfoList.device_id = cursor.getString(cursor.getColumnIndex(DEVICE_ID));
                ticketInfoLists.add(ticketInfoList);
                ticketAmount = Integer.parseInt(ticketInfoList.transactionAmount);
                amount = amount + ticketAmount;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return amount;
    }

    public void ticketInfoLists() {
        int id = 0;
        List<TicketInfoList> ticketInfoLists = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TICKET_TABLE + " LIMIT 1", null);

        try {
            while (cursor.moveToNext()) {
                TicketInfoList ticketInfoList = new TicketInfoList();
                ticketInfoList.ticket_id = cursor.getString(cursor.getColumnIndex(TICKET_ID));
                ticketInfoList.transactionType = cursor.getString(cursor.getColumnIndex(TRANSACTION_TYPE));
                ticketInfoList.transactionMedium = cursor.getString(cursor.getColumnIndex(TRANSACTION_MEDIUM));
                ticketInfoList.transactionAmount = cursor.getString(cursor.getColumnIndex(TRANSACTION_AMOUNT));
                ticketInfoList.transactionFee = cursor.getString(cursor.getColumnIndex(TRANSACTION_FEE));
                ticketInfoList.transactionCommission = cursor.getString(cursor.getColumnIndex(TRANSACTION_COMMISSION));
                ticketInfoList.isOnline = cursor.getString(cursor.getColumnIndex(IS_ONLINE));
                ticketInfoList.device_time = cursor.getString(cursor.getColumnIndex(DEVICE_TIME));
                ticketInfoList.offlineRefId = cursor.getString(cursor.getColumnIndex(OFFLINE_REF_ID));
                ticketInfoList.userType = cursor.getString(cursor.getColumnIndex(USER_TYPE));
                ticketInfoList.lat = cursor.getString(cursor.getColumnIndex(LAT));
                ticketInfoList.lng = cursor.getString(cursor.getColumnIndex(lNG));

                ticketInfoList.status = cursor.getString(cursor.getColumnIndex(STATUS));
                ticketInfoList.referenceId = cursor.getString(cursor.getColumnIndex(REFERENCE_ID));
                ticketInfoList.referenceHash = cursor.getString(cursor.getColumnIndex(REFERENCEHASH));
                ticketInfoList.passenger_id = cursor.getString(cursor.getColumnIndex(PASSENGER_ID));
                ticketInfoList.helper_id = cursor.getString(cursor.getColumnIndex(HELPER_ID));
                ticketInfoList.device_id = cursor.getString(cursor.getColumnIndex(DEVICE_ID));
                ticketInfoLists.add(ticketInfoList);
                id = cursor.getInt(cursor.getColumnIndex("id"));
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putInt(UtilStrings.LAST_DATA_ID, id).apply();
        TicketInfoDataPush.pushTicketData(context, ticketInfoLists);
        /*TicketInfoDataPush.pushBusData(context, getJsonData(ticketInfoLists));*/
    }

    public JSONObject getJsonData(List<TicketInfoList> ticketInfoLists) {
        JSONObject object = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            for (int i = 0; i < ticketInfoLists.size(); i++) {
                TicketInfoList ticketInfoList = ticketInfoLists.get(i);
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("device_id", context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).getString(UtilStrings.DEVICE_ID, ""));
                jsonObject.put("helper_id", ticketInfoList.helper_id);
                jsonObject.put("ticket_id", ticketInfoList.ticket_id);
                jsonObject.put("transactionType", ticketInfoList.transactionType);
                jsonObject.put("device_time", ticketInfoList.device_time);
                jsonObject.put("transactionMedium", ticketInfoList.transactionMedium);
                jsonObject.put("transactionAmount", ticketInfoList.transactionAmount);
                jsonObject.put("transactionFee", ticketInfoList.transactionFee);
                jsonObject.put("transactionCommission", ticketInfoList.transactionCommission);
                jsonObject.put("isOnline", ticketInfoList.isOnline);
                jsonObject.put("offlineRefId", ticketInfoList.offlineRefId);
                jsonObject.put("userType", ticketInfoList.userType);
                jsonObject.put("lat", ticketInfoList.lat);

                jsonObject.put("lng", ticketInfoList.lng);
                jsonObject.put("status", ticketInfoList.status);
                jsonObject.put("referenceId", ticketInfoList.referenceId);
                jsonObject.put("referenceHash", ticketInfoList.referenceHash);
                jsonObject.put("device_id", ticketInfoList.device_id);
                jsonObject.put("helper_id", ticketInfoList.helper_id);
                jsonObject.put("passenger_id", "passenger_id");
                array.put(jsonObject);

            }
            object.put("data", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    public List<HelperList> helperLists() {
        List<HelperList> helperLists = new ArrayList<>();
        String sql = "SELECT * FROM " + HELPER_TABLE;
        Cursor c = getWritableDatabase().rawQuery(sql, null);
        try {
            while (c.moveToNext()) {
                HelperList helperList = new HelperList();
                helperList.helper_id = c.getString(c.getColumnIndex(HELPER_ID));
                helperList.helper_name = c.getString(c.getColumnIndex(HELPER_NAME));
                helperLists.add(helperList);
            }
        }finally {
            c.close();
        }

        return helperLists;
    }

    public void clearHelpers() {
        getWritableDatabase().execSQL("DELETE FROM " + HELPER_TABLE);
    }

    public void deleteFromLocal() {
        int id = context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).getInt(UtilStrings.LAST_DATA_ID, 1);
        String sql = "DELETE FROM " + TICKET_TABLE + " WHERE id<=" + id;
        Log.i("deleteFromLocal", "" + sql);
        getWritableDatabase().execSQL(sql);
        context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putBoolean(UtilStrings.DATA_SENDING, false).apply();

    }

    public void deleteFromLocalId(String ticketNo) {
        String sql = "DELETE FROM " + TICKET_TABLE + " WHERE " + TICKET_ID + " ='" + ticketNo + "'";
        Log.i("deleteFromLocal", "" + sql);
        getWritableDatabase().execSQL(sql);
        /*context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putBoolean(UtilStrings.DATA_SENDING, false).apply();*/

    }

    public void clearAllFromData() {
        String sql = "DELETE FROM " + TICKET_TABLE;
        getWritableDatabase().execSQL(sql);
    }

    public void clearBlockList() {
        String sql = "DELETE FROM " + BLOCK_LIST_TABLE_TXT;
        getWritableDatabase().execSQL(sql);
    }

    public void updatePassengerCountForward(Integer position) {
        String sql = "DELETE FROM " + PASSENGER_COUNT_TABLE  +  " WHERE " + "id" + "=" +position;
        getWritableDatabase().execSQL(sql);
    }



    public void clearAllFromPassengerTime() {
        String sql = "DELETE FROM " + PASSENGER_COUNT_TABLE;
        getWritableDatabase().execSQL(sql);
    }
    public List<PriceList> priceLists(boolean normalDiscount) {
        List<PriceList> priceLists = new ArrayList<>();

        String sql = "SELECT * FROM " + PRICE_TABLE;
        Cursor c = getWritableDatabase().rawQuery(sql, null);
        try {


            while (c.moveToNext()) {
                PriceList priceList = new PriceList();
                priceList.price_value = c.getString(c.getColumnIndex(PRICE_VALUE));
                priceList.price_discount_value = c.getString(c.getColumnIndex(PRICE_DISCOUNT_VALUE));
                priceList.price_min_distance = c.getInt(c.getColumnIndex(PRICE_MIN_DISTANCE));
                priceList.price_distance = c.getInt(c.getColumnIndex(PRICE_DISTANCE));
                if (normalDiscount) {
                    boolean matchedPrice = false;
                    for (int i = 0; i < priceLists.size(); i++) {
                        if (priceLists.get(i).price_discount_value.equals(priceList.price_discount_value)) {
                            priceLists.get(i).price_distance = priceList.price_distance;
                            matchedPrice = true;
                            break;
                        }
                    }
                    if (!matchedPrice) {
                        priceLists.add(priceList);
                    }
                } else {
                    priceLists.add(priceList);
                }


            }
        }finally {
            if (c != null)
            c.close();
        }

        return priceLists;

    }

    public String priceWrtDistance(float distance, boolean isOn) {
        String price = "";
        Cursor c = getWritableDatabase().rawQuery("SELECT * FROM " + PRICE_TABLE + " WHERE " + PRICE_MIN_DISTANCE + " <= " + distance + " AND " + PRICE_DISTANCE + " >= " + distance, null);
        while (c.moveToNext()) {
            if (isOn) {

                price = c.getString(c.getColumnIndex(PRICE_DISCOUNT_VALUE));
            } else {
                price = c.getString(c.getColumnIndex(PRICE_VALUE));
            }
        }
        c.close();
        return price;
    }

    public List<RouteStationList> routeStationLists() {
        List<RouteStationList> routeStationLists = new ArrayList<>();

        String sql = "SELECT * FROM " + ROUTE_STATION_TABLE;

        Cursor c = getWritableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            RouteStationList routeStationList = new RouteStationList();
            routeStationList.station_id = c.getString(c.getColumnIndex(STATION_ID));
            routeStationList.station_order = c.getInt(c.getColumnIndex(STATION_ORDER));
            routeStationList.station_name = c.getString(c.getColumnIndex(STATION_NAME));
            routeStationList.station_name_eng = c.getString(c.getColumnIndex(STATION_NAME_ENG));
            routeStationList.station_lat = c.getString(c.getColumnIndex(STATION_LAT));
            routeStationList.station_lng = c.getString(c.getColumnIndex(STATION_LNG));
            routeStationList.station_distance = c.getFloat(c.getColumnIndex(STATION_DISTANCE));

            routeStationLists.add(routeStationList);
//            Log.i("routeStation", routeStationList.station_name + "::" + routeStationList.station_id);
        }
        c.close();
        return routeStationLists;
    }

    public List<PassengerCountList> passengerCountLists() {
        List<PassengerCountList> passengerCountLists = new ArrayList<>();

        String sql = "SELECT * FROM " + PASSENGER_COUNT_TABLE;

        Cursor c = getWritableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            PassengerCountList passengerCountList = new PassengerCountList();
            passengerCountList.id = c.getInt(c.getColumnIndex("id"));
            passengerCountList.passenger_station_position = c.getInt(c.getColumnIndex(PASSENGER_STATION_POSITION));
            passengerCountList.passenger_direction = c.getString(c.getColumnIndex(PASSENGER_DIRECTION));


            passengerCountLists.add(passengerCountList);
        }
        c.close();
        return passengerCountLists;
    }

    public String nextStation(int stationOrder) {
        String station = "";
        String sql = "SELECT * FROM " + ROUTE_STATION_TABLE + " WHERE " + STATION_ORDER + " =" + stationOrder;
        Cursor c = getWritableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {

            station = c.getString(c.getColumnIndex(STATION_NAME));

        }
        c.close();
        return station;

    }

    public int nextStationId(String stationId) {
        int station = 0;
        String sql = "SELECT " + STATION_ORDER + " FROM " + ROUTE_STATION_TABLE + " WHERE " + STATION_ID + " ='" + stationId + "'";
        Cursor c = getWritableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {

            station = c.getInt(c.getColumnIndex(STATION_ORDER));

        }
        c.close();
        return station;

    }

    public int lastStation(String stationId) {
        int station = 0;
        String sql = "SELECT " + STATION_ORDER + " FROM " + ROUTE_STATION_TABLE + " WHERE " + STATION_ID + " ='" + stationId + "'";
        Cursor c = getWritableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {

            station = c.getInt(c.getColumnIndex(STATION_ORDER));

        }
        c.close();
        return station;

    }

    public int getDouble(String stationId) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + ROUTE_STATION_TABLE + " WHERE " + STATION_ID + " ='" + stationId + "'", null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();
        Log.i("getDouble", "" + count);
        return count;
    }

    public void clearStations() {
        String sql = "DELETE FROM " + ROUTE_STATION_TABLE;
        getWritableDatabase().execSQL(sql);

    }

    public int RouteStationRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, ROUTE_STATION_TABLE);
        return numRows;
    }

    public void insertPositionAsync(final Position position, DatabaseHandler<Void> handler) {
        new DatabaseAsyncTask<Void>(handler) {
            @Override
            protected Void executeMethod() {
                insertPosition(position);
                return null;
            }
        }.execute();
    }

    public Position selectPosition() {
        Position position = new Position();

        Cursor cursor = db.rawQuery("SELECT * FROM position ORDER BY id LIMIT 1", null);
        try {
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();

                position.setId(cursor.getLong(cursor.getColumnIndex("id")));
                position.setDeviceId(cursor.getString(cursor.getColumnIndex("deviceId")));
                position.setTime(new Date(cursor.getLong(cursor.getColumnIndex("time"))));
                position.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
                position.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
                position.setAltitude(cursor.getDouble(cursor.getColumnIndex("altitude")));
                position.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
                position.setCourse(cursor.getDouble(cursor.getColumnIndex("course")));
                position.setAccuracy(cursor.getDouble(cursor.getColumnIndex("accuracy")));
                position.setBattery(cursor.getDouble(cursor.getColumnIndex("battery")));
                position.setMock(cursor.getInt(cursor.getColumnIndex("mock")) > 0);

            } else {
                return null;
            }
        } finally {
            cursor.close();
        }

        return position;
    }

    public void selectPositionAsync(DatabaseHandler<Position> handler) {
        new DatabaseAsyncTask<Position>(handler) {
            @Override
            protected Position executeMethod() {
                return selectPosition();
            }
        }.execute();
    }

    public void deletePosition(long id) {
        if (db.delete("position", "id = ?", new String[]{String.valueOf(id)}) != 1) {
            throw new SQLException();
        }
    }

    public void deletePositionAsync(final long id, DatabaseHandler<Void> handler) {
        new DatabaseAsyncTask<Void>(handler) {
            @Override
            protected Void executeMethod() {
                deletePosition(id);
                return null;
            }
        }.execute();
    }

    public String getWriteData() {
        String sql = "SELECT * FROM " + TICKET_TABLE_TXT;
        String data = "";
        Cursor c = getWritableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            data = data + c.getString(c.getColumnIndex(TICKET_ID)) + "\t" +
                    c.getString(c.getColumnIndex(TRANSACTION_TYPE)) + "\t" +
                    c.getString(c.getColumnIndex(TRANSACTION_MEDIUM)) + "\t" +
                    c.getString(c.getColumnIndex(TRANSACTION_AMOUNT)) + "\t" +
                    c.getString(c.getColumnIndex(TRANSACTION_FEE)) + "\t" +
                    c.getString(c.getColumnIndex(TRANSACTION_COMMISSION)) + "\t" +
                    c.getString(c.getColumnIndex(IS_ONLINE)) + "\t" +
                    c.getString(c.getColumnIndex(DEVICE_TIME)) + "\t" +

                    c.getString(c.getColumnIndex(OFFLINE_REF_ID)) + "\t" +
                    c.getString(c.getColumnIndex(USER_TYPE)) + "\t" +
                    c.getString(c.getColumnIndex(LAT)) + "\t" +
                    c.getString(c.getColumnIndex(lNG)) + "\t" +
                    c.getString(c.getColumnIndex(STATUS)) + "\t" +
                    c.getString(c.getColumnIndex(REFERENCE_ID)) + "\t" +
                    c.getString(c.getColumnIndex(REFERENCEHASH)) + "\t" +
                    c.getString(c.getColumnIndex(PASSENGER_ID)) + "\t" +
                    c.getString(c.getColumnIndex(HELPER_ID)) + "\t" +
                    c.getString(c.getColumnIndex(DEVICE_ID)) + "\t" + "\n";
        }
        c.close();
        return data;

    }

    public void writeToFile() {
        String data = getWriteData();
        Log.i("Data", "Data:" + data);

        File txtFile = new File(Environment.getExternalStorageDirectory() + "/TicketData/" + GeneralUtils.getFullDate() + ".txt");

        if (!txtFile.exists()) {
            try {
                txtFile.createNewFile();
                GeneralUtils.writeInTxt(txtFile, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            GeneralUtils.writeInTxt(txtFile, data);
        }
    }

    public void clearTxtTable() {
        String sql = "DELETE FROM " + TICKET_TABLE_TXT;
        getWritableDatabase().execSQL(sql);
    }

    public ArrayList<AdvertiseList> adFilename(String stationId) {
        ArrayList<AdvertiseList> adList = new ArrayList<>();
        String sql = "SELECT * FROM " + ADVERTISEMENT_TABLE + " WHERE " + ADVERTISEMENT_STATIONS + " ='" + stationId + "' AND " + ADVERTISEMENT_COUNT + ">" + 0 + " AND " + ADVERTISEMENT_TYPE + " =" + UtilStrings.TYPE_ADV;
        Cursor c = getWritableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            AdvertiseList advertiseList = new AdvertiseList();
            File dwnloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File adFile = new File(dwnloadDir, c.getString(c.getColumnIndex(ADVERTISEMENT_FILE)));

            advertiseList.adFile = adFile;
            advertiseList.adCount = c.getInt(c.getColumnIndex(ADVERTISEMENT_COUNT));
            advertiseList.adStation = c.getString(c.getColumnIndex(ADVERTISEMENT_STATIONS));
            advertiseList.adId = c.getString(c.getColumnIndex(ADVERTISEMENT_ID));
            advertiseList.adType = c.getType(c.getColumnIndex(ADVERTISEMENT_TYPE));
            Log.i("updateSql", "" + advertiseList.adCount);
            adList.add(advertiseList);
        }
        c.close();

        return adList;
    }

    public ArrayList<AdvertiseList> noticeList() {
        ArrayList<AdvertiseList> adList = new ArrayList<>();
        String sql = "SELECT * FROM " + ADVERTISEMENT_TABLE + " WHERE " + ADVERTISEMENT_TYPE + " =" + UtilStrings.TYPE_NOTICE;
        Cursor c = getWritableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            AdvertiseList advertiseList = new AdvertiseList();
            File dwnloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            advertiseList.adFile = new File(dwnloadDir, c.getString(c.getColumnIndex(ADVERTISEMENT_FILE)));
            Log.i("updateSql", "" + advertiseList.adCount);
            adList.add(advertiseList);
        }
        c.close();

        return adList;
    }

    public int noticeAdSize() {
        ArrayList<AdvertiseList> adList = new ArrayList<>();
        String sql = "SELECT * FROM " + ADVERTISEMENT_TABLE;
        Cursor c = getWritableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            AdvertiseList advertiseList = new AdvertiseList();
            advertiseList.adType = c.getInt(c.getColumnIndex(ADVERTISEMENT_TYPE));
            Log.i("updateSql", "" + advertiseList.adCount);
            adList.add(advertiseList);
        }
        c.close();

        return adList.size();
    }

    public void updateAdCount(String adId) {
        String sql = "UPDATE " + ADVERTISEMENT_TABLE + " SET " + ADVERTISEMENT_COUNT + " =" + ADVERTISEMENT_COUNT + "-1 WHERE " + ADVERTISEMENT_ID + " ='" + adId + "'";
        getWritableDatabase().execSQL(sql);

    }

    public interface DatabaseHandler<T> {
        void onComplete(boolean success, T result);
    }

    private static abstract class DatabaseAsyncTask<T> extends AsyncTask<Void, Void, T> {

        private DatabaseHandler<T> handler;
        private RuntimeException error;


        public DatabaseAsyncTask(DatabaseHandler<T> handler) {
            this.handler = handler;
        }

        @Override
        protected T doInBackground(Void... params) {
            try {
                return executeMethod();
            } catch (RuntimeException error) {
                this.error = error;
                return null;
            }
        }

        protected abstract T executeMethod();

        @Override
        protected void onPostExecute(T result) {
            handler.onComplete(error == null, result);
        }
    }


}
