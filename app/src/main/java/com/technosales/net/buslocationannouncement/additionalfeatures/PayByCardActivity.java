package com.technosales.net.buslocationannouncement.additionalfeatures;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.hornet.dateconverter.DateConverter;
import com.hornet.dateconverter.Model;
import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.card.mifare.M1CardHandler;
import com.morefun.yapi.card.mifare.M1KeyTypeConstrants;
import com.morefun.yapi.device.reader.icc.ICCSearchResult;
import com.morefun.yapi.device.reader.icc.IccCardType;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.SDKManager;
import com.technosales.net.buslocationannouncement.activity.CheckBalanceActivity;
import com.technosales.net.buslocationannouncement.activity.HelperLogin;
import com.technosales.net.buslocationannouncement.activity.ReIssueCard;
import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;
import com.technosales.net.buslocationannouncement.base.BaseActivity;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.mosambeesupport.BeepLEDTest;
import com.technosales.net.buslocationannouncement.mosambeesupport.M1CardHandlerMosambee;
import com.technosales.net.buslocationannouncement.mosambeesupport.Printer;
import com.technosales.net.buslocationannouncement.mosambeesupport.SearchCardOrCardReaderTest;
import com.technosales.net.buslocationannouncement.pojo.BlockList;
import com.technosales.net.buslocationannouncement.pojo.PassengerCountList;
import com.technosales.net.buslocationannouncement.pojo.RouteStationList;
import com.technosales.net.buslocationannouncement.pojo.TicketInfoList;
import com.technosales.net.buslocationannouncement.serverconn.Encrypt;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMERID;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_AMT;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_HASH;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_TRANSACTION_NO;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.FIRST_TRANSACTION_AMT;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.FIRST_TRANSACTION_HASH;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.FIRST_TRANSACTION_ID;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.KEY_A;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.KEY_B;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.PAYMENT_CARD;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECOND_TRANSACTION_AMT;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECOND_TRANSACTION_HASH;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECOND_TRANSACTION_ID;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECRET_KEY;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_CUSTOMER;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRANSATION;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.STATUS;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TOGETOFF;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TRANSACTION_TYPE_PAYMENT;


@SuppressLint("HandlerLeak")
public class PayByCardActivity extends BaseActivity {

