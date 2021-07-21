package com.technosales.net.buslocationannouncement.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hornet.dateconverter.DateConverter;
import com.hornet.dateconverter.Model;
import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.mosambeesupport.BeepLEDTest;
import com.technosales.net.buslocationannouncement.mosambeesupport.M1CardHandlerMosambee;
import com.technosales.net.buslocationannouncement.mosambeesupport.Printer;
import com.technosales.net.buslocationannouncement.pojo.ApiError;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.pojo.TraModel;
import com.technosales.net.buslocationannouncement.serverconn.Encrypt;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;
import com.technosales.net.buslocationannouncement.base.BaseActivity;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.pojo.CheckBalanceModel;
import com.technosales.net.buslocationannouncement.pojo.Passenger;
import com.technosales.net.buslocationannouncement.pojo.Recharge;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
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
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.NULL;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.PAYMENT_CARD;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.PAYMENT_CASH;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECOND_TRANSACTION_AMT;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECOND_TRANSACTION_HASH;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECOND_TRANSACTION_ID;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECRET_KEY;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.STATUS;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TRANSACTION_TYPE_LOAD;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TRANSACTION_TYPE_PAYMENT;


public class CheckBalanceActivity extends  BaseActivity {
    int minAmt = 25, maxAmt = 500;
    int total_tickets;
    int total_collections_card;
    String passengerId = "", currentAmount = "", transactionHash = "", transactionNo = "";
    String latitude;
    String longitude;
    TokenManager tokenManager;
    int[] customerDetailsRead = {CUSTOMERID, CUSTOMER_HASH, CUSTOMER_TRANSACTION_NO};
    int[] customerUpdatedDetailsBlock = {CUSTOMER_AMT, CUSTOMER_HASH};

