package com.technosales.net.buslocationannouncement.userregistration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;
import com.technosales.net.buslocationannouncement.base.BaseActivity;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.mosambeesupport.BeepLEDTest;
import com.technosales.net.buslocationannouncement.mosambeesupport.M1CardHandlerMosambee;
import com.technosales.net.buslocationannouncement.mosambeesupport.Printer;
import com.technosales.net.buslocationannouncement.pojo.ApiError;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMERID;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_AMT;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_HASH;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_TRANSACTION_NO;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.USER_NUMBER;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.network;


public class IssueCardActivity extends BaseActivity implements ICreateAccount.View {
    public DetectMThread detectMThread;
    String phoneNumber = "";
    String cardNUmber = "";
    String customer_number, userNumber;
    String user_num;
    String accountTypeStg, helperId, deviceId;
    int total_tickets;
    int total_collections_card;
    boolean stopThread;
    private EditText first_name, middle_name, last_name, contactNo, editTextEmail, editAddress;
    private EditText addressField;
    private TextView tv_phoneNum;
    private TextView tv_cardNum;
    private Button submitButton, btn_cancel;
//    private EPiccType piccType;
    private Button acctype;
    private RegisterImplPresenter presenter;
    private SharedPreferences preferences,preferencesHelper;
    private ProgressDialog progressDialog;
    private ScrollView registerScroll;
    private String success, customerId, cusmoterMobile, customerAmount;
//    private EM1KeyType m1KeyType = EM1KeyType.TYPE_B;
    private int TIME_DELAY = 30000;
    private String ticketId;
    private String valueOfTickets = "";
    private String printData;
    private String latitude;
    private String longitude;
    private DatabaseHelper databaseHelper;
    private Intent intent;
    int successStatus=0;
    SweetAlertDialog sweetAlertDialog;
    Thread thread=new Thread(new Runnable() {
        @Override
        public void run() {
            while (!Thread.interrupted() && !stopThread)
                try {
                    Thread.sleep(TIME_DELAY);
                    runOnUiThread(new Runnable() // start actions in UI thread
                    {
                        @Override
                        public void run() {
                            int[]value={};
                            M1CardHandlerMosambee.read_miCard(handler,value,"IssueCardActivity");
                        }
                    });
                } catch (InterruptedException e) {
                }
        }
    });
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 100:
                    setCardNUm(msg.obj.toString());
                    stopThread=true;
                    break;
                case 505:

                    Toast.makeText(IssueCardActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 200:
                        try {
                            sweetAlertDialog.dismissWithAnimation();
                            Printer.Print(IssueCardActivity.this, printData, handler);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(IssueCardActivity.this, "ग्राहक सफलतापूर्वक दर्ता गरियो।", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(IssueCardActivity.this, TicketAndTracking.class));
                        finish();

                    break;
                default:
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_card);

        setUpToolbar("ग्राहक कार्ड दर्ता", true);
        customer_number = getIntent().getStringExtra(USER_NUMBER);
        progressDialog = new ProgressDialog(IssueCardActivity.this);


        registerScroll = findViewById(R.id.registerScroll);
        presenter = new RegisterImplPresenter(this, new RegisterControllerImpl());
        acctype = findViewById(R.id.acctype);


        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        preferencesHelper =getSharedPreferences(UtilStrings.SHARED_PREFERENCES_HELPER, 0);
        helperId = preferencesHelper.getString(UtilStrings.ID_HELPER, "");

        databaseHelper = new DatabaseHelper(this);

        stopThread=false;
        int[]value={};
        M1CardHandlerMosambee.read_miCard(handler,value,"IssueCardActivity");
        thread.start();

        setupUI();
    }


    void setupUI() {

        submitButton = findViewById(R.id.btn_submit);
        btn_cancel = findViewById(R.id.btn_cancel);
        first_name = findViewById(R.id.input_name);
        tv_phoneNum = findViewById(R.id.text_phone_number);
        tv_cardNum = findViewById(R.id.text_card_num);
        middle_name = findViewById(R.id.middle_name);
        last_name = findViewById(R.id.last_name);
        contactNo = findViewById(R.id.contactNo);
        editTextEmail = findViewById(R.id.editTextEmail);
        editAddress = findViewById(R.id.editAddress);
//        first_name.setText("Hari");
//        last_name.setText("Nepal");
//        contactNo.setText("9842554500");
//        editTextEmail.setText("hari@gmail.com");
//        editAddress.setText("Gothatar");

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IssueCardActivity.this, TicketAndTracking.class));
                finish();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
                total_tickets = preferences.getInt(UtilStrings.TOTAL_TICKETS, 0);
                total_collections_card= preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_CARD, 0);
                deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
                latitude = preferences.getString(UtilStrings.LATITUDE, "0.0");
                longitude = preferences.getString(UtilStrings.LONGITUDE, "0.0");

                total_tickets = total_tickets + 1;
                total_collections_card=total_collections_card+100;
