package com.technosales.net.buslocationannouncement.additionalfeatures;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hornet.dateconverter.DateConverter;
import com.hornet.dateconverter.Model;
import com.pax.dal.entity.EScannerType;
import com.technosales.net.buslocationannouncement.paxsupport.printer.Device;
import com.technosales.net.buslocationannouncement.paxsupport.printer.PrinterTester;
import com.technosales.net.buslocationannouncement.paxsupport.printer.ReceiptPrintParam;
import com.technosales.net.buslocationannouncement.paxsupport.scanner.ScannerPax;
import com.technosales.net.buslocationannouncement.printlib.Printer;
import com.technosales.net.buslocationannouncement.printlib.RxUtils;
import com.technosales.net.buslocationannouncement.PrintListenerImpl;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;
import com.technosales.net.buslocationannouncement.base.BaseActivity;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.pojo.RouteStationList;
import com.technosales.net.buslocationannouncement.pojo.TicketInfoList;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.util.ArrayList;
import java.util.List;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.PAYMENT_QR;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.STATUS;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TRANSACTION_TYPE_PAYMENT;


public class QrCodeScanner extends BaseActivity implements View.OnClickListener {
    TextView tv_amount, text_qr_num;
    LinearLayout withQR;
    //new
    private Button submitButton;
    String card_id = "";
    private SharedPreferences preferences,preferencesHelper;
    private String helperId, deviceId, amount, busName, toGetOff, source;
    private String nearest_name = "";
    private DatabaseHelper databaseHelper;
    private DateConverter dateConverter;
    private List<RouteStationList> routeStationLists = new ArrayList<>();
    Context context;
    String ticketType;
    String discountType;
    String  station_name;
    Float totalDistance;
    String strTotal;
    String isOnlineCheck;
    int total_tickets;
    int total_collections;
    String latitude;
    String longitude;
    int POSITION;
    String passenserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);
        setUpToolbar("कार्ड बाट तिर्नुहोस", true);

        initViews();
    }

    private void initViews() {
        text_qr_num = findViewById(R.id.ssid_code);
        withQR = findViewById(R.id.withQR);
        tv_amount = findViewById(R.id.amount);
        preferences = this.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        preferencesHelper = this.getSharedPreferences(UtilStrings.SHARED_PREFERENCES_HELPER, 0);
        databaseHelper = new DatabaseHelper(this);
        routeStationLists = databaseHelper.routeStationLists();
        submitButton = findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(this);

        helperId = preferencesHelper.getString(UtilStrings.ID_HELPER, "");
        deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
        busName = preferences.getString(UtilStrings.DEVICE_NAME, "");
        amount = getIntent().getStringExtra(UtilStrings.PRICE_VALUE);
        if (amount != null) {
            tv_amount.setText(amount);
        }
        ticketType = getIntent().getStringExtra(UtilStrings.TICKET_TYPE);
        discountType = getIntent().getStringExtra(UtilStrings.DISCOUNT_TYPE);
        toGetOff = getIntent().getStringExtra(UtilStrings.TOGETOFF);
        nearest_name = getIntent().getStringExtra(UtilStrings.NEAREST_PLACE);
        source = getIntent().getStringExtra(UtilStrings.SOURCE);
        totalDistance = getIntent().getFloatExtra(UtilStrings.TOTAL_DISTANCE,0);
        station_name = getIntent().getStringExtra(UtilStrings.STATION_NAME);
        POSITION = getIntent().getIntExtra(UtilStrings.POSITION,0);
        passenserId="1";
        ScannerPax.getInstance(EScannerType.FRONT).scan(handler, 15000);

        if (GeneralUtils.isNetworkAvailable(this)) {
            isOnlineCheck = "true";
        } else {
            isOnlineCheck = "false";
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    if(msg.obj.toString()!=null) {
                        if (databaseHelper.listBlockList().size() > 0) {
                            for (int i = 0; i < databaseHelper.listBlockList().size(); i++) {
                                if (databaseHelper.listBlockList().get(i).identificationId.equalsIgnoreCase(msg.obj.toString())) {
                                    Toast.makeText(QrCodeScanner.this, "यो कार्ड ब्लक गरिएको छ।", Toast.LENGTH_SHORT).show();
                                } else {
                                    getCardId(msg.obj.toString());
                                }
                            }
                        } else {
                            getCardId(msg.obj.toString());
                        }
                    }else {
                        Toast.makeText(QrCodeScanner.this, "Please show card properly", Toast.LENGTH_SHORT).show();
                    }

                    break;
                default:
                    break;
            }
        }

        ;
    };

    private void getCardId(String toString) {
        if (!toString.equalsIgnoreCase("Empty")) {
            card_id = toString;
            text_qr_num.setText(toString);
        } else {
            startActivity(new Intent(this, TicketAndTracking.class));
            finish();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ScannerTester.getInstance(EScannerType.FRONT).close();
    }

    Boolean validate() {
        if (card_id.equals("")) {
            Toast.makeText(this, "QR reading time expired. Please scan again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, TicketAndTracking.class));
            finish();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                if (validate()) {
                    if (source != null && source.equalsIgnoreCase(UtilStrings.PLACE)) {
                        price();
                    } else if (source != null && source.equalsIgnoreCase(UtilStrings.PRICE)) {
                        place();
                    } else {
                        normal();
                    }
                }
                break;
        }
    }


    private void normal() {

        ///startProcess
        float distance = 0;
        float nearest = 0;
        for (int i = 0; i < routeStationLists.size(); i++) {
            double startLat = Double.parseDouble(preferences.getString(UtilStrings.LATITUDE, "0.0"));
            double startLng = Double.parseDouble(preferences.getString(UtilStrings.LONGITUDE, "0.0"));
            double endLat = Double.parseDouble(routeStationLists.get(i).station_lat);
            double endLng = Double.parseDouble(routeStationLists.get(i).station_lng);
            distance = GeneralUtils.calculateDistance(startLat, startLng, endLat, endLng);
            if (i == 0) {
                nearest = distance;
            } else if (i > 0) {
                if (distance < nearest) {
                    nearest = distance;
                    nearest_name = routeStationLists.get(i).station_name;
                }

            }
        }

        if (helperId.length() != 0) {
            total_tickets = preferences.getInt(UtilStrings.TOTAL_TICKETS, 0);
            total_collections = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS, 0);
            deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
            latitude = preferences.getString(UtilStrings.LATITUDE, "0.0");
            longitude = preferences.getString(UtilStrings.LONGITUDE, "0.0");

            total_tickets = total_tickets + 1;
            total_collections = total_collections + Integer.parseInt(amount);
            preferences.edit().putInt(UtilStrings.TOTAL_TICKETS, total_tickets).apply();
            preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS, total_collections).apply();
            Log.i("nearest_name", "" + nearest_name + ":" + total_tickets + "");

            String valueOfTickets = "";
            if (total_tickets < 10) {
                valueOfTickets = "00" + String.valueOf(total_tickets);

            } else if (total_tickets > 9 && total_tickets < 100) {
                valueOfTickets = "0" + String.valueOf(total_tickets);
            } else {
                valueOfTickets = String.valueOf(total_tickets);
            }


            dateConverter = new DateConverter();
            String dates[] = GeneralUtils.getFullDate().split("-");
            int dateYear = Integer.parseInt(dates[0]);
            int dateMonth = Integer.parseInt(dates[1]);
            int dateDay = Integer.parseInt(dates[2]);


            Model outputOfConversion = dateConverter.getNepaliDate(dateYear, dateMonth, dateDay);

            int year = outputOfConversion.getYear();
            int month = outputOfConversion.getMonth() + 1;
            int day = outputOfConversion.getDay();
            Log.i("getNepaliDate", "year=" + year + ",month:" + month + ",day:" + day);

            TicketInfoList ticketInfoList = new TicketInfoList();
            ticketInfoList.ticket_id = deviceId.substring(deviceId.length() - 4) + GeneralUtils.getDate()  + GeneralUtils.getTicketTime()+ "" + valueOfTickets;
            ticketInfoList.transactionAmount = String.valueOf(Integer.parseInt(amount));
            ticketInfoList.helper_id = helperId;
            ticketInfoList.device_id = deviceId;
            ticketInfoList.device_time = GeneralUtils.getFullDate()+" "+GeneralUtils.getTime();
            ticketInfoList.transactionMedium = PAYMENT_QR;
            ticketInfoList.transactionType = TRANSACTION_TYPE_PAYMENT;
            ticketInfoList.lat =  routeStationLists.get(POSITION).station_lat;
            ticketInfoList.lng =  routeStationLists.get(POSITION).station_lng;
            ticketInfoList.userType = ticketType;
            ticketInfoList.transactionFee = "null";
            ticketInfoList.transactionCommission = "null";
            ticketInfoList.isOnline =isOnlineCheck;
            ticketInfoList.offlineRefId = "null";
            ticketInfoList.status = STATUS;
            ticketInfoList.passenger_id=passenserId;
            ticketInfoList.referenceHash = "null";
            ticketInfoList.referenceId = "null";
            databaseHelper.insertTicketInfo(ticketInfoList);

            Log.e("TAG", "onClick: " + deviceId.substring(deviceId.length() - 2) + GeneralUtils.getDate() + "" + valueOfTickets + "1\n" +
                    String.valueOf(Integer.parseInt(amount) + "\n" + ticketType + "\n" + GeneralUtils.getFullDate() + "\n" + GeneralUtils.getTime() + "\n" + latitude + "\n" + longitude + "\n " + helperId));


            String printTransaction = busName + "\n" +
                    GeneralUtils.getUnicodeNumber(ticketInfoList.ticket_id) +"क्यू आर"+ "\n" +
                    "रु." + GeneralUtils.getUnicodeNumber(ticketInfoList.transactionAmount) + discountType + "\n" +
                    GeneralUtils.getNepaliMonth(String.valueOf(month)) + " "
                    + GeneralUtils.getUnicodeNumber(String.valueOf(day)) + " " +
                    GeneralUtils.getUnicodeNumber(GeneralUtils.getTime());
//            printTransaction(printTransaction);

            String status = PrinterTester.getInstance().getStatus();
            if(status.equalsIgnoreCase("Out of paper ")){
                Toast.makeText(QrCodeScanner.this, "मुद्रण कागज समाप्त भयो।", Toast.LENGTH_SHORT).show();
            }else {
                paraPrint(printTransaction);
            }

            startActivity(new Intent(QrCodeScanner.this, TicketAndTracking.class));
            finish();
        } else {
            Toast.makeText(this, "सहयोगी लग ईन छैन", Toast.LENGTH_SHORT).show();
        }
    }

    private void price() {
        ///startProcess
        if (helperId.length() != 0) {
            total_tickets = preferences.getInt(UtilStrings.TOTAL_TICKETS, 0);
            total_collections = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS, 0);
            deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
            latitude = preferences.getString(UtilStrings.LATITUDE, "0.0");
            longitude = preferences.getString(UtilStrings.LONGITUDE, "0.0");

            total_tickets = total_tickets + 1;
            total_collections = total_collections + Integer.parseInt(amount);
            preferences.edit().putInt(UtilStrings.TOTAL_TICKETS, total_tickets).apply();
            preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS, total_collections).apply();
            Log.i("nearest_name", "" + nearest_name + ":" + total_tickets + "");

            String valueOfTickets = "";
            if (total_tickets < 10) {
                valueOfTickets = "00" + String.valueOf(total_tickets);

            } else if (total_tickets > 9 && total_tickets < 100) {
                valueOfTickets = "0" + String.valueOf(total_tickets);
            } else {
                valueOfTickets = String.valueOf(total_tickets);
            }
            dateConverter = new DateConverter();
            String dates[] = GeneralUtils.getFullDate().split("-");
            int dateYear = Integer.parseInt(dates[0]);
            int dateMonth = Integer.parseInt(dates[1]);
            int dateDay = Integer.parseInt(dates[2]);


            Model outputOfConversion = dateConverter.getNepaliDate(dateYear, dateMonth, dateDay);

            int year = outputOfConversion.getYear();
            int month = outputOfConversion.getMonth() + 1;
            int day = outputOfConversion.getDay();
            Log.i("getNepaliDate", "year=" + year + ",month:" + month + ",day:" + day);


            TicketInfoList ticketInfoList = new TicketInfoList();
            ticketInfoList.ticket_id = deviceId.substring(deviceId.length() - 4) + GeneralUtils.getDate()  + GeneralUtils.getTicketTime()+ "" + valueOfTickets;
            ticketInfoList.transactionAmount = String.valueOf(Integer.parseInt(amount));
            ticketInfoList.helper_id = helperId;
            ticketInfoList.device_id = deviceId;
            ticketInfoList.device_time = GeneralUtils.getFullDate()+" "+GeneralUtils.getTime();
            ticketInfoList.transactionMedium = PAYMENT_QR;
            ticketInfoList.transactionType = TRANSACTION_TYPE_PAYMENT;
            ticketInfoList.lat =  routeStationLists.get(POSITION).station_lat;
            ticketInfoList.lng =  routeStationLists.get(POSITION).station_lng;
            ticketInfoList.userType = ticketType;
            ticketInfoList.transactionFee = "null";
            ticketInfoList.transactionCommission = "null";
            ticketInfoList.isOnline =isOnlineCheck;
            ticketInfoList.offlineRefId = "null";
            ticketInfoList.status = STATUS;
            ticketInfoList.passenger_id=passenserId;
            ticketInfoList.referenceHash = "null";
            ticketInfoList.referenceId = "null";
            databaseHelper.insertTicketInfo(ticketInfoList);


            String printTransaction = busName + "\n" +
                    GeneralUtils.getUnicodeNumber(ticketInfoList.ticket_id)+"क्यू आर"+ "\n" +
                    "रु." + GeneralUtils.getUnicodeNumber(ticketInfoList.transactionAmount) + discountType + "\n" +
                    nearest_name + "-" + toGetOff + "\n" +
                    GeneralUtils.getNepaliMonth(String.valueOf(month)) + " "
                    + GeneralUtils.getUnicodeNumber(String.valueOf(day)) + " " +
                    GeneralUtils.getUnicodeNumber(GeneralUtils.getTime());
