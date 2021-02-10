package com.technosales.net.buslocationannouncement.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.hornet.dateconverter.DateConverter;
import com.hornet.dateconverter.Model;
import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.pojo.ApiError;
import com.technosales.net.buslocationannouncement.R;
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

import at.favre.lib.crypto.bcrypt.BCrypt;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMERID;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_AMT;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_HASH;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.NULL;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.PAYMENT_CASH;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SHARED_PREFERENCES;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.STATUS;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TRANSACTION_TYPE_LOAD;


public class CheckBalanceActivity extends BaseActivity {
    int minAmt = 25, maxAmt = 500;
    int total_tickets;
    int total_collections;
    String passenserId, currentAmount, transactionHash;
    String latitude;
    String longitude;
    TokenManager tokenManager;
    int[] customerDetailsRead = {CUSTOMERID, CUSTOMER_HASH};
    int[] customerUpdatedDetailsBlock = {CUSTOMER_AMT, CUSTOMER_HASH};
    ArrayList<String> customerDetails = new ArrayList<>();
    private TextView tv_message;
    private TextView tv_amount;
    private Button btn_recharge;
    private String cardNum = "";
    private String deviceID = "";
    private SharedPreferences preferences, preferencesHelper;
    private String helperString = "";
//    private EPiccType piccType;
    private String isOnlineCheck;
    private int TIME_DELAY = 500;
    private DatabaseHelper databaseHelper;
    private boolean stopThread;
    private String rechargeBill;
    private String newHash = "";
    private int successStatus = 0;
    private Handler rechargeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 100:
                    if (!msg.obj.toString().equalsIgnoreCase("") && msg.obj.toString() != null) {
                        getUserInfo(msg.obj.toString());
                    } else {
                        Toast.makeText(CheckBalanceActivity.this, "Please show card properly", Toast.LENGTH_SHORT).show();
                    }