//                if (total_tickets < 10) {
//                    valueOfTickets = "00" + String.valueOf(total_tickets);
//
//                } else if (total_tickets > 9 && total_tickets < 100) {
//                    valueOfTickets = "0" + String.valueOf(total_tickets);
//                } else {
//                    valueOfTickets = String.valueOf(total_tickets);
//                }

                valueOfTickets = String.format("%04d",total_tickets);

                String dateTime = GeneralUtils.getTicketDate() + GeneralUtils.getTicketTime();;
                ticketId =deviceId.substring(deviceId.length() - 2) + dateTime + "" + valueOfTickets;



                if (validate()) {
                    if (helperId.length() > 0) {
                        registerScroll.setScrollY(0);
                        if (GeneralUtils.isNetworkAvailable(IssueCardActivity.this)) {
                            presenter.createAccount(IssueCardActivity.this);
                        } else {
                            Toast.makeText(IssueCardActivity.this, network, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(IssueCardActivity.this, "सहायक छान्नुहोस् ।", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        if (customer_number != null) {
            setPhoneNum(customer_number);
        }
    }

    Boolean validate() {
        if (cardNUmber.equals("")) {
            Toast.makeText(this, "Please Assign Card", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (contactNo.getText().length() == 10) {
            } else {
                Toast.makeText(this, "Please enter valid phone number", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    void setCardNUm(String num) {
        if (num != null) {
            stopThread=true;
            thread.interrupt();
            if (databaseHelper.listBlockList().size() != 0) {
                for (int i = 0; i < databaseHelper.listBlockList().size(); i++) {
                    if (!databaseHelper.listBlockList().get(i).identificationId.equalsIgnoreCase(num)) {
                        try {
                            BeepLEDTest.beepSuccess();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        cardNUmber = num;
                        tv_cardNum.setText(num);
                    } else {
                        Toast.makeText(IssueCardActivity.this, "यो कार्ड ब्लक गरिएको छ। ", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                try {
                    BeepLEDTest.beepSuccess();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                cardNUmber = num;
                tv_cardNum.setText(num);
            }
        } else {
            Toast.makeText(this, "Please Show Card Properly", Toast.LENGTH_SHORT).show();
        }
    }

    void setPhoneNum(String num) {
        phoneNumber = num;
        tv_phoneNum.setText("***"+num.substring(num.length() - 3));
    }

    @Override
    protected void onResume() {
        handler.removeCallbacksAndMessages(null);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);

    }

    @Override
    public String getIdentificationId() {
        if (cardNUmber != null && !cardNUmber.equalsIgnoreCase("")) {
            return cardNUmber;
        } else {
            Toast.makeText(this, "Please Assign Card", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    public String getMobileNo() {
        return  customer_number.substring(customer_number.length() - 10);
    }

    @Override
    public String getFirstName() {
        return first_name.getText().toString();
    }

    @Override
    public String getMiddleName() {
        return middle_name.getText().toString();
    }

    @Override
    public String getLastName() {
        return last_name.getText().toString();
    }

    @Override
    public String getContactNo() {
        return contactNo.getText().toString();
    }

    @Override
    public String getEmailAddress() {
        return editTextEmail.getText().toString();
    }

    @Override
    public String getAddress() {
        return editAddress.getText().toString();
    }

    @Override
    public String getUserType() {
        return getString(R.string.user_account);
    }

    @Override
    public String getDeviceId() {
        if (deviceId != null && !deviceId.equalsIgnoreCase("")) {
            return deviceId;
        } else {
            return null;
        }

    }

    @Override
    public String getDeviceUserId() {
        return helperId;
    }


    @Override
    public void onSuccess(CreateAccountModel.CreateAccountResponse createAccountResponse) {
        String helperAmt = preferencesHelper.getString(UtilStrings.AMOUNT_HELPER, "");
        int newHelperAmt = Integer.valueOf(helperAmt) - 100;


        preferencesHelper.edit().putString(UtilStrings.AMOUNT_HELPER, String.valueOf(newHelperAmt)).apply();

        preferences.edit().putInt(UtilStrings.TOTAL_TICKETS, total_tickets).apply();
        preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS_CARD, total_collections_card).apply();


        progressDialog.dismiss();
        success = "Successful";
        String id = String.valueOf(createAccountResponse.getData().getId());
        String referenceHash = createAccountResponse.getData().getReferenceHash();
        String amount = "100";
        showCardReadLayout(id, amount, referenceHash);
        TIME_DELAY = 25000;

        printData ="कार्ड जारी गरियो \n" +  GeneralUtils.getUnicodeNumber(ticketId) + "\n" + "ग्राहकको नाम :-" + createAccountResponse.getData().getFirstName() + " " + createAccountResponse.getData().getLastName() + "\n " +
                "वर्तमान रकम:-" + "Rs." + GeneralUtils.getUnicodeNumber(amount) + "\n" + "रेजिष्टर्ड फोन नम्बर:-" + "***" + customer_number.substring(customer_number.length() - 3);

    }

    private void showCardReadLayout(String id, String amount, String referenceHash) {
        sweetAlertDialog= new SweetAlertDialog(IssueCardActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText("Account Created Successfully !!!")
                .setContentText("तपाईको कार्ड प्रमाणित गर्नुहोस् ।")
                .setConfirmText("CONFIRM")
                .setCancelable(false);
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        String customerId = Base64.encodeToString(id.getBytes(), Base64.DEFAULT);
                        String customerAmt = Base64.encodeToString(amount.getBytes(), Base64.DEFAULT);
                        String customerHash = Base64.encodeToString(referenceHash.getBytes(), Base64.DEFAULT);
                        String customerTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);
                        Log.i("TAG", "onClick: " + id + amount + referenceHash + customerTranNo);

                        String[] customerDetails={customerId,customerAmt,customerHash,customerTranNo};
                        int[] customerDetailsBlock={CUSTOMERID,CUSTOMER_AMT,CUSTOMER_HASH,CUSTOMER_TRANSACTION_NO};
                        M1CardHandlerMosambee.write_miCard(handler,customerDetails,customerDetailsBlock,"IssueCardActivity-CreateCard");
                    }
                }).show();
    }

    @Override
    public String getLat() {
        return latitude;
    }

    @Override
    public String getLng() {
        return longitude;
    }

    @Override
    public String getTicketId() {
        return ticketId;
    }

    @Override
    public String getDeviceTime() {
        return GeneralUtils.getFullDate() + " " + GeneralUtils.getTime();
    }

    @Override
    public void onFailure(ResponseBody responseBody) {
        progressDialog.dismiss();
        handleErrors(responseBody);
    }

    private void handleErrors(ResponseBody responseBody) {
        ApiError apiErrors = GeneralUtils.convertErrors(responseBody);

        if (responseBody != null) {
            for (Map.Entry<String, List<String>> error : apiErrors.getErrors().entrySet()) {
                if (error.getKey().equals("identificationId")) {
                    tv_cardNum.setError(error.getValue().get(0));
                    Toast.makeText(this, error.getValue().get(0), Toast.LENGTH_SHORT).show();
                } else {
                }
                if (error.getKey().equals("mobileNo")) {
                    tv_phoneNum.setError(error.getValue().get(0));
                    Toast.makeText(this, error.getValue().get(0), Toast.LENGTH_SHORT).show();
                }

                if (error.getKey().equals("emailAddress")) {
                    editTextEmail.setError(error.getValue().get(0));
                    Toast.makeText(this, error.getValue().get(0), Toast.LENGTH_SHORT).show();
                }


            }
        } else {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void noInternetConnection() {

    }

    @Override
    public void connectionTimeOut() {

    }

    @Override
    public void showProgressBar(boolean showpBar) {
//       GeneralUtils. showProgressBar(showpBar,progressBar);
    }

    @Override
    public void unKnownError() {

    }

    @Override
    protected void onDestroy() {
        stopThread = true;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        stopThread = true;
        startActivity(new Intent(this, TicketAndTracking.class));
        finish();
        super.onBackPressed();
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
////                    receiptPrintParam.print(printData, new PrintListenerImpl(IssueCardActivity.this));
////                    Device.beepOk();
//                }
//            }
//        });
//    }

    class DetectMThread extends Thread {
        @Override
        public void run() {
            super.run();
        }
    }

}//last bracket