    private static final String TAG = "PayByCardActivity";
    public List<BlockList.Datum> blockList = new ArrayList<>();
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
    int reducedValue = 0;
    String dateTime;
    boolean stopThread1, stopThread2, stopThread3, stopThread4;
    Context context;
//    ProgressDialog pClick;
    int[] customerDetailsBlock = {CUSTOMERID, CUSTOMER_AMT, CUSTOMER_HASH, CUSTOMER_TRANSACTION_NO};
    int firstTransactionStatus = 0;
    private TokenManager tokenManager;
    private String passengerId = "";
    private String passengerAmt = "";
    private String transactionHash = "";
    private String passengerTranNo = "";
    private String latestTransHash = "";
    private int TIME_DELAY = 500;
    private TextView tv_cardNum, tv_amount, currentAmount,distance;
    private SharedPreferences preferences, helperPref;
    private String helperId, deviceId, amount, busName, toGetOff, source;
    private String nearest_name = "";
    private DatabaseHelper databaseHelper;
    private DateConverter dateConverter;
    private List<RouteStationList> routeStationLists = new ArrayList<>();
    private String isOnlineCheck;
    private String printTransaction;
    private String ticketFirstId = "", ticketSecondId = "", ticketFirstAmount = "", ticketSecondAmount = "", ticketFirstHash = "", ticketSecondHash = "", latestFirstTranResponseHash;
    private String ticketId = "", tranCurrentHash = "";
    Dialog dialog;
    String newRefHash;
    String newBalance;
    int orderPos,total_passenger;
    byte[] value1 = GeneralUtils.decoderfun(SECRET_KEY);
    private Handler handlerTransaction = new Handler() {
        public void handleMessage(android.os.Message msg) {
            this.obtainMessage();
            switch (msg.what) {
                case 100:
                    msg.obj.toString();
                    cardNUmber = msg.obj.toString();
                    setCardNUm(msg.obj.toString());
                    Log.e("TAG", "handleMessage Id: " + msg.obj.toString());

                    break;
                case 101:
                    passengerId = msg.obj.toString();
                    Log.e("TAG", "handleMessage Id: " + msg.obj.toString());
                    break;

                case 102:
                    passengerAmt = msg.obj.toString();
                    Log.e("TAG", "handleMessage Id: " + msg.obj.toString());
                    break;

                case 103:

                    transactionHash = msg.obj.toString();
                    latestTransHash = msg.obj.toString();
                    Log.e("TAG", "handleMessage Id: " + msg.obj.toString());
                    break;

                case 104:
                    passengerTranNo = msg.obj.toString();
                    Log.e("TAG", "handleMessage Id: " + msg.obj.toString());
                    break;

                case 105:

                    ticketFirstId = msg.obj.toString();
                    Log.e("TAG", "handleMessage Id11: " + msg.obj.toString());

                    break;
                case 106:
                    if (msg.obj.toString() != null) {
                        ticketFirstAmount = msg.obj.toString();
                        Log.e("TAG", "handleMessage Id:11 " + msg.obj.toString());
                    } else {
                        Toast.makeText(PayByCardActivity.this, "Timeout Please restart", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 107:
                    ticketFirstHash = msg.obj.toString();
                    Log.e("TAG", "handleMessage Id11: " + msg.obj.toString());
                    break;

                case 108:
                    ticketSecondId = msg.obj.toString();
                    Log.e("TAG", "handleMessage Id: " + msg.obj.toString());
                    break;

                case 109:
                    ticketSecondAmount = msg.obj.toString();
                    Log.e("TAG", "handleMessage Id: " + msg.obj.toString());
                    break;

                case 110:
                    ticketSecondHash = msg.obj.toString();
                    Log.e("TAG", "handleMessage Id: " + msg.obj.toString());
                    break;
                case 202:
//                    Log.i(TAG, "setNewTransaction: i have reached from offline");
                    setNewTransaction(newBalance, newRefHash, ticketId, tranCurrentHash, dialog);
                    break;

                case 404:
//                    Toast.makeText(PayByCardActivity.this, "Card is not authorized", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(PayByCardActivity.this, TicketAndTracking.class));
//                    finish();
                    break;

                case 405:
                    Toast.makeText(PayByCardActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    startActivity(getIntent());
                    finish();
                    overridePendingTransition(0, 0);
                    break;


                case 500:
                    Toast.makeText(PayByCardActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
//                    pClick.dismiss();
                    dialog.dismiss();
//                    startActivity(getIntent());
                    finish();
                    overridePendingTransition(0, 0);
                    break;
                case 505:
                    Toast.makeText(PayByCardActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 200:
                        if (!ticketId.equalsIgnoreCase("") && !tranCurrentHash.equalsIgnoreCase("")) {
                            try {
                                BeepLEDTest.beepSuccess();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }


                            total_passenger=total_passenger+1;
                            preferences.edit().putInt(UtilStrings.TOTAL_PASSENGERS, total_passenger).apply();
                            PassengerCountList passengerCountList = new PassengerCountList();
                            passengerCountList.passenger_station_position=orderPos;
                            passengerCountList.passenger_direction=String.valueOf(preferences.getBoolean(UtilStrings.FORWARD, true));
                            databaseHelper.insertPassengerCountList(passengerCountList);

                            if (source != null && source.equalsIgnoreCase(UtilStrings.PLACE)) {
                                price(ticketId, tranCurrentHash);
                            } else if (source != null && source.equalsIgnoreCase(UtilStrings.PRICE)) {
                                place(ticketId, tranCurrentHash);
                            } else {
                                normal(ticketId, tranCurrentHash);
                            }
                        } else {
                        }
                    
                    break;
                default:
                    break;
            }
        }
    };


    Thread thread3 = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!Thread.interrupted() && !stopThread4) {
                if (!ticketSecondId.equalsIgnoreCase("") && !ticketSecondAmount.equalsIgnoreCase("") && !ticketSecondHash.equalsIgnoreCase("")) {
                    thread3.interrupt();
                    stopThread4 = true;
                    sendCardTransactionToServerSecond(ticketSecondId, ticketSecondAmount, dialog);
                } else {
                }
            }
        }
    });
    Thread thread2 = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!Thread.interrupted() && !stopThread3) {
                if (!ticketFirstId.equalsIgnoreCase("") && !ticketFirstAmount.equalsIgnoreCase("") && !ticketFirstHash.equalsIgnoreCase("")) {
                    thread2.interrupt();
                    stopThread3 = true;
                    sendCardTransactionToServer(ticketFirstId, ticketFirstAmount, ticketFirstHash, dialog);
                } else {

                }
            }
        }
    });

    private boolean getOfflineTransactionExecuted = false;
    Thread thread1 = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!Thread.interrupted() && !stopThread2) {
                if (!passengerId.equalsIgnoreCase("") && !passengerAmt.equalsIgnoreCase("") && !transactionHash.equalsIgnoreCase("") && !passengerTranNo.equalsIgnoreCase("")) {
                    thread1.interrupt();
                    stopThread2 = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentAmount.setText(GeneralUtils.getUnicodeNumber(passengerAmt));
                            currentAmount.setTextColor(ContextCompat.getColor(PayByCardActivity.this,R.color.greenbb));
                            tv_cardNum.setTextColor(ContextCompat.getColor(PayByCardActivity.this,R.color.greenbb));
                            tv_amount.setTextColor(ContextCompat.getColor(PayByCardActivity.this,R.color.greenbb));
                            distance.setTextColor(ContextCompat.getColor(PayByCardActivity.this,R.color.greenbb));
                        }
                    });
                    getCustomerDetails();


                } else {
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_by_card);
        setUpToolbar("कार्ड बाट तिर्नुहोस", true);
//        pClick = new ProgressDialog(this); //Your Activity.this

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
//        Log.i("isdataSending", "" + preferences.getBoolean(UtilStrings.DATA_SENDING, false));
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
        orderPos= getIntent().getIntExtra(UtilStrings.STATION_POS_PASSENGERS, 0);
        total_passenger=  preferences.getInt(UtilStrings.TOTAL_PASSENGERS, 0);