                case 101:
                    if (msg.obj.toString() != null) {
                        Log.i("TAG", "handleMessage: " + msg.obj.toString());
                        passenserId=msg.obj.toString();
                    } else {
                        Toast.makeText(CheckBalanceActivity.this, "Timeout Please restart", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 102:
                    if (msg.obj.toString() != null) {
                        Log.i("TAG", "handleMessage: " + msg.obj.toString());
                        transactionHash=msg.obj.toString();
                    } else {
                        Toast.makeText(CheckBalanceActivity.this, "Timeout Please restart", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 200:
                    Log.i("TAG", "handleMessage: " + msg.obj.toString());
                    if (msg.obj.toString() != null) {
                        successStatus = successStatus + Integer.valueOf(msg.obj.toString());
                        if (successStatus == 2) {
                            Toast.makeText(CheckBalanceActivity.this, "Recharged Successfully!!!", Toast.LENGTH_SHORT).show();
//                            paraPrint(rechargeBill);
                            startActivity(new Intent(CheckBalanceActivity.this, TicketAndTracking.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(CheckBalanceActivity.this, "Timeout Please restart", Toast.LENGTH_SHORT).show();
                    }
                    if (msg.obj.toString().equalsIgnoreCase("successful")) {

                    }
                    break;
                default:
                    break;
            }
        }
    };
    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!Thread.interrupted() && !stopThread)
                try {
                    Thread.sleep(TIME_DELAY);
                    runOnUiThread(new Runnable() // start actions in UI thread
                    {
                        @Override
                        public void run() {
//                            for (int i = 0; i < customerDetailsRead.length; i++) {
//                            PiccTransaction.getInstance(piccType).read(rechargeHandler, customerDetailsRead);
//                                Log.i("TAG", "run: "+i);
//                            }

                        }
                    });
                } catch (InterruptedException e) {
                }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_balance);
        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        preferencesHelper = getSharedPreferences(UtilStrings.SHARED_PREFERENCES_HELPER, 0);
        databaseHelper = new DatabaseHelper(this);
        setUpToolbar("ब्यालेन्स जाँच", true);
        setupUI();
//        piccType = EPiccType.INTERNAL;

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        stopThread = false;
        (thread).start();

        if (GeneralUtils.isNetworkAvailable(this)) {
            isOnlineCheck = "true";
        } else {
            isOnlineCheck = "false";
        }
    }

    void setupUI() {
        tv_message = findViewById(R.id.tv_message);
        tv_amount = findViewById(R.id.tv_amount);
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

    void rechargeProcess(String currentAmount, int amount) {

        deviceID = preferences.getString(UtilStrings.DEVICE_ID, "");
        helperString = preferencesHelper.getString(UtilStrings.ID_HELPER, "");

        latitude = preferences.getString(UtilStrings.LATITUDE, "0.0");
        longitude = preferences.getString(UtilStrings.LONGITUDE, "0.0");


        total_tickets = preferences.getInt(UtilStrings.TOTAL_TICKETS, 0);
        total_collections = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_CARD, 0);

        total_tickets = total_tickets + 1;
        total_collections = total_collections + amount;

        String valueOfTickets = "";

//        if (total_tickets < 10) {
//            valueOfTickets = "00" + String.valueOf(total_tickets);
//
//        } else if (total_tickets > 9 && total_tickets < 100) {
//            valueOfTickets = "0" + String.valueOf(total_tickets);
//        } else {
//            valueOfTickets = String.valueOf(total_tickets);
//        }

        valueOfTickets = String.format("%04d", total_tickets);

        String dateTime = GeneralUtils.getTicketDate() + GeneralUtils.getTicketTime();
        if (!helperString.equalsIgnoreCase("")){
            if (!helperString.equalsIgnoreCase(passenserId)) {
                Log.i("TAG", "rechargeProcess: " + passenserId + " " + transactionHash);
                if (GeneralUtils.isNetworkAvailable(this)) {
                    String ticketId = deviceID.substring(deviceID.length() - 2) + dateTime + "" + valueOfTickets;
                    String referenceHash = BCrypt.withDefaults().hashToString(10, (transactionHash + ticketId + passenserId + amount).toCharArray());
                    newHash = referenceHash.substring(referenceHash.length() - 9);
                    Map<String, Object> params = new HashMap<>();
                    params.put("helper_id", helperString);
                    params.put("ticket_id", ticketId);
                    params.put("transactionType", TRANSACTION_TYPE_LOAD);
                    params.put("device_time", dateTime);
                    params.put("transactionAmount", amount);
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
                    params.put("passenger_id", passenserId);
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

                                preferences.edit().putInt(UtilStrings.TOTAL_TICKETS, total_tickets).apply();
                                preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS_CARD, total_collections).apply();

                                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
                                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                myEdit.putString(UtilStrings.AMOUNT_HELPER, String.valueOf(recharge.getData().getHelperAmount()));
                                myEdit.apply();

                                printDetails(recharge.getData().getPassengerAmount(), amount);
                                tv_amount.setText("रू " + GeneralUtils.getUnicodeNumber(String.valueOf(recharge.getData().getPassengerAmount())));
                                new SweetAlertDialog(CheckBalanceActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("तपाईंको रिचार्ज सफलतापूर्वक सम्पन्न भयो।!")
                                        .setContentText("तपाईंको ब्यालेन्स अपडेट गर्नुहोस्।")
                                        .setConfirmText("अपडेट")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                Log.i("TAG", "rechargeCard: " + recharge.getData().getPassengerAmount());
                                                String customerAmt = Base64.encodeToString(String.valueOf(recharge.getData().getPassengerAmount()).getBytes(), Base64.DEFAULT);
                                                String customerHash = Base64.encodeToString(newHash.getBytes(), Base64.DEFAULT);
                                                String[] customerUpdatedDetails = {customerAmt, customerHash};
                                                for (int i = 0; i < customerUpdatedDetails.length; i++) {
//                                                    PiccTransaction.getInstance(piccType).writeData(rechargeHandler, customerUpdatedDetails[i], customerUpdatedDetailsBlock[i]);
                                                    if (i == 1) {
                                                        sDialog.dismiss();
                                                    }
                                                }
                                            }
                                        }).show();
                            } else if (response.code() == 404) {
                                handleErrors(response.errorBody());
                            } else if (response.code() == 401) {
                                startActivity(new Intent(CheckBalanceActivity.this, HelperLogin.class));
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
    }else {
            showRechargeError("सहयोगी लग इन छैन। कृपया पहिले लग ईन गर्नुहोस्।");
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
                            startActivity(new Intent(CheckBalanceActivity.this, TicketAndTracking.class));
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
                }else {
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

            rechargeBill = passenger_name + "\n" + "पहिला भएको रकम रु. " + GeneralUtils.getUnicodeNumber(passenger_pre_amount) + "\n"
                    + "थपिएको रकम  रु. " + GeneralUtils.getUnicodeNumber(String.valueOf(amount)) + "\n" + "अब बाँकी रकम रु. " + GeneralUtils.getUnicodeNumber(String.valueOf(newAmount)) + " " + "\n\n" +
                    GeneralUtils.getNepaliMonth(String.valueOf(month)) + " "
                    + GeneralUtils.getUnicodeNumber(String.valueOf(day)) + " " +
                    GeneralUtils.getUnicodeNumber(GeneralUtils.getTime()) + "\n";
        }
    }

