package com.technosales.net.buslocationannouncement.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.pax.dal.entity.EBeepMode;
import com.pax.dal.entity.EPiccType;
import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.PrintListenerImpl;
import com.technosales.net.buslocationannouncement.paxsupport.picc.PiccTester;
import com.technosales.net.buslocationannouncement.paxsupport.printer.Device;
import com.technosales.net.buslocationannouncement.paxsupport.printer.PrinterTester;
import com.technosales.net.buslocationannouncement.paxsupport.printer.ReceiptPrintParam;
import com.technosales.net.buslocationannouncement.picc.PiccTransaction;
import com.technosales.net.buslocationannouncement.printlib.Printer;
import com.technosales.net.buslocationannouncement.printlib.RxUtils;
import com.technosales.net.buslocationannouncement.printlib.SysTester;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;
import com.technosales.net.buslocationannouncement.base.BaseActivity;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.pojo.ReIssueCardResponse;
import com.technosales.net.buslocationannouncement.userregistration.IssueCardActivity;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMERID;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_AMT;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_HASH;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_TRANSACTION_NO;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SHARED_PREFERENCES;

public class ReIssueCard extends BaseActivity implements View.OnClickListener {
    private String customer_card_no;
    private TextView card_num;
    private Button btn_submit, btn_cancel;
    private EPiccType piccType;
    public DetectMThread detectMThread;
    private EditText customer_mob_no;
    private ReIssueCardResponse reIssueCardResponse;
    private ProgressBar progressBar;
    private DatabaseHelper databaseHelper;
    private boolean stopThread;
    private TokenManager tokenManager;
    private SharedPreferences preferences;
    private String printData;
    int successStatus=0;
    int total_collections_issue;
        Thread thread=new Thread(new Runnable() {
        @Override
        public void run() {
            while (!Thread.interrupted() && !stopThread)
                try {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() // start actions in UI thread
                    {
                        @Override
                        public void run() {
                            if (detectMThread != null) {
                                detectMThread.interrupt();
                                detectMThread = null;
                            }
                            PiccTester.getInstance(piccType).open();
                            detectMThread = new DetectMThread();
                            detectMThread.start();
                        }
                    });
                } catch (InterruptedException e) {
                }
        }
    });
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reissue_card_layout);
        setUpToolbar("कार्ड पुनः जारी गर्नुहोस् |", true);
        btn_cancel = findViewById(R.id.btn_cancel);
        customer_mob_no = findViewById(R.id.customer_mob_no);
        databaseHelper = new DatabaseHelper(this);

        card_num = findViewById(R.id.card_num);
        progressBar = findViewById(R.id.progressBar);
        btn_submit = findViewById(R.id.btn_submit);
        setUpToolbar("जानकारी अपडेट गर्नुहोस्", true);
        piccType = EPiccType.INTERNAL;
        preferences =getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        stopThread = false;
        (thread).start();

        btn_submit.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    if (msg.obj.toString() != null) {
                        setCardNUm(msg.obj.toString());
                    } else {
                        Toast.makeText(ReIssueCard.this, "Please show card properly", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 200:
                    if (msg.obj.toString()!=null) {
                        Log.i("TAG", "handleMessage: "+successStatus);
                        successStatus=successStatus+Integer.valueOf(msg.obj.toString());
                        if(successStatus==4) {
                            String  status = PrinterTester.getInstance().getStatus();
                            if(status.equalsIgnoreCase("Out of paper ")){
                                Toast.makeText(ReIssueCard.this, "मुद्रण कागज समाप्त भयो।", Toast.LENGTH_SHORT).show();
                            }else {
                                paraPrint(printData);
                            }

                            Toast.makeText(ReIssueCard.this, "ग्राहक सफलतापूर्वक दर्ता गरियो।", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ReIssueCard.this, TicketAndTracking.class));
                            finish();
                        }
                    } else if (msg.obj.toString().equalsIgnoreCase("failed")) {
                        Log.i("TAG", "handleMessage: " + "failed");
                        Toast.makeText(ReIssueCard.this, "Verification Failed.. Please try again", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void setCardNUm(String toString) {
        if (databaseHelper.listBlockList().size() > 0) {
            for (int i = 0; i < databaseHelper.listBlockList().size(); i++) {
                if (databaseHelper.listBlockList().get(i).identificationId.equalsIgnoreCase(toString)) {
                    Toast.makeText(ReIssueCard.this, "यो कार्ड ब्लक गरिएको छ।", Toast.LENGTH_SHORT).show();
                } else {
                    SysTester.getInstance().beep(EBeepMode.FREQUENCE_LEVEL_6, 100);
                    customer_card_no = toString;
                    card_num.setText(toString);
                }
            }
        } else {
            SysTester.getInstance().beep(EBeepMode.FREQUENCE_LEVEL_6, 100);
            customer_card_no = toString;
            card_num.setText(toString);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PiccTester.getInstance(piccType).close();
        if (detectMThread != null) {
            detectMThread.interrupt();
            detectMThread = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PiccTester.getInstance(piccType).close();
        if (detectMThread != null) {
            detectMThread.interrupt();
            detectMThread = null;
        }
    }

    @Override
    public void onBackPressed() {
        stopThread = true;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        stopThread = true;
        PiccTester.getInstance(piccType).close();
        if (detectMThread != null) {
            detectMThread.interrupt();
            detectMThread = null;
        }
        super.onDestroy();
    }


    private void issueCard(String mobile_customer, String card_helper_id) {
        RetrofitInterface retrofitInterface = ServerConfigNew.createServiceWithAuth(RetrofitInterface.class,tokenManager);
        Call<ReIssueCardResponse> call = retrofitInterface.reissue_card(mobile_customer, card_helper_id);
        call.enqueue(new Callback<ReIssueCardResponse>() {
            @Override
            public void onResponse(Call<ReIssueCardResponse> call, Response<ReIssueCardResponse> response) {
                if (response.code() == 200) {
                    reIssueCardResponse = response.body();

                    String id = String.valueOf(reIssueCardResponse.getData().getCard().getId());
                    String referenceHash = reIssueCardResponse.getData().getPreviousHash();
                    String amount =String.valueOf(reIssueCardResponse.getData().getCard().getAmount());

                    printData = "कार्ड पुनः जारी गरियो।" +"\n" +"ग्राहकको नाम :-" + reIssueCardResponse.getData().getCard().getFirstName() + " " +reIssueCardResponse.getData().getCard().getLastName()  + "\n " +
                            "वर्तमान रकम:-" + "Rs." + GeneralUtils.getUnicodeNumber(amount) + "\n" + "रेजिष्टर्ड फोन नम्बर:-" + "***" + customer_mob_no.getText().toString().substring(customer_mob_no.getText().toString().length() - 3);
                     showCardReadLayout(id, amount, referenceHash);
                } else if (response.code() == 404) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ReIssueCard.this, "Mobile number Not Found ", Toast.LENGTH_SHORT).show();
                }else if(response.code()==401){
                    startActivity(new Intent(ReIssueCard.this,HelperLogin.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ReIssueCardResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(ReIssueCard.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showCardReadLayout(String id, String amount, String referenceHash) {

        new SweetAlertDialog(ReIssueCard.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Card ReIssued Successfully !!!")
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
                        for (int i = 0; i < customerDetails.length; i++) {
                            PiccTransaction.getInstance(piccType).registerTranBlock(handler,customerDetails[i],customerDetailsBlock[i]);
                        }
                    }
                }).show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                if (GeneralUtils.isNetworkAvailable(this)) {
                    stopThread=true;
                     thread.interrupt();
                    String mobile_customer = customer_mob_no.getText().toString();
                    if (customer_card_no != null && mobile_customer != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        issueCard(customer_card_no, mobile_customer);
                    } else {
                        Toast.makeText(this, "कार्ड देखाउनु होस् ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, UtilStrings.network, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_cancel:
                startActivity(new Intent(ReIssueCard.this, TicketAndTracking.class));
                break;
        }
    }

   private class DetectMThread extends Thread {
        @Override
        public void run() {
            super.run();
            PiccTester.getInstance(piccType).getId(handler);
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
                    receiptPrintParam.print(printData, new PrintListenerImpl(ReIssueCard.this));
                    Device.beepOk();
                }
            }
        });
    }

}
