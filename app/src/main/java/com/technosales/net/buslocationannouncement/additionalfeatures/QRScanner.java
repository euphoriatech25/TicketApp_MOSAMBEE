package com.technosales.net.buslocationannouncement.additionalfeatures;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hornet.dateconverter.DateConverter;
import com.hornet.dateconverter.Model;
import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;
import com.technosales.net.buslocationannouncement.base.BaseActivity;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.mosambeesupport.Printer;
import com.technosales.net.buslocationannouncement.pojo.RouteStationList;
import com.technosales.net.buslocationannouncement.pojo.TicketInfoList;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMERID;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_AMT;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_HASH;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_TRANSACTION_NO;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.PAYMENT_CARD;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.STATUS;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TRANSACTION_TYPE_PAYMENT;

public class QRScanner extends BaseActivity {
    private static final String TAG = "QRScanner";
    int total_tickets;
    int total_collections;
    int total_collections_card;
    String latitude;
    String longitude;
    String cardNUmber = "";
    String ticketType;
    String discountType;
    String station_name;
    Float totalDistance;
    String strTotal;
    ProgressBar progressBar;
    String dateTime;
    Dialog dialog;
    int orderPos, total_passenger;
    private IntentIntegrator qrScan;
    private TokenManager tokenManager;
    private String passengerId = "";
    private TextView tv_cardNum, tv_amount, currentAmount, distance;
    private SharedPreferences preferences, helperPref;
    private String helperId, deviceId, amount, busName, toGetOff, source;
    private String nearest_name = "";
    private DatabaseHelper databaseHelper;
    private DateConverter dateConverter;
    private List<RouteStationList> routeStationLists = new ArrayList<>();
    private String isOnlineCheck;
    private String printTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);
        setUpToolbar("कार्ड बाट तिर्नुहोस", true);
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);
        qrScan.initiateScan();
        initView();
    }

    private void initView() {

        tv_cardNum = findViewById(R.id.text_card_num);
        currentAmount = findViewById(R.id.currentAmount);
        distance = findViewById(R.id.distance);


        tv_amount = findViewById(R.id.amount);
        preferences = this.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        databaseHelper = new DatabaseHelper(this);
        routeStationLists = databaseHelper.routeStationLists();


        helperPref = getSharedPreferences(UtilStrings.SHARED_PREFERENCES_HELPER, 0);
        helperId = helperPref.getString(UtilStrings.ID_HELPER, "");

        busName = preferences.getString(UtilStrings.DEVICE_NAME, "");
        progressBar = findViewById(R.id.progressBar);

        deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
        latitude = preferences.getString(UtilStrings.LATITUDE, "0.0");
        longitude = preferences.getString(UtilStrings.LONGITUDE, "0.0");
        busName = preferences.getString(UtilStrings.DEVICE_NAME, "");
        amount = getIntent().getStringExtra(UtilStrings.PRICE_VALUE);
        ticketType = getIntent().getStringExtra(UtilStrings.TICKET_TYPE);
        discountType = getIntent().getStringExtra(UtilStrings.DISCOUNT_TYPE);
        toGetOff = getIntent().getStringExtra(UtilStrings.TOGETOFF);
        nearest_name = getIntent().getStringExtra(UtilStrings.NEAREST_PLACE);
        source = getIntent().getStringExtra(UtilStrings.SOURCE);
        dateTime = GeneralUtils.getTicketDate() + GeneralUtils.getTicketTime();
        totalDistance = getIntent().getFloatExtra(UtilStrings.TOTAL_DISTANCE, 0);
        orderPos = getIntent().getIntExtra(UtilStrings.STATION_POS_PASSENGERS, 0);
        total_passenger = preferences.getInt(UtilStrings.TOTAL_PASSENGERS, 0);

//       transafering code from below
        total_tickets = preferences.getInt(UtilStrings.TOTAL_TICKETS, 0);
        total_collections = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS, 0);
        total_collections_card = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_BY_CARD, 0);
        total_tickets = total_tickets + 1;
        total_collections = total_collections + Integer.parseInt(amount);
        total_collections_card = total_collections_card + Integer.parseInt(amount);
        Log.i(TAG, "onCreate: " + toGetOff + nearest_name + station_name);

        station_name = getIntent().getStringExtra(UtilStrings.STATION_NAME);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));


    }


    private void place(String ticketId, String tranCurrentHash) {
        Log.i(TAG, "place: "+ticketId+GeneralUtils.getUnicodeReverse(amount)+helperId+deviceId+GeneralUtils.getFullDate()
                + " " + GeneralUtils.getTime()+PAYMENT_CARD+TRANSACTION_TYPE_PAYMENT+latitude+longitude+ticketType+isOnlineCheck+STATUS+passengerId+tranCurrentHash);
//
//        Log.i("TAG", "place: " + ticketId + " " + tranCurrentHash);
        ///startProcess
        if (helperId.length() != 0) {
            deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
            latitude = preferences.getString(UtilStrings.LATITUDE, "0.0");
            longitude = preferences.getString(UtilStrings.LONGITUDE, "0.0");

            String helperAmt = helperPref.getString(UtilStrings.AMOUNT_HELPER, "");
            int newHelperAmt = Integer.valueOf(helperAmt) + Integer.valueOf(GeneralUtils.getUnicodeReverse(amount));
            helperPref.edit().putString(UtilStrings.AMOUNT_HELPER, String.valueOf(newHelperAmt)).apply();

            preferences.edit().putInt(UtilStrings.TOTAL_TICKETS, total_tickets).apply();
            preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS, total_collections).apply();
            preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS_BY_CARD, total_collections_card).apply();


            dateConverter = new DateConverter();
            String dates[] = GeneralUtils.getFullDate().split("-");
            int dateYear = Integer.parseInt(dates[0]);
            int dateMonth = Integer.parseInt(dates[1]);
            int dateDay = Integer.parseInt(dates[2]);
            Model outputOfConversion = dateConverter.getNepaliDate(dateYear, dateMonth, dateDay);
            int year = outputOfConversion.getYear();
            int month = outputOfConversion.getMonth() + 1;
            int day = outputOfConversion.getDay();