    void showNoInternet() {
        new SweetAlertDialog(CheckBalanceActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error!")
                .setContentText("No internet")
                .setConfirmText("close")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

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
                            rechargeProcess(currentAmount, amt);
                            mAlertDialog.dismiss();
                        }
                    } else
                        Toast.makeText(CheckBalanceActivity.this, "Amount should be between " + minAmt + " - " + maxAmt, Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(CheckBalanceActivity.this, "Please enter amount", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void getUserDetails(ArrayList<String> customerDetails) {
        Log.i("TAG", "getUserDetails: " + customerDetails.get(0) + "  " + customerDetails.get(1));
        if (customerDetails.size() > 0) {
            passenserId = customerDetails.get(0);
            transactionHash = customerDetails.get(1);
        }
    }

    private void getUserInfo(String toString) {
        if (databaseHelper.listBlockList().size() > 0) {
            for (int i = 0; i < databaseHelper.listBlockList().size(); i++) {
                if (databaseHelper.listBlockList().get(i).identificationId.equalsIgnoreCase(toString)) {
                    Toast.makeText(CheckBalanceActivity.this, "यो कार्ड ब्लक गरिएको छ।", Toast.LENGTH_SHORT).show();
                } else {
                    if (GeneralUtils.isNetworkAvailable(CheckBalanceActivity.this)) {
//                        SysTester.getInstance().beep(EBeepMode.FREQUENCE_LEVEL_6, 100);
                        cardNum = toString;
                        tv_amount.setText("");
                        btn_recharge.setVisibility(View.VISIBLE);
                        btn_recharge.setClickable(true);
                        tv_message.setText("पर्खनुहोस...");
                        checkCustomerBalance(cardNum, tv_message, tv_amount);
                    } else {
                        showNoInternet();
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
                showNoInternet();
            }
        }
    }

    private void checkCustomerBalance(String cardNum, TextView tv_message, TextView tv_amount) {
        if (GeneralUtils.isNetworkAvailable(this)) {

            RetrofitInterface post = ServerConfigNew.createServiceWithAuth(RetrofitInterface.class, tokenManager);
            Call<CheckBalanceModel> call = post.checkBalance(cardNum);
            call.enqueue(new Callback<CheckBalanceModel>() {
                @Override
                public void onResponse(Call<CheckBalanceModel> call, Response<CheckBalanceModel> response) {
                    CheckBalanceModel responseBody = response.body();
                    stopThread = true;
                    if (response.code() == 200) {
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
                        tv_message.setText("यात्रीको नाम: " + passenger.name);
                        Log.d("TAG", "onResponse: " + newAmount);
                        SharedPreferences preferences;
                        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
                        preferences.edit().putString(UtilStrings.PASSENGER_NAME, data.getFirstName() + " " + data.getLastName()).apply();
                        preferences.edit().putString(UtilStrings.PASSENGER_OLD_BLNC, String.valueOf(data.getAmount())).apply();
                    } else if(response.code()==404) {
                        handleError(response.errorBody(),CheckBalanceActivity.this);
                    }else if(response.code()==401) {
                        startActivity(new Intent(CheckBalanceActivity.this,HelperLogin.class));
                        finish();
                    }else {
                        tv_message.setText("Could not show balance");
                        tv_amount.setText("");
                    }
                }

                @Override
                public void onFailure(Call<CheckBalanceModel> call, Throwable t) {
                    if (t instanceof SocketTimeoutException) {

                    } else {
                    }
                }
            });
        }

    }
    private static void handleError(ResponseBody errorBody, Context context) {
        ApiError apiErrors = GeneralUtils.convertErrors(errorBody);

        if (errorBody != null) {
            for (Map.Entry<String, List<String>> error : apiErrors.getErrors().entrySet()) {
                if (error.getKey().equals("message")) {
                      Toast.makeText(context, error.getValue().get(0), Toast.LENGTH_SHORT).show();
                }


            }
        } else {
            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

//    private void paraPrint(String printData) {
//        RxUtils.runInBackgroud(new Runnable() {
//            @Override
//            public void run() {
////                ReceiptPrintParam receiptPrintParam = new ReceiptPrintParam();
//                String printType = "error";
//                if (GeneralUtils.needBtPrint()) {
//                    Printer.printA60Receipt("", "", printType);
//                } else {
////                    receiptPrintParam.print(printData, new PrintListenerImpl(CheckBalanceActivity.this));
////                    Device.beepOk();
//                }
//            }
//        });
//    }

    @Override
    public void onBackPressed() {
        stopThread = true;
        super.onBackPressed();
    }
}