//       transafering code from below
        total_tickets = preferences.getInt(UtilStrings.TOTAL_TICKETS, 0);
        total_collections = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS, 0);
        total_collections_card = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_BY_CARD, 0);
        total_tickets = total_tickets + 1;
        total_collections = total_collections + Integer.parseInt(amount);
        total_collections_card = total_collections_card + Integer.parseInt(amount);
        Log.i(TAG, "onCreate: "+toGetOff+nearest_name+station_name);

        station_name = getIntent().getStringExtra(UtilStrings.STATION_NAME);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (amount != null) {
            tv_amount.setText(amount+"/-");
        }

        stopThread1 = false;
        stopThread2 = false;
        stopThread3 = false;
        stopThread4 = false;


        M1CardHandlerMosambee.read_miCard(handlerTransaction, customerDetailsBlock, "PayByCardActivity");
        (thread1).start();

        if (GeneralUtils.isNetworkAvailable(this)) {
            isOnlineCheck = "true";
        } else {
            isOnlineCheck = "false";
        }



        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.customer_dialog);

        if (source != null && source.equalsIgnoreCase(UtilStrings.PLACE)) {
            if(toGetOff==null){
                distance.setText( nearest_name);
            }else {
                distance.setText( nearest_name + "-" + toGetOff );
            }

        } else if (source != null && source.equalsIgnoreCase(UtilStrings.PRICE)) {
            if(station_name==null){
                distance.setText( nearest_name);
            }else {
                distance.setText( nearest_name + "-" + station_name );
            }

        } else {
            distance.setText("सामान्य भुक्तानी प्रकार");
        }
    }

    private void getCustomerDetails() {
//        Log.i(TAG, "getCustomerDetails: " + (passengerId.equalsIgnoreCase("") && passengerAmt.equalsIgnoreCase("") && transactionHash.equalsIgnoreCase("") && passengerTranNo.equalsIgnoreCase("")));
        if (!passengerId.equalsIgnoreCase("") && !passengerAmt.equalsIgnoreCase("") && !transactionHash.equalsIgnoreCase("") && !passengerTranNo.equalsIgnoreCase("")) {
            stopThread1 = true;
            stopThread2 = true;


            if (!helperId.equalsIgnoreCase(passengerId)) {
                if (Integer.parseInt(passengerAmt) > Integer.parseInt(amount) || Integer.valueOf(passengerAmt).equals(Integer.valueOf(amount))) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.show();
                        }
                    });
                    if (passengerTranNo.equalsIgnoreCase("0")) {
                        startTransactionProcess(transactionHash);
                    } else if (passengerTranNo.equalsIgnoreCase("1") || passengerTranNo.equalsIgnoreCase("2")) {
                        if (GeneralUtils.isNetworkAvailable(PayByCardActivity.this)) {
                            if (!isFinishing()) {
                                getOfflineTransaction();
                            }
                        } else {
                            startTransactionProcess(transactionHash);
                        }
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing()) {
                                currentAmount.setText(GeneralUtils.getUnicodeNumber(String.valueOf(passengerAmt)));
                                showRechargeError("तपाईंसँग पर्याप्त ब्यालेन्स छैन। कृपया रिचार्ज गर्नुहोस्।");
                            }
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            showError("सहचालक कार्ड र यात्री कार्ड भिन्न हुनुपर्दछ।");
                        }
                    }
                });
            }
        } else {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing()) {
                                showError("यो कार्ड दर्ता गरिएको छैन।");
                            }
                        }
                    });
                }
            }, 5000);

        }
    }

    private void getOfflineTransaction() {

        if (getOfflineTransactionExecuted) return;
        getOfflineTransactionExecuted = true;

        int[] firstOfflineTranBlock = {FIRST_TRANSACTION_ID, FIRST_TRANSACTION_AMT, FIRST_TRANSACTION_HASH};
        M1CardHandlerMosambee.read_miCard(handlerTransaction, firstOfflineTranBlock, "GetFirstOfflineTransaction");
        thread2.start();
    }

    private void sendCardTransactionToServer(String finalCusFirstTransId, String finalCusFirstTransAmt, String finalCusFirstTransHash, Dialog pClick) {

        if (!stopThread3) return;
        thread2.interrupt();
        stopThread3 = true;
//        Log.i(TAG, "sendCardTransactionToServer: " + "reached here " + finalCusFirstTransId + " " + finalCusFirstTransAmt + " " + finalCusFirstTransHash);
        byte[] value1 = GeneralUtils.decoderfun(SECRET_KEY);
        String amt = null;  String hash = null;
        try {
            amt = Encrypt.encrypt(value1, finalCusFirstTransAmt);
            hash=Encrypt.encrypt(value1,finalCusFirstTransHash + finalCusFirstTransId + passengerId + finalCusFirstTransAmt);

        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.i(TAG, "sendCardTransactionToServer: " + finalCusFirstTransHash + " " + transactionHash);
        if (GeneralUtils.isNetworkAvailable(this)) {
            Map<String, Object> params = new HashMap<>();
            params.put("helper_id", helperId);
            params.put("ticket_id", finalCusFirstTransId);
            params.put("transactionType", TRANSACTION_TYPE_PAYMENT);
            params.put("device_time", GeneralUtils.getDate() + "" + GeneralUtils.getTime());
            params.put("transactionAmount", amt);
            params.put("transactionMedium", PAYMENT_CARD);
            params.put("lat", latitude);
            params.put("lng", longitude);
            params.put("userType", ticketType);
            params.put("transactionFee", "0");
            params.put("transactionCommission", "0");
            params.put("isOnline", "true");
            params.put("offlineRefId", "null");
            params.put("status", STATUS);
            params.put("referenceId", "0");
            params.put("referenceHash",hash );
            params.put("passenger_id", passengerId);
            params.put("device_id", getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).getString(UtilStrings.DEVICE_ID, ""));

            RetrofitInterface post = ServerConfigNew.createServiceWithAuth(RetrofitInterface.class, tokenManager);
            Call<TraModel> call = post.transactionHistory(params);
            call.enqueue(new Callback<TraModel>() {
                @Override
                public void onResponse(Call<TraModel> call, Response<TraModel> response) {

                    if (response.code() == 200) {
                        TraModel response1 = response.body();
                        String hashFromFirstTransaction = response1.getData().getTransaction().getReferenceHash();
                        firstTransactionStatus = 200;
                        if (passengerTranNo.equalsIgnoreCase("2")) {
                            latestFirstTranResponseHash = hashFromFirstTransaction;
                            getSecondTransaction();
                        } else if (passengerTranNo.equalsIgnoreCase("1")) {

                            String newTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);

                            String[] newTranNoList = {newTranNo};
                            int[] newTranNoListBlock = {CUSTOMER_TRANSACTION_NO};
                            M1CardHandlerMosambee.write_miCard(handlerTransaction, newTranNoList, newTranNoListBlock, "OfflineTransactionNoUpdate");
                            startTransactionProcess(hashFromFirstTransaction);
                        }
                    } else if (response.code() == 400) {
                        firstTransactionStatus = 400;
                        thread2.interrupt();
                        stopThread3 = true;
                        if (passengerTranNo.equalsIgnoreCase("2")) {
                            getSecondTransaction();
                        } else if (passengerTranNo.equalsIgnoreCase("1")) {
                            String newTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);
                            String[] newTranNoList = {newTranNo};
                            int[] newTranNoListBlock = {CUSTOMER_TRANSACTION_NO};
                            M1CardHandlerMosambee.write_miCard(handlerTransaction, newTranNoList, newTranNoListBlock, "OfflineTransactionNoUpdate");
                            startTransactionProcess(transactionHash);
//                            final Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    M1CardHandlerMosambee.write_miCard(handlerTransaction, newTranNoList, newTranNoListBlock, "OfflineTransactionNoUpdate");
//                                    startTransactionProcess(latestTransHash);
//                                }
//                            }, 3000);
                        }
                    } else if (response.code() == 404) {
                        firstTransactionStatus = 404;
                        if (passengerTranNo.equalsIgnoreCase("2")) {
                            getSecondTransaction();
                        } else if (passengerTranNo.equalsIgnoreCase("1")) {
                            String newTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);
                            String[] newTranNoList = {newTranNo};
                            int[] newTranNoListBlock = {CUSTOMER_TRANSACTION_NO};
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    M1CardHandlerMosambee.write_miCard(handlerTransaction, newTranNoList, newTranNoListBlock, "OfflineTransactionNoUpdate");
                                    startTransactionProcess(latestTransHash);
                                }
                            }, 1000);
//                            M1CardHandlerMosambee.write_miCard(handlerTransaction, newTranNoList, newTranNoListBlock, "OfflineTransactionNoUpdate");
//
//                            startTransactionProcess(latestTransHash);
                        } else if (response.code() == 401) {
                            firstTransactionStatus = 401;
                            startActivity(new Intent(PayByCardActivity.this, HelperLogin.class));
                            finish();
                        }
                    }
                }
                @Override
                public void onFailure(Call<TraModel> call, Throwable t) {
                    Toast.makeText(PayByCardActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    pClick.dismiss();
                }
            });
        }
    }

    private void getSecondTransaction() {
        int[] secondOfflineTranBlock = {SECOND_TRANSACTION_ID, SECOND_TRANSACTION_AMT, SECOND_TRANSACTION_HASH};
        M1CardHandlerMosambee.read_miCard(handlerTransaction, secondOfflineTranBlock, "GetSecondOfflineTransaction");
        thread3.start();
    }

    private void sendCardTransactionToServerSecond(String ticketSecondId, String ticketSecondAmount, Dialog pClick) {

        if (!stopThread4) return;
        thread3.interrupt();
        stopThread4 = true;

        if (firstTransactionStatus == 200) {
            transactionHash = latestFirstTranResponseHash;
        } else if (firstTransactionStatus == 400) {
            transactionHash = ticketSecondHash;
        } else if (firstTransactionStatus == 404) {
            transactionHash = ticketSecondHash;
        }
//        Log.i(TAG, "sendCardTransactionToServerSecond: " + "i m in sec trans" + ticketSecondId + " " + ticketSecondAmount + " " + transactionHash);

        String newHash=null;
        byte[] value1 = GeneralUtils.decoderfun(SECRET_KEY);
        String amt = null;
        try {
            amt = Encrypt.encrypt(value1, ticketSecondAmount);
            newHash =  Encrypt.encrypt(value1,transactionHash + ticketSecondId + passengerId + ticketSecondAmount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (GeneralUtils.isNetworkAvailable(this)) {
            Map<String, Object> params = new HashMap<>();
            params.put("helper_id", helperId);
            params.put("ticket_id", ticketSecondId);
            params.put("transactionType", TRANSACTION_TYPE_PAYMENT);
            params.put("device_time", dateTime);
            params.put("transactionAmount", amt);
            params.put("transactionMedium", PAYMENT_CARD);
            params.put("lat", latitude);
            params.put("lng", longitude);
            params.put("userType", ticketType);
            params.put("transactionFee", "0");
            params.put("transactionCommission", "0");
            params.put("isOnline", "false");
            params.put("offlineRefId", "null");
            params.put("status", STATUS);
            params.put("referenceId", "0");
            params.put("referenceHash", newHash);
            params.put("passenger_id", passengerId);
            params.put("device_id", getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).getString(UtilStrings.DEVICE_ID, ""));
//            Log.i("getParams", "" + params);

            RetrofitInterface post = ServerConfigNew.createServiceWithAuth(RetrofitInterface.class, tokenManager);
            Call<TraModel> call = post.transactionHistory(params);
            call.enqueue(new Callback<TraModel>() {
                @Override
                public void onResponse(Call<TraModel> call, Response<TraModel> response) {
                    Log.e(TAG, "onResponse:" + response);

                    pClick.dismiss();
                    if (response.code() == 200) {
                        TraModel transactionResponse = response.body();
                        String newTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);
                        String[] newTranNoList = {newTranNo};
                        int[] newTranNoListBlock = {CUSTOMER_TRANSACTION_NO};
                        M1CardHandlerMosambee.write_miCard(handlerTransaction, newTranNoList, newTranNoListBlock, "OfflineTransactionNoUpdate");

                        String transactionHash = transactionResponse.getData().getTransaction().getReferenceHash();
                        startTransactionProcess(transactionHash);
                    } else if (response.code() == 400) {
                        thread3.interrupt();
                        stopThread4 = true;
                        String newTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);
                        String[] newTranNoList = {newTranNo};
                        int[] newTranNoListBlock = {CUSTOMER_TRANSACTION_NO};
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                M1CardHandlerMosambee.write_miCard(handlerTransaction, newTranNoList, newTranNoListBlock, "OfflineTransactionNoUpdate");
                                startTransactionProcess(latestTransHash);
                            }
                        }, 5000);
                    } else if (response.code() == 404) {
                        thread3.interrupt();
                        stopThread4 = true;
                        String newTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);
                        String[] newTranNoList = {newTranNo};
                        int[] newTranNoListBlock = {CUSTOMER_TRANSACTION_NO};
                        M1CardHandlerMosambee.write_miCard(handlerTransaction, newTranNoList, newTranNoListBlock, "OfflineTransactionNoUpdate");

////                        handleErrors(response.errorBody());
                        startTransactionProcess(latestTransHash);
                    } else if (response.code() == 401) {
                        startActivity(new Intent(PayByCardActivity.this, HelperLogin.class));
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<TraModel> call, Throwable t) {
                    Toast.makeText(PayByCardActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    pClick.dismiss();
                }
            });

        }
    }

    private void startTransactionProcess(String transactionHashLatest) {
        if (!validate()) {
                String valueOfTickets = "";
               valueOfTickets = String.format("%04d", total_tickets);

                ticketId = deviceId.substring(deviceId.length() - 2) + dateTime + "" + valueOfTickets;
//                tranCurrentHash = BCrypt.withDefaults().hashToString(10, (transactionHashLatest + ticketId + passengerId + GeneralUtils.getUnicodeReverse(amount)).toCharArray());

            try {
                Log.i(TAG, "startTransactionProcess:1111111111111111111111 "+transactionHashLatest + ticketId + passengerId + GeneralUtils.getUnicodeReverse(amount));
                tranCurrentHash = Encrypt.encrypt(value1,transactionHashLatest + ticketId + passengerId + GeneralUtils.getUnicodeReverse(amount));
            } catch (Exception e) {
                e.printStackTrace();
            }

            String newWritableHash = tranCurrentHash.substring(tranCurrentHash.length() - 9);
                reducedValue = Integer.valueOf(passengerAmt)-(Integer.valueOf(GeneralUtils.getUnicodeReverse(amount)));

//            writing details to blocks
                newRefHash  = Base64.encodeToString(newWritableHash.getBytes(), Base64.DEFAULT);
                newBalance  = Base64.encodeToString(String.valueOf(reducedValue).getBytes(), Base64.DEFAULT);

            if (GeneralUtils.isNetworkAvailable(PayByCardActivity.this)) {
                    if (!isFinishing()) {
                        setNewTransaction(newBalance, newRefHash, ticketId, tranCurrentHash,  dialog);
                    }
                } else {
                    if (passengerTranNo.equalsIgnoreCase("2")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorCardStorageFull();
                            }
                        });
                    } else {
                        String newRefHash1 = Base64.encodeToString(transactionHashLatest.getBytes(), Base64.DEFAULT);

                        if (passengerTranNo.equalsIgnoreCase("0")) {
                            setFirstOfflineTransaction(newBalance, newRefHash1, newRefHash, ticketId, tranCurrentHash, dialog);
                        } else if (passengerTranNo.equalsIgnoreCase("1")) {
                            secondOfflineTransaction(newBalance, newRefHash1, newRefHash, ticketId, tranCurrentHash, dialog);
                        }
                    }
                }

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PayByCardActivity.this, "Show card properly", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void secondOfflineTransaction(String newBalance, String offlineHash, String newWritableHash, String ticketId, String tranCurrentHash, Dialog pClick) {
        String newTranNo = Base64.encodeToString("2".getBytes(), Base64.DEFAULT);
        String passenserFare = Base64.encodeToString(GeneralUtils.getUnicodeReverse(amount).getBytes(), Base64.DEFAULT);

        int[] secondOfflineTranBlock = {SECOND_TRANSACTION_ID, SECOND_TRANSACTION_AMT, SECOND_TRANSACTION_HASH, CUSTOMER_TRANSACTION_NO};
        String[] secondOfflineTran = {ticketId, passenserFare, offlineHash, newTranNo};
        M1CardHandlerMosambee.write_miCard(handlerTransaction, secondOfflineTran, secondOfflineTranBlock, "SecondOfflineTransaction-Write");


    }

    private void setFirstOfflineTransaction(String newBalance, String offlineHash, String newRefHash, String ticketId, String tranCurrentHash, Dialog pClick) {
        String newTranNo = Base64.encodeToString("1".getBytes(), Base64.DEFAULT);
        String passenserFare = Base64.encodeToString(GeneralUtils.getUnicodeReverse(amount).getBytes(), Base64.DEFAULT);

        String[] firstOfflineTran = {ticketId, passenserFare, offlineHash, newTranNo};
        int[] firstOfflineTranBlock = {FIRST_TRANSACTION_ID, FIRST_TRANSACTION_AMT, FIRST_TRANSACTION_HASH, CUSTOMER_TRANSACTION_NO};
        M1CardHandlerMosambee.write_miCard(handlerTransaction, firstOfflineTran, firstOfflineTranBlock, "FirstOfflineTransaction-Write");


    }

    private void setNewTransaction(String reducedBalance, String trimmedHash, String ticketId, String tranCurrentHash, Dialog pClick) {
        String[] customerUpdatedDetails = {reducedBalance, trimmedHash};
        int[] customerUpdatedDetailsBlock = {CUSTOMER_AMT, CUSTOMER_HASH};
        M1CardHandlerMosambee.write_miCard(handlerTransaction, customerUpdatedDetails, customerUpdatedDetailsBlock, "PayByCardActivity-UpdateCard");

    }

    void showRechargeError(String s) {
        if (!isFinishing()) {
            new SweetAlertDialog(PayByCardActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error!")
                    .setContentText(s)
                    .setConfirmText("रिचार्ज गर्नुहोस्।")
                    .setCancelButton("रद्द गर्नुहोस्।", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            sweetAlertDialog.dismissWithAnimation();
//                            startActivity(new Intent(PayByCardActivity.this, TicketAndTracking.class));
                            finish();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            startActivity(new Intent(PayByCardActivity.this, CheckBalanceActivity.class));
                            finish();

                        }
                    })
                    .show();
        }
    }

    void showError(String s) {
        if (!isFinishing()) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error!")
                    .setContentText(s)
                    .hideConfirmButton()
                    .setCancelButton("close", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            sDialog.dismissWithAnimation();
//                            startActivity(new Intent(PayByCardActivity.this, TicketAndTracking.class));
                            finish();
                        }
                    })
                    .show();
        }
    }

    public void errorCardStorageFull() {
        if (!isFinishing()) {
            new SweetAlertDialog(PayByCardActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(R.string.payment_error)
                    .setContentText(getString(R.string.not_pay))
                    .hideConfirmButton()
                    .setCancelButton(getString(R.string.exit), new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            dialog.dismiss();
                            sDialog.dismissWithAnimation();
                            startActivity(new Intent(PayByCardActivity.this, TicketAndTracking.class));
                            finish();
                        }
                    }).show();
        }
    }

    Boolean validate() {
        boolean hasError = false;
        if (cardNUmber.equals("")) {

            Toast.makeText(this, "Please Assign Card", Toast.LENGTH_SHORT).show();
            hasError = true;
        }
        if (passengerId == null) {

            Toast.makeText(this, "Please Show card Properly", Toast.LENGTH_SHORT).show();
            hasError = true;
        }
        if (transactionHash.equals("")) {
            Toast.makeText(this, "Please Show card Properly", Toast.LENGTH_SHORT).show();
            hasError = true;
        }
        if (passengerTranNo == null) {
            Toast.makeText(this, "Please Show card Properly", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        if (passengerAmt == null) {
            Toast.makeText(this, "Please Show card Properly", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        return hasError;
    }

    public void setCardNUm(String s) {
        if (cardNUmber != null) {
            if (databaseHelper.listBlockList().size() != 0) {
                for (int i = 0; i < databaseHelper.listBlockList().size(); i++) {
                    if (!databaseHelper.listBlockList().get(i).identificationId.equalsIgnoreCase(cardNUmber)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_cardNum.setText(cardNUmber);
                                currentAmount.setText(GeneralUtils.getUnicodeNumber(passengerAmt));
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(PayByCardActivity.this, "यो कार्ड ब्लक गरिएको छ। ", Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(PayByCardActivity.this, TicketAndTracking.class));
                                finish();
                            }
                        });

                    }
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentAmount.setText(GeneralUtils.getUnicodeNumber(passengerAmt));
                        tv_cardNum.setText(cardNUmber);
                    }
                });

            }
        } else {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopThread1 = true;
        stopThread2 = true;
        stopThread3 = true;
        stopThread4 = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopThread1 = true;
        stopThread2 = true;
        stopThread3 = true;
        stopThread4 = true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void onBackPressed() {
        stopThread1 = true;
        stopThread2 = true;
        stopThread3 = true;
        stopThread4 = true;
        super.onBackPressed();
        startActivity(new Intent(PayByCardActivity.this, TicketAndTracking.class));
        finish();
    }

    private void normal(String ticketId, String tranCurrentHash) {

//        Log.i("TAG", "place: " + ticketId + " " + tranCurrentHash);
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
            dateConverter = new DateConverter();
            String dates[] = GeneralUtils.getFullDate().split("-");
            int dateYear = Integer.parseInt(dates[0]);
            int dateMonth = Integer.parseInt(dates[1]);
            int dateDay = Integer.parseInt(dates[2]);

            preferences.edit().putInt(UtilStrings.TOTAL_TICKETS, total_tickets).apply();
            preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS, total_collections).apply();
            preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS_BY_CARD, total_collections_card).apply();

            // updating helper balance
            String helperAmt = helperPref.getString(UtilStrings.AMOUNT_HELPER, "");
            int newHelperAmt = Integer.valueOf(helperAmt) + Integer.valueOf(GeneralUtils.getUnicodeReverse(amount));
            helperPref.edit().putString(UtilStrings.AMOUNT_HELPER, String.valueOf(newHelperAmt)).apply();


            Model outputOfConversion = dateConverter.getNepaliDate(dateYear, dateMonth, dateDay);

            int year = outputOfConversion.getYear();
            int month = outputOfConversion.getMonth() + 1;
            int day = outputOfConversion.getDay();