//            Log.i("getNepaliDate", "year=" + year + ",month:" + month + ",day:" + day);

                 TicketInfoList ticketInfoList = new TicketInfoList();
//            ticketInfoList.ticket_id = ticketId;
//            ticketInfoList.transactionAmount = GeneralUtils.getUnicodeReverse(amount);
//            ticketInfoList.helper_id = helperId;
//            ticketInfoList.device_id = deviceId;
//            ticketInfoList.device_time = GeneralUtils.getFullDate() + " " + GeneralUtils.getTime();
//            ticketInfoList.transactionMedium = PAYMENT_CARD;
//            ticketInfoList.transactionType = TRANSACTION_TYPE_PAYMENT;
//            ticketInfoList.lat = latitude;
//            ticketInfoList.lng = longitude;
//            ticketInfoList.userType = ticketType;
//            ticketInfoList.isOnline = isOnlineCheck;
//            ticketInfoList.status = STATUS;
//            ticketInfoList.offlineRefId = "null";
//            ticketInfoList.passenger_id = passengerId;
//            ticketInfoList.referenceHash = tranCurrentHash;
//            ticketInfoList.referenceId = "0";
//            ticketInfoList.transactionFee = "0";
//            ticketInfoList.transactionCommission = "0";
//            databaseHelper.insertTicketInfo(ticketInfoList);
//
//            if (totalDistance != null) {
//                float distanceInKm = (totalDistance / 1000);
//                strTotal = distanceInKm + "";
//                if (strTotal.length() > 4) {
//                    strTotal = strTotal.substring(0, 4);
//                }
//            } else {
//                strTotal = "null";
//            }
//
//            printTransaction = "बस नम्बर :- " + busName + " (कार्ड)" + "\n" +
//                    "टिकट नम्बर :-" + GeneralUtils.getUnicodeNumber(ticketInfoList.ticket_id) + "\n" +
//                    GeneralUtils.getUnicodeNumber(strTotal) + "कि.मी \n रकम :- रु." + GeneralUtils.getUnicodeNumber(ticketInfoList.transactionAmount) + discountType + "\n" +
//                    "दूरी :-" + nearest_name + "-" + station_name + "\n" +
//                    "जारी मिति :-" + GeneralUtils.getNepaliMonth(String.valueOf(month)) + " "
//                    + GeneralUtils.getUnicodeNumber(String.valueOf(day)) + " " +
//                    GeneralUtils.getUnicodeNumber(GeneralUtils.getTime());
//
//            if (!printTransaction.equalsIgnoreCase("")) {
//                try {
//                    Printer.Print(QRScanner.this, printTransaction, null);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//                dialog.dismiss();
//                Intent intent = new Intent(this, TicketAndTracking.class);
//                startActivity(intent);
//                finish();

//            } else {
////                Log.i("TAG", "onActivate: " + "rrrr");
//            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(QRScanner.this, "सहयोगी लग ईन छैन", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(PayByCardActivity.this, TicketAndTracking.class));
                    finish();
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {

            } else {
                place("", "");

                Log.i("TAG", "onActivityResult: " + result.getContents());
                try {
                    Type listType = new TypeToken<List<TicketInfoList>>() {
                    }.getType();

                    Gson obj = new Gson();
                    obj.fromJson(result.getContents(), listType);
                    progressBar.setVisibility(View.VISIBLE);
                    Log.d("My App", obj.toJson(result.getContents()));

                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + t.getLocalizedMessage() + "\"");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