    SweetAlertDialog pDialog1;
    ImageView scanCard;
    LinearLayout scanCardLayout;
    boolean stopThread3, stopThread4;
    Dialog dialog;
    private TextView tv_message;
    private TextView tv_amount;
    private Button btn_recharge;
    private String cardNum = "";
    private String deviceID = "";
    private SharedPreferences preferences, preferencesHelper;
    private String helperString = "";
    private String isOnlineCheck;
    private int TIME_DELAY = 500;
    private DatabaseHelper databaseHelper;
    private boolean stopThread, stopThread1;
    private String rechargeBill;
    private String newHash = "";
    private int successStatus = 0;
    private RecyclerView priceRecycler;
    private GridLayoutManager gridLayoutManager;
    private RechargeAdapter rechargeAdapter;
    private ProgressDialog pDialogFinal;
    private String newBlnc = "";
    private String dateTime = "", ticketType = "";
    private int firstTransactionStatus = 0;
    private String cardNo;
    private String ticketFirstId = "", ticketSecondId = "", ticketFirstAmount = "", ticketSecondAmount = "", ticketFirstHash = "", ticketSecondHash = "", latestFirstTranResponseHash;
    private boolean getOfflineTransactionExecuted = false;
    private Handler rechargeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 100:
                    if (!msg.obj.toString().equalsIgnoreCase(":") && msg.obj.toString() != null) {
//                        getUserInfo(msg.obj.toString());
                        cardNo = msg.obj.toString();
                        stopThread = true;
                    } else {
                        Toast.makeText(CheckBalanceActivity.this, "Please show card properly", Toast.LENGTH_SHORT).show();
                    }
                case 101:
                    if (msg.obj.toString() != null) {
                        Log.e("TAG", "handleMessage: " + msg.obj.toString());
                        passengerId = msg.obj.toString();
                        stopThread = true;
                    } else {
                        Toast.makeText(CheckBalanceActivity.this, "Timeout Please restart", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 102:
                    Log.e("TAG", "handleMessage: " + msg.obj.toString());
                    transactionHash = msg.obj.toString();
                    stopThread = true;

                    break;

                case 103:
                    Log.e("TAG", "handleMessage: " + msg.obj.toString());
                    transactionNo = msg.obj.toString();
                    stopThread = true;

                    break;


                case 104:
                    transactionNo = msg.obj.toString();
                    if (GeneralUtils.isNetworkAvailable(CheckBalanceActivity.this)) {
                        if (!passengerId.equalsIgnoreCase("") && !transactionHash.equalsIgnoreCase("") && !transactionNo.equalsIgnoreCase("")) {
                            if (transactionNo.equalsIgnoreCase("2") || transactionNo.equalsIgnoreCase("1")) {
                                getOfflineTransaction();
                            } else {
                                getUserInfo(cardNo);
                                transactionNo = "0";
                            }
                        } else {
                            Toast.makeText(CheckBalanceActivity.this, "Card Not Read Properly...", Toast.LENGTH_SHORT).show();
                        }
                        Log.e("TAG", "handleMessage Id: " + msg.obj.toString());
                    } else {
                        showNoInternet();
                    }
                    break;
                case 105:

                    ticketFirstId = msg.obj.toString();
                    Log.e("TAG", "handleMessage Id11: " + msg.obj.toString());

                    break;
                case 106:
                    ticketFirstAmount = msg.obj.toString();
                    Log.e("TAG", "handleMessage Id:11 " + msg.obj.toString());
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

                case 200:
                    Log.i("TAG", "handleMessage: " + msg.obj.toString());
                    if (!rechargeBill.equalsIgnoreCase("")) {

                        pDialogFinal.dismiss();
                        try {
                            BeepLEDTest.beepSuccess();
                            Printer.Print(CheckBalanceActivity.this, rechargeBill,rechargeHandler);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                    }
                    Toast.makeText(CheckBalanceActivity.this, "Recharged Successfully!!!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CheckBalanceActivity.this, TicketAndTracking.class));
                    finish();
                    break;
                case 404:
                    recreate();
                    Toast.makeText(CheckBalanceActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();

                    if (newBlnc.length() > 0 && newHash.length() > 0) {
                        pDialog1 = new SweetAlertDialog(CheckBalanceActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        pDialog1.setTitleText("तपाईंको शेष रकम अपडेट गरिएको छैन।!")
                                .setContentText("फेरि प्रयास गर्नुहोस।")
                                .setConfirmText("अपडेट")
                                .setCancelable(false);
                        pDialog1.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                String customerAmt = Base64.encodeToString(newBlnc.getBytes(), Base64.DEFAULT);
                                String customerHash = Base64.encodeToString(newHash.getBytes(), Base64.DEFAULT);
                                String[] customerUpdatedDetails = {customerAmt, customerHash};
                                M1CardHandlerMosambee.write_miCard(rechargeHandler, customerUpdatedDetails, customerUpdatedDetailsBlock, "CheckBalanceActivity-UpdateCard");
                            }
                        }).show();
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

    private void handleError(ResponseBody errorBody, Context context) {
        ApiError apiErrors = GeneralUtils.convertErrors(errorBody);

        if (errorBody != null) {
            for (Map.Entry<String, List<String>> error : apiErrors.getErrors().entrySet()) {
                if (error.getKey().equals("message")) {
                    Toast.makeText(context, error.getValue().get(0), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CheckBalanceActivity.this, TicketAndTracking.class);
                    startActivity(intent);
                    finish();

                }


            }
        } else {
            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_balance);
        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        preferencesHelper = getSharedPreferences(UtilStrings.SHARED_PREFERENCES_HELPER, 0);

        latitude = preferences.getString(UtilStrings.LATITUDE, "0.0");
        longitude = preferences.getString(UtilStrings.LONGITUDE, "0.0");
        deviceID = preferences.getString(UtilStrings.DEVICE_ID, "");
        helperString = preferencesHelper.getString(UtilStrings.ID_HELPER, "");

        databaseHelper = new DatabaseHelper(this);
        setUpToolbar("ब्यालेन्स जाँच", true);
        setupUI();
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        stopThread = false;
        scanCardLayout = findViewById(R.id.scanCardLayout);
        scanCard = findViewById(R.id.scanCard);
        Glide.with(this).asGif().load(R.drawable.helper).into(scanCard);
        pDialogFinal = new ProgressDialog(CheckBalanceActivity.this);
        M1CardHandlerMosambee.read_miCard(rechargeHandler, customerDetailsRead, "CheckBalanceActivity");

        dateTime = GeneralUtils.getTicketDate() + GeneralUtils.getTicketTime();
        ticketType = "discount";
        if (GeneralUtils.isNetworkAvailable(this)) {
            isOnlineCheck = "true";
        } else {
            isOnlineCheck = "false";
        }
    }

    void setupUI() {
        tv_message = findViewById(R.id.tv_message);
        tv_amount = findViewById(R.id.tv_amount);
        priceRecycler = findViewById(R.id.priceRecycler);


//        price list
        List<Integer> priceList = new ArrayList<>();
        priceList.add(25);
        priceList.add(30);
        priceList.add(40);
        priceList.add(50);
        priceList.add(100);
        priceList.add(200);
        priceList.add(300);
        priceList.add(400);
        priceList.add(500);
        rechargeAdapter = new RechargeAdapter(priceList, this);
        gridLayoutManager = new GridLayoutManager(this, 3);
        priceRecycler.setLayoutManager(gridLayoutManager);
        priceRecycler.setHasFixedSize(true);
        priceRecycler.setAdapter(rechargeAdapter);
        tv_amount.setText("");


        btn_recharge = findViewById(R.id.btn_recharge);
        btn_recharge.setVisibility(View.INVISIBLE);
        btn_recharge.setClickable(false);


        btn_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputAmountDialog();
            }
        });
    }

    void rechargeProcess(int amount) {

        total_tickets = preferences.getInt(UtilStrings.TOTAL_TICKETS, 0);
        total_collections_card = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_CARD, 0);

        total_tickets = total_tickets + 1;
        total_collections_card = total_collections_card + amount;

        String valueOfTickets = "";

        valueOfTickets = String.format("%04d", total_tickets);
        byte[] value1 = GeneralUtils.decoderfun(SECRET_KEY);
        String dateTime = GeneralUtils.getTicketDate() + GeneralUtils.getTicketTime();
        String amt = null;
        try {
            amt = Encrypt.encrypt(value1, String.valueOf(amount));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (passengerId.length() > 0 && transactionHash.length() > 0) {
            if (!helperString.equalsIgnoreCase("")) {
                if (!helperString.equalsIgnoreCase(passengerId)) {
                    Log.i("TAG", "rechargeProcess: " + passengerId + " " + transactionHash);
                    if (GeneralUtils.isNetworkAvailable(this)) {
                        String ticketId = deviceID.substring(deviceID.length() - 2) + dateTime + "" + valueOfTickets;
                        String referenceHash = null;
                        try {
                            transactionHash = transactionHash.replace("\n", "");
                            referenceHash = Encrypt.encrypt(value1, transactionHash + ticketId + passengerId + amount);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        newHash = referenceHash.substring(referenceHash.length() - 9);
                        Map<String, Object> params = new HashMap<>();
                        params.put("helper_id", helperString);
                        params.put("ticket_id", ticketId);
                        params.put("transactionType", TRANSACTION_TYPE_LOAD);
                        params.put("device_time", GeneralUtils.getDate() + "" + GeneralUtils.getTime());
                        params.put("transactionAmount", amt);
                        params.put("transactionMedium", PAYMENT_CASH);
                        params.put("lat", latitude);
                        params.put("lng", longitude);
                        params.put("userType", "recharge");
                        params.put("transactionFee", NULL);
                        params.put("transactionCommission", NULL);
                        params.put("isOnline", true);
                        params.put("offlineRefId", NULL);
                        params.put("status", STATUS);
                        params.put("referenceId", NULL);
                        params.put("referenceHash", referenceHash);
                        params.put("passenger_id", passengerId);
                        params.put("device_id", getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).getString(UtilStrings.DEVICE_ID, ""));
                        Log.i("getParams123", "" + params);


                        ProgressDialog pDialog = new ProgressDialog(this); //Your Activity.this
                        pDialog.setMessage("Requesting");
                        pDialog.setCancelable(false);
                        pDialog.show();


                        RetrofitInterface post = ServerConfigNew.createServiceWithAuth(RetrofitInterface.class, tokenManager);
                        Call<Recharge> call = post.recharge(params);
                        call.enqueue(new Callback<Recharge>() {
                            @Override
                            public void onResponse(Call<Recharge> call, Response<Recharge> response) {
                                Recharge recharge = response.body();
                                pDialog.dismiss();
                                stopThread = true;
                                if (response.isSuccessful()) {

                                    preferencesHelper.edit().putString(UtilStrings.AMOUNT_HELPER, String.valueOf(recharge.getData().getHelperAmount())).apply();

                                    preferences.edit().putInt(UtilStrings.TOTAL_TICKETS, total_tickets).apply();
                                    preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS_CARD, total_collections_card).apply();

                                    printDetails(recharge.getData().getPassengerAmount(), amount);
                                    tv_amount.setText("रू " + GeneralUtils.getUnicodeNumber(String.valueOf(recharge.getData().getPassengerAmount())));

                                    pDialogFinal.setMessage("रिचार्ज मान्यताको लागि कृपया कार्ड देखाउनुहोस्।");
                                    pDialogFinal.setCancelable(false);
                                    pDialogFinal.show();
                                    newBlnc = String.valueOf(recharge.getData().getPassengerAmount());
                                    Log.i("TAG", "rechargeCard: " + recharge.getData().getPassengerAmount());
                                    String customerAmt = Base64.encodeToString(String.valueOf(recharge.getData().getPassengerAmount()).getBytes(), Base64.DEFAULT);
                                    String customerHash = Base64.encodeToString(newHash.getBytes(), Base64.DEFAULT);
                                    String[] customerUpdatedDetails = {customerAmt, customerHash};
                                    M1CardHandlerMosambee.write_miCard(rechargeHandler, customerUpdatedDetails, customerUpdatedDetailsBlock,  "CheckBalanceActivity-UpdateCard");
//                                        }
//                                    }).show();

                                } else if (response.code() == 404) {
                                    handleErrors(response.errorBody());
                                } else if (response.code() == 401) {
                                    startActivity(new Intent(CheckBalanceActivity.this, HelperLogin.class));
                                    finish();
                                } else if (response.code() == 500) {
                                    Toast.makeText(CheckBalanceActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(CheckBalanceActivity.this, TicketAndTracking.class));
                                    finish();

                                } else {
                                    Toast.makeText(CheckBalanceActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<Recharge> call, Throwable t) {
                                Toast.makeText(CheckBalanceActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Helper cannot recharge its own account", Toast.LENGTH_SHORT).show();
                }
            } else {
                showRechargeError("सहयोगी लग इन छैन। कृपया पहिले लग ईन गर्नुहोस्।");
            }
        } else {
            showRechargeError("कार्ड राम्रोसँग देखाइएको छैन। कृपया फेरि सुरू गर्नुहोस्।");
        }
    }

    void showRechargeError(String s) {
        if (!isFinishing()) {
            new SweetAlertDialog(CheckBalanceActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error!")
                    .setContentText(s)
                    .setConfirmText("रलग इन गर्नुहोस्।")
                    .setCancelButton("रद्द गर्नुहोस्।", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            sweetAlertDialog.dismissWithAnimation();
//                            startActivity(new Intent(CheckBalanceActivity.this, TicketAndTracking.class));
                            finish();
                        }
                    }).show();
        }
    }

    private void handleErrors(ResponseBody responseBody) {
        ApiError apiErrors = GeneralUtils.convertErrors(responseBody);
        if (responseBody != null) {
            for (Map.Entry<String, List<String>> error : apiErrors.getErrors().entrySet()) {
                if (error.getKey().equals("message")) {
                    Toast.makeText(this, error.getValue().get(0), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, error.getValue().get(0), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    private void printDetails(int newAmount, int amount) {
        DateConverter dateConverter;
        if (newAmount != 0 && amount != 0) {
            String passenger_name = preferences.getString(UtilStrings.PASSENGER_NAME, "");
            String passenger_pre_amount = preferences.getString(UtilStrings.PASSENGER_OLD_BLNC, "");
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

            rechargeBill = "यात्रीको नाम :-" + passenger_name + "\n" + "पहिला भएको रकम रु. " + GeneralUtils.getUnicodeNumber(passenger_pre_amount) + "\n"
                    + "थपिएको रकम  रु. " + GeneralUtils.getUnicodeNumber(String.valueOf(amount)) + "\n" + "अब बाँकी रकम रु. " + GeneralUtils.getUnicodeNumber(String.valueOf(newAmount)) + " " + "\n\n" +
                    GeneralUtils.getNepaliMonth(String.valueOf(month)) + " "
                    + GeneralUtils.getUnicodeNumber(String.valueOf(day)) + " " +
                    GeneralUtils.getUnicodeNumber(GeneralUtils.getTime()) + "\n";
        }
    }

    void showNoInternet() {

        try {
            BeepLEDTest.beepError();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        new SweetAlertDialog(CheckBalanceActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error!")
                .setContentText("No internet")
                .setConfirmText("close")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        startActivity(new Intent(CheckBalanceActivity.this, TicketAndTracking.class));
                        finish();
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    void inputAmountDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recharge");
        builder.setPositiveButton("OK", null);


        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText amountInput = new EditText(this);
        amountInput.setHint("रकम");
        amountInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        amountInput.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(3)
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 10, 0, 0);
        amountInput.setLayoutParams(params);
        amountInput.setTextSize(40);
        amountInput.setHeight(150);

        layout.addView(amountInput);

        builder.setView(layout);

        final AlertDialog mAlertDialog = builder.create();

        mAlertDialog.show();
        mAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new Message());
        mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = amountInput.getText().toString().trim();
                if (text.length() > 0) {
                    int amt = Integer.parseInt(text);
                    if (amt >= minAmt && amt <= maxAmt) {
                        if (tv_amount.getText().toString().length() > 0) {
                            if (transactionNo.equalsIgnoreCase("1") || transactionNo.equalsIgnoreCase("2")) {
                                if (GeneralUtils.isNetworkAvailable(CheckBalanceActivity.this)) {
                                    if (!isFinishing()) {
                                        getOfflineTransaction();
                                    }
                                }
                            } else {
                                rechargeProcess(amt);
                                mAlertDialog.dismiss();
                            }

                        }
                    } else
                        Toast.makeText(CheckBalanceActivity.this, "Amount should be between " + minAmt + " - " + maxAmt, Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(CheckBalanceActivity.this, "Please enter amount", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendCardTransactionToServerSecond(String ticketSecondId, String ticketSecondAmount, Dialog pClick) {
        String dateTime = GeneralUtils.getTicketDate() + GeneralUtils.getTicketTime();
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

        String newHash = null;
        byte[] value1 = GeneralUtils.decoderfun(SECRET_KEY);
        String amt = null;
        try {
            amt = Encrypt.encrypt(value1, ticketSecondAmount);
            transactionHash = transactionHash.replace("\n", "");
            newHash = Encrypt.encrypt(value1, transactionHash + ticketSecondId + passengerId + ticketSecondAmount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (GeneralUtils.isNetworkAvailable(this)) {
            Map<String, Object> params = new HashMap<>();
            params.put("helper_id", helperString);
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

                    if (response.isSuccessful()) {
                        TraModel transactionResponse = response.body();
                        String newTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);
                        String[] newTranNoList = {newTranNo};
                        int[] newTranNoListBlock = {CUSTOMER_TRANSACTION_NO};
                        M1CardHandlerMosambee.write_miCard(rechargeHandler, newTranNoList, newTranNoListBlock, "OfflineTransactionNoUpdate");
                        String transactionHash = transactionResponse.getData().getTransaction().getReferenceHash();
                        rechargeHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getUserInfo(cardNo);
                                transactionNo = "0";
                            }
                        }, 1000);

                    } else if (response.code() == 400) {
                        thread3.interrupt();
                        stopThread4 = true;
                        String newTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);
                        String[] newTranNoList = {newTranNo};
                        int[] newTranNoListBlock = {CUSTOMER_TRANSACTION_NO};

                        M1CardHandlerMosambee.write_miCard(rechargeHandler, newTranNoList, newTranNoListBlock,  "OfflineTransactionNoUpdate");

                        rechargeHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getUserInfo(cardNo);
                                transactionNo = "0";
                            }
                        }, 1000);
                    } else if (response.code() == 404) {
                        handleError(response.errorBody(),CheckBalanceActivity.this);
                        thread3.interrupt();
                        stopThread4 = true;
                        String newTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);
                        String[] newTranNoList = {newTranNo};
                        int[] newTranNoListBlock = {CUSTOMER_TRANSACTION_NO};
                        M1CardHandlerMosambee.write_miCard(rechargeHandler, newTranNoList, newTranNoListBlock,"OfflineTransactionNoUpdate");

                        rechargeHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getUserInfo(cardNo);
                                transactionNo = "0";
                            }
                        }, 1000);
                    } else if (response.code() == 401) {
                        startActivity(new Intent(CheckBalanceActivity.this, HelperLogin.class));
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<TraModel> call, Throwable t) {
                    Toast.makeText(CheckBalanceActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                    pClick.dismiss();
                }
            });

        }
    }

    private void getSecondTransaction() {
        int[] secondOfflineTranBlock = {SECOND_TRANSACTION_ID, SECOND_TRANSACTION_AMT, SECOND_TRANSACTION_HASH};
        M1CardHandlerMosambee.read_miCard(rechargeHandler, secondOfflineTranBlock, "GetSecondOfflineTransaction");
        thread3.start();
    }

    private void sendCardTransactionToServer(String finalCusFirstTransId, String finalCusFirstTransAmt, String finalCusFirstTransHash, Dialog pClick) {

        if (!stopThread3) return;
        thread2.interrupt();
        stopThread3 = true;
//        Log.i(TAG, "sendCardTransactionToServer: " + "reached here " + finalCusFirstTransId + " " + finalCusFirstTransAmt + " " + finalCusFirstTransHash);
        byte[] value1 = GeneralUtils.decoderfun(SECRET_KEY);
        String amt = null;
        String hash = null;
        try {
            amt = Encrypt.encrypt(value1, finalCusFirstTransAmt);
            finalCusFirstTransHash = finalCusFirstTransHash.replace("\n", "");
            hash = Encrypt.encrypt(value1, finalCusFirstTransHash + finalCusFirstTransId + passengerId + finalCusFirstTransAmt);

        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.i(TAG, "sendCardTransactionToServer: " + finalCusFirstTransHash + " " + transactionHash);
        if (GeneralUtils.isNetworkAvailable(this)) {
            Map<String, Object> params = new HashMap<>();
            params.put("helper_id", helperString);
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
            params.put("referenceHash", hash);
            params.put("passenger_id", passengerId);
            params.put("device_id", getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).getString(UtilStrings.DEVICE_ID, ""));

            RetrofitInterface post = ServerConfigNew.createServiceWithAuth(RetrofitInterface.class, tokenManager);
            Call<TraModel> call = post.transactionHistory(params);
            call.enqueue(new Callback<TraModel>() {
                @Override
                public void onResponse(Call<TraModel> call, Response<TraModel> response) {

                    if (response.isSuccessful()) {
                        TraModel response1 = response.body();
                        String hashFromFirstTransaction = response1.getData().getTransaction().getReferenceHash();
                        firstTransactionStatus = 200;
                        if (transactionNo.equalsIgnoreCase("2")) {
                            latestFirstTranResponseHash = hashFromFirstTransaction;
                            getSecondTransaction();
                        } else if (transactionNo.equalsIgnoreCase("1")) {
                            String newTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);
                            String[] newTranNoList = {newTranNo};
                            int[] newTranNoListBlock = {CUSTOMER_TRANSACTION_NO};
                            M1CardHandlerMosambee.write_miCard(rechargeHandler, newTranNoList, newTranNoListBlock, "OfflineTransactionNoUpdate");
                            rechargeHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getUserInfo(cardNo);
                                    transactionNo = "0";
                                }
                            }, 500);
                        }
                    } else if (response.code() == 400) {

                        firstTransactionStatus = 400;
                        thread2.interrupt();
                        stopThread3 = true;
                        if (transactionNo.equalsIgnoreCase("2")) {
                            getSecondTransaction();
                        } else if (transactionNo.equalsIgnoreCase("1")) {

                            String newTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);
                            String[] newTranNoList = {newTranNo};
                            int[] newTranNoListBlock = {CUSTOMER_TRANSACTION_NO};
                            M1CardHandlerMosambee.write_miCard(rechargeHandler, newTranNoList, newTranNoListBlock,"OfflineTransactionNoUpdate");
                            rechargeHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getUserInfo(cardNo);
                                    transactionNo = "0";
                                }
                            }, 500);

                        }
                    } else if (response.code() == 404) {
                        handleError(response.errorBody(),CheckBalanceActivity.this);

                        firstTransactionStatus = 404;
                        if (transactionNo.equalsIgnoreCase("2")) {
                            getSecondTransaction();
                        } else if (transactionNo.equalsIgnoreCase("1")) {
                            String newTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);
                            String[] newTranNoList = {newTranNo};
                            int[] newTranNoListBlock = {CUSTOMER_TRANSACTION_NO};
                            M1CardHandlerMosambee.write_miCard(rechargeHandler, newTranNoList, newTranNoListBlock, "OfflineTransactionNoUpdate");

                            rechargeHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getUserInfo(cardNo);
                                    transactionNo = "0";
                                }
                            }, 500);
                        }
                    }

                }

                @Override
                public void onFailure(Call<TraModel> call, Throwable t) {
                    Toast.makeText(CheckBalanceActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "onFailure: " + t.getLocalizedMessage());
//                    pClick.dismiss();
                }
            });
        }
    }

    private void getOfflineTransaction() {
        if (getOfflineTransactionExecuted) return;
        getOfflineTransactionExecuted = true;

        int[] firstOfflineTranBlock = {FIRST_TRANSACTION_ID, FIRST_TRANSACTION_AMT, FIRST_TRANSACTION_HASH};
        M1CardHandlerMosambee.read_miCard(rechargeHandler, firstOfflineTranBlock,  "GetFirstOfflineTransaction");
        thread2.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void getUserInfo(String toString) {
        if (databaseHelper.listBlockList().size() > 0) {
            for (int i = 0; i < databaseHelper.listBlockList().size(); i++) {
                if (databaseHelper.listBlockList().get(i).identificationId.equalsIgnoreCase(toString)) {
                    Toast.makeText(CheckBalanceActivity.this, "यो कार्ड ब्लक गरिएको छ।", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CheckBalanceActivity.this, TicketAndTracking.class));
                    finish();
                } else {
                    if (GeneralUtils.isNetworkAvailable(CheckBalanceActivity.this)) {
                        cardNum = toString;
                        tv_amount.setText("");
                        btn_recharge.setVisibility(View.VISIBLE);
                        btn_recharge.setClickable(true);
                        tv_message.setText("पर्खनुहोस...");
                        checkCustomerBalance(cardNum, tv_message, tv_amount);
                    } else {
                        if (!isFinishing()) {
                            showNoInternet();
                        }
                    }
                }
            }
        } else {
            if (GeneralUtils.isNetworkAvailable(CheckBalanceActivity.this)) {
                cardNum = toString;
                tv_amount.setText("");
                btn_recharge.setVisibility(View.VISIBLE);
                btn_recharge.setClickable(true);
                tv_message.setText("पर्खनुहोस...");
                checkCustomerBalance(cardNum, tv_message, tv_amount);
            } else {
                if (!isFinishing()) {
                    showNoInternet();
                }
            }
        }
    }

    private void checkCustomerBalance(String cardNum, TextView tv_message, TextView tv_amount) {
        if (GeneralUtils.isNetworkAvailable(this)) {
            ProgressDialog pDialog = new ProgressDialog(this); //Your Activity.this
            if (!isFinishing()) {

                pDialog.setMessage("Requesting");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            byte[] value1 = GeneralUtils.decoderfun(SECRET_KEY);
            RetrofitInterface post = ServerConfigNew.createServiceWithAuth(RetrofitInterface.class, tokenManager);
            Call<CheckBalanceModel> call = null;
            try {
                call = post.checkBalance(Encrypt.encrypt(value1, cardNum));
                call.enqueue(new Callback<CheckBalanceModel>() {
                    @Override
                    public void onResponse(Call<CheckBalanceModel> call, Response<CheckBalanceModel> response) {
                        CheckBalanceModel responseBody = response.body();
                        stopThread = true;
                        pDialog.dismiss();
                        if (response.code() == 200) {

                            scanCardLayout.setVisibility(View.GONE);

                            CheckBalanceModel.Datum data = responseBody.getData();
                            Passenger passenger = new Passenger();
                            passenger.amount = data.getAmount();
                            passenger.id = data.getId();

                            passenger.name = data.getFirstName() + " " + data.getLastName();
                            passenger.address = data.getAddress();
                            passenger.phone = data.getContactNo();
                            passenger.cardNumber = data.getIdentificationId();
                            String newAmount = String.valueOf(passenger.amount);
                            tv_amount.setText("रू " + GeneralUtils.getUnicodeNumber(newAmount));
//                                amount.setText("रू " +passenger.amount);
                            currentAmount = newAmount;
                            tv_message.setText(passenger.name);
                            Log.d("TAG", "onResponse: " + newAmount);
                            SharedPreferences preferences;
                            preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
                            preferences.edit().putString(UtilStrings.PASSENGER_NAME, data.getFirstName() + " " + data.getLastName()).apply();
                            preferences.edit().putString(UtilStrings.PASSENGER_OLD_BLNC, String.valueOf(data.getAmount())).apply();
                        } else if (response.code() == 404) {
                            handleError(response.errorBody(), CheckBalanceActivity.this);
                        } else if (response.code() == 401) {
                            startActivity(new Intent(CheckBalanceActivity.this, HelperLogin.class));
                            finish();
                        } else {
                            tv_message.setText("Could not show balance");
                            tv_amount.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<CheckBalanceModel> call, Throwable t) {
                        pDialog.dismiss();

                        if (t instanceof SocketTimeoutException) {
                        } else {
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showNoInternet();
        }

    }

    @Override
    public void onBackPressed() {
        stopThread = true;
        super.onBackPressed();
        startActivity(new Intent(CheckBalanceActivity.this, TicketAndTracking.class));
        finish();
    }


    public class RechargeAdapter extends RecyclerView.Adapter<RechargeAdapter.MyViewHolder> {
        Context context;
        private List<Integer> priceLists;

        public RechargeAdapter(List<Integer> priceLists, Context context) {
            this.context = context;
            this.priceLists = priceLists;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recharge_price_list, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RechargeAdapter.MyViewHolder holder, final int position) {
            holder.price_value.setText("रू " + GeneralUtils.getUnicodeNumber(String.valueOf(priceLists.get(position))));
            if (transactionNo.equalsIgnoreCase("1") || transactionNo.equalsIgnoreCase("2")) {
                holder.price_value.setBackground(getDrawable(R.color.grey));
            } else {
                holder.price_value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setTitle("के तपाईं " + GeneralUtils.getUnicodeNumber(String.valueOf(priceLists.get(position))) + " रुपैयाँ रिचार्ज गर्न चाहानुहुन्छ?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        rechargeProcess(priceLists.get(position));
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                    }
                                });
                        alertDialog.show();
                    }
                });
            }

        }


        @Override
        public int getItemCount() {
            return priceLists.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView price_value;
            CardView priceCard;


            public MyViewHolder(View itemView) {
                super(itemView);
                price_value = itemView.findViewById(R.id.price_value);
                priceCard = itemView.findViewById(R.id.priceCard);
            }
        }
    }
}