//            Log.i("getNepaliDate", "year=" + year + ",month:" + month + ",day:" + day);

//            Log.i("TAG", "normal: " + ticketId);
            TicketInfoList ticketInfoList = new TicketInfoList();
            ticketInfoList.ticket_id = ticketId;
            ticketInfoList.transactionAmount = GeneralUtils.getUnicodeReverse(amount);
            ticketInfoList.helper_id = helperId;
            ticketInfoList.device_id = deviceId;
            ticketInfoList.device_time = GeneralUtils.getFullDate() + " " + GeneralUtils.getTime();
            ticketInfoList.transactionMedium = PAYMENT_CARD;
            ticketInfoList.transactionType = TRANSACTION_TYPE_PAYMENT;
            ticketInfoList.lat = latitude;
            ticketInfoList.lng = longitude;
            ticketInfoList.userType = ticketType;
            ticketInfoList.isOnline = isOnlineCheck;
            ticketInfoList.offlineRefId = "null";
            ticketInfoList.status = STATUS;
            ticketInfoList.passenger_id = passengerId;
            ticketInfoList.referenceHash = tranCurrentHash;
            ticketInfoList.referenceId = "0";
            ticketInfoList.transactionFee = "0";
            ticketInfoList.transactionCommission = "0";
            databaseHelper.insertTicketInfo(ticketInfoList);

            Log.e("TAG", "onClick: " + ticketId + " " +
                    String.valueOf(Integer.parseInt(amount) + "\n" + ticketType + "\n" + GeneralUtils.getFullDate() + "\n" + GeneralUtils.getTime() + "\n" + latitude + "\n" + longitude + "\n " + helperId));