//            printTransaction(printTransaction);
            String status = PrinterTester.getInstance().getStatus();
            if(status.equalsIgnoreCase("Out of paper ")){
                Toast.makeText(QrCodeScanner.this, "मुद्रण कागज समाप्त भयो।", Toast.LENGTH_SHORT).show();
            }else {
                paraPrint(printTransaction);
            }
            startActivity(new Intent(this, TicketAndTracking.class));
        } else {
            Toast.makeText(this, "सहयोगी लग ईन छैन ।", Toast.LENGTH_SHORT).show();
        }
    }

    private void place() {
        ///startProcess
        if (helperId.length() != 0) {
            total_tickets = preferences.getInt(UtilStrings.TOTAL_TICKETS, 0);
            total_collections = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS, 0);
            deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
            latitude = preferences.getString(UtilStrings.LATITUDE, "0.0");
            longitude = preferences.getString(UtilStrings.LONGITUDE, "0.0");
            total_tickets = total_tickets + 1;
            total_collections = total_collections + Integer.parseInt(amount);
            preferences.edit().putInt(UtilStrings.TOTAL_TICKETS, total_tickets).apply();
            preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS, total_collections).apply();
//            ((TicketAndTracking) context).setTotal();
            String valueOfTickets = "";
            if (total_tickets < 10) {
                valueOfTickets = "00" + String.valueOf(total_tickets);

            } else if (total_tickets < 100) {
                valueOfTickets = "0" + String.valueOf(total_tickets);
            } else {
                valueOfTickets = String.valueOf(total_tickets);
            }
            dateConverter = new DateConverter();
            String dates[] = GeneralUtils.getFullDate().split("-");
            int dateYear = Integer.parseInt(dates[0]);
            int dateMonth = Integer.parseInt(dates[1]);
            int dateDay = Integer.parseInt(dates[2]);
            Model outputOfConversion = dateConverter.getNepaliDate(dateYear, dateMonth, dateDay);
            int year = outputOfConversion.getYear();
            int month = outputOfConversion.getMonth() + 1;
            int day = outputOfConversion.getDay();
            Log.i("getNepaliDate", "year=" + year + ",month:" + month + ",day:" + day);

            TicketInfoList ticketInfoList = new TicketInfoList();
            ticketInfoList.ticket_id = deviceId.substring(deviceId.length() - 4) + GeneralUtils.getDate()  + GeneralUtils.getTicketTime()+ "" + valueOfTickets;
            ticketInfoList.transactionAmount = String.valueOf(Integer.parseInt(amount));
            ticketInfoList.helper_id = helperId;
            ticketInfoList.device_id = deviceId;
            ticketInfoList.device_time = GeneralUtils.getFullDate()+" "+GeneralUtils.getTime();
            ticketInfoList.transactionMedium = UtilStrings.PAYMENT_CASH;
            ticketInfoList.transactionType = TRANSACTION_TYPE_PAYMENT;
            ticketInfoList.lat =  routeStationLists.get(POSITION).station_lat;
            ticketInfoList.lng =  routeStationLists.get(POSITION).station_lng;
            ticketInfoList.userType = ticketType;
            ticketInfoList.isOnline = isOnlineCheck;
            ticketInfoList.status = STATUS;

            ticketInfoList.transactionFee = "null";
            ticketInfoList.transactionCommission = "null";
            ticketInfoList.offlineRefId = "null";
            ticketInfoList.passenger_id=passenserId;
            ticketInfoList.referenceHash = "null";
            ticketInfoList.referenceId = "null";
            databaseHelper.insertTicketInfo(ticketInfoList);

            if (totalDistance != null) {
                float distanceInKm = (totalDistance/ 1000);
                strTotal = distanceInKm + "";
                if (strTotal.length() > 4) {
                    strTotal = strTotal.substring(0, 4);
                }
            } else {
                strTotal = "null";
            }
            String printTransaction = busName + "\n" +
                    GeneralUtils.getUnicodeNumber(ticketInfoList.ticket_id)+"क्यू आर"+ "\n" +
                    GeneralUtils.getUnicodeNumber(strTotal) + "कि.मी , रु." + GeneralUtils.getUnicodeNumber(ticketInfoList.transactionAmount) + discountType + "\n" +
                    nearest_name + "-" + station_name + "\n" +
                    GeneralUtils.getNepaliMonth(String.valueOf(month)) + " "
                    + GeneralUtils.getUnicodeNumber(String.valueOf(day)) + " " +
                    GeneralUtils.getUnicodeNumber(GeneralUtils.getTime());

            String status = PrinterTester.getInstance().getStatus();

            if(status.equalsIgnoreCase("Out of paper ")){
                Toast.makeText(QrCodeScanner.this, "मुद्रण कागज समाप्त भयो।", Toast.LENGTH_SHORT).show();
            }else {
                paraPrint(printTransaction);
            }
            startActivity(new Intent(this, TicketAndTracking.class));
            finish();
        } else {
//            ((TicketAndTracking) context).helperName.setText("सहायक छान्नुहोस् ।");
            startActivity(new Intent(this, TicketAndTracking.class));
            finish();
            Toast.makeText(this, "सहयोगी लग ईन छैन ।", Toast.LENGTH_SHORT).show();
        }
    }


    private void paraPrint(String printData) {
        RxUtils.runInBackgroud(new Runnable() {
            @Override
            public void run() {
                ReceiptPrintParam receiptPrintParam = new ReceiptPrintParam();
                String printType = "error";
                if (GeneralUtils.needBtPrint()) {
                    Printer.printA60Receipt("", "", printType);
                } else {
                    receiptPrintParam.print(printData, new PrintListenerImpl(QrCodeScanner.this));
                    Device.beepOk();
                }
            }
        });
    }


}


















