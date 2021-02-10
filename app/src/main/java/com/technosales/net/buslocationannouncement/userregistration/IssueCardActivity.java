package com.technosales.net.buslocationannouncement.userregistration;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.pax.dal.entity.EBeepMode;
import com.pax.dal.entity.EM1KeyType;
import com.pax.dal.entity.EPiccType;
import com.technosales.net.buslocationannouncement.paxsupport.printer.Device;
import com.technosales.net.buslocationannouncement.paxsupport.printer.PrinterTester;
import com.technosales.net.buslocationannouncement.paxsupport.printer.ReceiptPrintParam;
import com.technosales.net.buslocationannouncement.callcontrol.IncomingCallReceiver;
import com.technosales.net.buslocationannouncement.picc.PiccTransaction;
import com.technosales.net.buslocationannouncement.printlib.Printer;
import com.technosales.net.buslocationannouncement.printlib.RxUtils;
import com.technosales.net.buslocationannouncement.printlib.SysTester;
import com.technosales.net.buslocationannouncement.PrintListenerImpl;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;
import com.technosales.net.buslocationannouncement.base.BaseActivity;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
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
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRAILER_CUSTOMER_FIRST_TRANSACTION;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRAILER_CUSTOMER_SECOND_TRANSACTION;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.network;

//import com.pax.dal.entity.EBeepMode;
@SuppressLint("HandlerLeak")
public class IssueCardActivity extends BaseActivity implements ICreateAccount.View {
    public DetectMThread detectMThread;
    String phoneNumber = "";
    String cardNUmber = "";
    String customer_number, userNumber;
    String user_num;
    String accountTypeStg, helperId, deviceId;
    int total_tickets;
    int total_collections_issue;
    boolean stopThread;
    private EditText first_name, middle_name, last_name, contactNo, editTextEmail, editAddress;
    private EditText addressField;
    private TextView tv_phoneNum;
    private TextView tv_cardNum;
    private Button submitButton, btn_cancel;
    private EPiccType piccType;
    private Button acctype;
    private RegisterImplPresenter presenter;
    private SharedPreferences preferences,preferencesHelper;
    private ProgressDialog progressDialog;
    private ScrollView registerScroll;
    private String success, customerId, cusmoterMobile, customerAmount;
    private EM1KeyType m1KeyType = EM1KeyType.TYPE_B;
    private int TIME_DELAY = 1000;
    private String ticketId;
    private String valueOfTickets = "";
    private String printData;
    private String latitude;
    private String longitude;
    private DatabaseHelper databaseHelper;
    private Intent intent;
    int successStatus=0;
    int[]authBlock={SECTOR_TRAILER_CUSTOMER_FIRST_TRANSACTION,SECTOR_TRAILER_CUSTOMER_SECOND_TRANSACTION};
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
                            PiccTransaction.getInstance(piccType).readId(handler);
                            for (int i = 0; i < authBlock.length; i++) {
                                int finalI = i;
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        PiccTransaction.getInstance(piccType).authSector(handler,authBlock[finalI]);
                                    }
                                }, 1000);

                            }
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
                    if (msg.obj.toString() != null) {
                        setCardNUm(msg.obj.toString());
                    } else {
                        Toast.makeText(IssueCardActivity.this, "Timeout Please restart", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 200:
                    if (msg.obj.toString()!=null) {
                        Log.i("TAG", "handleMessage: "+successStatus);
                        successStatus=successStatus+Integer.valueOf(msg.obj.toString());
                        if(successStatus==4) {
                          String  status = PrinterTester.getInstance().getStatus();
                            if(status.equalsIgnoreCase("Out of paper ")){
                                Toast.makeText(IssueCardActivity.this, "मुद्रण कागज समाप्त भयो।", Toast.LENGTH_SHORT).show();
                            }else {
                                paraPrint(printData);
                            }


                            Toast.makeText(IssueCardActivity.this, "ग्राहक सफलतापूर्वक दर्ता गरियो।", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(IssueCardActivity.this, TicketAndTracking.class));
                            finish();
                        }
                    } else if (msg.obj.toString().equalsIgnoreCase("failed")) {
                        Log.i("TAG", "handleMessage: " + "failed");
                        Toast.makeText(IssueCardActivity.this, "Verification Failed.. Please try again", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_card);
        intent=new Intent(this, IncomingCallReceiver.class);
        startService(intent);
        setUpToolbar("ग्राहक कार्ड दर्ता", true);
        userNumber = getIntent().getStringExtra("phone_number");
        progressDialog = new ProgressDialog(IssueCardActivity.this);
        piccType = EPiccType.INTERNAL;

        SharedPreferences sharedPreferences = getSharedPreferences("User_NUM", MODE_PRIVATE);
        user_num = sharedPreferences.getString("userNum", "0");
        registerScroll = findViewById(R.id.registerScroll);
        presenter = new RegisterImplPresenter(this, new RegisterControllerImpl());
        acctype = findViewById(R.id.acctype);


        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        preferencesHelper =getSharedPreferences(UtilStrings.SHARED_PREFERENCES_HELPER, 0);
        helperId = preferencesHelper.getString(UtilStrings.ID_HELPER, "");

        databaseHelper = new DatabaseHelper(this);
        customer_number = getIntent().getStringExtra(IncomingCallReceiver.key_bootUpStart);
        getCallDetails(user_num, customer_number);
    }

    private void getCallDetails(final String userNum, String customer_num) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String getSimNumber = telemamanger.getLine1Number();
        Log.e("TAG", "getCallDetails: " + getSimNumber);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (getSimNumber != null) {

            builder.setTitle("Number Received").setMessage("कृपया " + getSimNumber + " मा कल गर्नुहोस यसमा कुनै शुल्क लाग्दैन " + userNum);

            builder.setPositiveButton("चेक गर्नुहोस", null);
            builder.setNegativeButton("रद्द गर्नुहोस", null);
        }

        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView tv = new TextView(IssueCardActivity.this);
        tv.setPadding(5, 5, 5, 5);
        tv.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        layout.addView(tv);
        builder.setView(layout);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();
        mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customer_num != null) {
                    if(intent!=null){
                        stopService(intent);
                    }
                    String trimmed = customer_num.substring(customer_num.length() - 3);
                    if (userNum.equals(trimmed)) {
                        tv.setText("Matched");
                        mAlertDialog.dismiss();
                        stopThread = false;
                        (thread).start();

                        setupUI();
                    } else {
                        tv.setText("NOT Matched");
                    }
                } else {
                    Toast.makeText(IssueCardActivity.this, "Waiting for call", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "रद्द गर्नुहोस",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.dismiss();
                        startActivity(new Intent(IssueCardActivity.this, TicketAndTracking.class));
                        finish();
                    }
                });
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
        first_name.setText("Hari");
        last_name.setText("Nepal");
        contactNo.setText("9842554500");
        editTextEmail.setText("hari@gmail.com");
        editAddress.setText("Gothatar");

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
                total_collections_issue= preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_CARD, 0);
                deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
                latitude = preferences.getString(UtilStrings.LATITUDE, "0.0");
                longitude = preferences.getString(UtilStrings.LONGITUDE, "0.0");

                total_tickets = total_tickets + 1;
                total_collections_issue=total_collections_issue+100;
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
            setPhoneNum(user_num);
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
                        SysTester.getInstance().beep(EBeepMode.FREQUENCE_LEVEL_6, 100);
                        cardNUmber = num;
                        tv_cardNum.setText(num);
                    } else {
                        Toast.makeText(IssueCardActivity.this, "यो कार्ड ब्लक गरिएको छ। ", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                SysTester.getInstance().beep(EBeepMode.FREQUENCE_LEVEL_6, 100);
                cardNUmber = num;
                tv_cardNum.setText(num);
            }
        } else {
            Toast.makeText(this, "Please Show Card Properly", Toast.LENGTH_SHORT).show();
        }
    }

    void setPhoneNum(String num) {
        phoneNumber = num;
        tv_phoneNum.setText(num.substring(num.length() - 3));
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
        preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS_CARD, total_collections_issue).apply();


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

        new SweetAlertDialog(IssueCardActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Account Created Successfully !!!")
                .setContentText("तपाईको कार्ड प्रमाणित गर्नुहोस् ।")
                .setConfirmText("CONFIRM")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        String customerId = Base64.encodeToString(id.getBytes(), Base64.DEFAULT);
                        String customerAmt = Base64.encodeToString(amount.getBytes(), Base64.DEFAULT);
                        String customerHash = Base64.encodeToString(referenceHash.getBytes(), Base64.DEFAULT);
                        String customerTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);
                        Log.i("TAG", "onClick: " + id + amount + referenceHash + customerTranNo);

                        String[] customerDetails={customerId,customerAmt,customerHash,customerTranNo};
                        int[] customerDetailsBlock={CUSTOMERID,CUSTOMER_AMT,CUSTOMER_HASH,CUSTOMER_TRANSACTION_NO};
//                        PiccTransaction.getInstance(piccType).registerCustomerCard(handler,customerDetails,customerDetailsBlock);
                        for (int i = 0; i < customerDetails.length; i++) {
                            PiccTransaction.getInstance(piccType).registerTranBlock(handler,customerDetails[i],customerDetailsBlock[i]);
                        }
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

    private void paraPrint(String printData) {
        RxUtils.runInBackgroud(new Runnable() {
            @Override
            public void run() {
                ReceiptPrintParam receiptPrintParam = new ReceiptPrintParam();
                String printType = "error";
                if (GeneralUtils.needBtPrint()) {
                    Printer.printA60Receipt("", "", printType);
                } else {
                    receiptPrintParam.print(printData, new PrintListenerImpl(IssueCardActivity.this));
                    Device.beepOk();
                }
            }
        });
    }

    class DetectMThread extends Thread {
        @Override
        public void run() {
            super.run();
        }
    }

}//last bracket