//            Log.i("TAG", "rwwwwwwwun: " + reducedValue);

            printTransaction = "बस नम्बर :- " + busName + " (कार्ड)" + "\n" +
                    "टिकट नम्बर :-" + GeneralUtils.getUnicodeNumber(ticketInfoList.ticket_id) + "\n" +
                    "रकम :- " + "रु." + GeneralUtils.getUnicodeNumber(ticketInfoList.transactionAmount) + discountType + "\n" +
                    "जारी मिति :-" + GeneralUtils.getNepaliMonth(String.valueOf(month)) + " "
                    + GeneralUtils.getUnicodeNumber(String.valueOf(day)) + " " +
                    GeneralUtils.getUnicodeNumber(GeneralUtils.getTime());

//                    busName + "\n" +
//                    GeneralUtils.getUnicodeNumber(ticketInfoList.ticket_id) + "\n" +
//                    "रु." + GeneralUtils.getUnicodeNumber(ticketInfoList.transactionAmount) + discountType + "\n" +
//                    GeneralUtils.getNepaliMonth(String.valueOf(month)) + " "
//                    + GeneralUtils.getUnicodeNumber(String.valueOf(day)) + " " +
//                    GeneralUtils.getUnicodeNumber(GeneralUtils.getTime());

            dialog.dismiss();

            if (!printTransaction.equalsIgnoreCase("")) {
                try {
                    Printer.Print(PayByCardActivity.this, printTransaction, handlerTransaction);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Intent intent=new Intent(this,TicketAndTracking.class);
                startActivity(intent);
                finish();
            } else {
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PayByCardActivity.this, "सहयोगी लग ईन छैन", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(PayByCardActivity.this, TicketAndTracking.class));
                    finish();
                }
            });
        }
    }

    private void price(String ticketId, String tranCurrentHash) {
        ///startProcess
        Log.i(TAG, "onCreate: "+toGetOff+nearest_name+station_name);

//        Log.i("TAG", "place: " + ticketId + " " + tranCurrentHash);
        if (helperId.length() != 0) {
            deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
            latitude = preferences.getString(UtilStrings.LATITUDE, "0.0");
            longitude = preferences.getString(UtilStrings.LONGITUDE, "0.0");

            preferences.edit().putInt(UtilStrings.TOTAL_TICKETS, total_tickets).apply();
            preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS, total_collections).apply();
            preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS_BY_CARD, total_collections_card).apply();

            String helperAmt = helperPref.getString(UtilStrings.AMOUNT_HELPER, "");
            int newHelperAmt = Integer.valueOf(helperAmt) + Integer.valueOf(GeneralUtils.getUnicodeReverse(amount));
            helperPref.edit().putString(UtilStrings.AMOUNT_HELPER, String.valueOf(newHelperAmt)).apply();

            dateConverter = new DateConverter();
            String dates[] = GeneralUtils.getFullDate().split("-");
            int dateYear = Integer.parseInt(dates[0]);
            int dateMonth = Integer.parseInt(dates[1]);
            int dateDay = Integer.parseInt(dates[2]);

            Model outputOfConversion = dateConverter.getNepaliDate(dateYear, dateMonth, dateDay);

            int year = outputOfConversion.getYear();
            int month = outputOfConversion.getMonth() + 1;
            int day = outputOfConversion.getDay();

            TicketInfoList ticketInfoList = new TicketInfoList();
            ticketInfoList.ticket_id = ticketId;
            ticketInfoList.transactionAmount = GeneralUtils.getUnicodeReverse(amount);
            ticketInfoList.helper_id = helperId;
            ticketInfoList.device_id = deviceId;
            ticketInfoList.device_time = GeneralUtils.getFullDate() + " " + GeneralUtils.getTime();
            ticketInfoList.transactionMedium = UtilStrings.PAYMENT_CARD;
            ticketInfoList.transactionType = TRANSACTION_TYPE_PAYMENT;
            ticketInfoList.lat = latitude;
            ticketInfoList.lng = longitude;
            ticketInfoList.userType = ticketType;
            ticketInfoList.isOnline = isOnlineCheck;
            ticketInfoList.offlineRefId = "null";
            ticketInfoList.status = STATUS;
            ticketInfoList.passenger_id = passengerId;
            ticketInfoList.referenceHash = tranCurrentHash;
            ticketInfoList.referenceId = "0";
            ticketInfoList.transactionFee = "0";
            ticketInfoList.transactionCommission = "0";
            databaseHelper.insertTicketInfo(ticketInfoList);


            printTransaction = "बस नम्बर :- " + busName + " (कार्ड)" + "\n" +
                    "टिकट नम्बर :-" + GeneralUtils.getUnicodeNumber(ticketInfoList.ticket_id) + "\n" +
                    "रकम :- " + "रु." + GeneralUtils.getUnicodeNumber(ticketInfoList.transactionAmount) + discountType + "\n" +
                    "दूरी :-" + nearest_name + "-" + toGetOff + "\n" +
                    "जारी मिति :-" + GeneralUtils.getNepaliMonth(String.valueOf(month)) + " "
                    + GeneralUtils.getUnicodeNumber(String.valueOf(day)) + " " +
                    GeneralUtils.getUnicodeNumber(GeneralUtils.getTime());

            dialog.dismiss();

            if (!printTransaction.equalsIgnoreCase("")) {
                try {
                    Printer.Print(PayByCardActivity.this, printTransaction, handlerTransaction);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                Intent intent=new Intent(this,TicketAndTracking.class);
                startActivity(intent);
                finish();
            } else {
//                Log.i("TAG", "onActivate: " + "rrrr");
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Toast.makeText(PayByCardActivity.this, "सहयोगी लग ईन छैन", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(PayByCardActivity.this, TicketAndTracking.class));
                    finish();
                }
            });

        }
    }

    private void place(String ticketId, String tranCurrentHash) {
        Log.i(TAG, "onCreate: "+toGetOff+nearest_name+station_name);

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
            ticketInfoList.ticket_id = ticketId;
            ticketInfoList.transactionAmount = GeneralUtils.getUnicodeReverse(amount);
            ticketInfoList.helper_id = helperId;
            ticketInfoList.device_id = deviceId;
            ticketInfoList.device_time = GeneralUtils.getFullDate() + " " + GeneralUtils.getTime();
            ticketInfoList.transactionMedium = PAYMENT_CARD;
            ticketInfoList.transactionType = TRANSACTION_TYPE_PAYMENT;
            ticketInfoList.lat = latitude;
            ticketInfoList.lng = longitude;
            ticketInfoList.userType = ticketType;
            ticketInfoList.isOnline = isOnlineCheck;
            ticketInfoList.status = STATUS;
            ticketInfoList.offlineRefId = "null";
            ticketInfoList.passenger_id = passengerId;
            ticketInfoList.referenceHash = tranCurrentHash;
            ticketInfoList.referenceId = "0";
            ticketInfoList.transactionFee = "0";
            ticketInfoList.transactionCommission = "0";
            databaseHelper.insertTicketInfo(ticketInfoList);

            if (totalDistance != null) {
                float distanceInKm = (totalDistance / 1000);
                strTotal = distanceInKm + "";
                if (strTotal.length() > 4) {
                    strTotal = strTotal.substring(0, 4);
                }
            } else {
                strTotal = "null";
            }

            printTransaction = "बस नम्बर :- " + busName + " (कार्ड)" + "\n" +
                    "टिकट नम्बर :-" + GeneralUtils.getUnicodeNumber(ticketInfoList.ticket_id) + "\n" +
                    GeneralUtils.getUnicodeNumber(strTotal) + "कि.मी \n रकम :- रु." + GeneralUtils.getUnicodeNumber(ticketInfoList.transactionAmount) + discountType + "\n" +
                    "दूरी :-" + nearest_name + "-" + station_name + "\n" +
                    "जारी मिति :-" + GeneralUtils.getNepaliMonth(String.valueOf(month)) + " "
                    + GeneralUtils.getUnicodeNumber(String.valueOf(day)) + " " +
                    GeneralUtils.getUnicodeNumber(GeneralUtils.getTime());

            if (!printTransaction.equalsIgnoreCase("")) {
                try {
                    Printer.Print(PayByCardActivity.this, printTransaction, handlerTransaction);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                 Intent intent=new Intent(this,TicketAndTracking.class);
                startActivity(intent);
                finish();

            } else {
//                Log.i("TAG", "onActivate: " + "rrrr");
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(PayByCardActivity.this, "सहयोगी लग ईन छैन", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(PayByCardActivity.this, TicketAndTracking.class));
                    finish();
                }
            });

        }
    }

    public interface OnSearchListener {
        void onSearchResult(int retCode, Bundle bundle);
    }

}