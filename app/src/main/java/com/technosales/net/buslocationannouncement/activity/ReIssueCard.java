package com.technosales.net.buslocationannouncement.activity;

import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.mosambeesupport.BeepLEDTest;
import com.technosales.net.buslocationannouncement.mosambeesupport.M1CardHandlerMosambee;
import com.technosales.net.buslocationannouncement.mosambeesupport.Printer;
import com.technosales.net.buslocationannouncement.pojo.ApiError;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;
import com.technosales.net.buslocationannouncement.base.BaseActivity;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.pojo.ReIssueCardResponse;
import com.technosales.net.buslocationannouncement.userregistration.IssueCardActivity;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

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
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SHARED_PREFERENCES;

public class ReIssueCard extends BaseActivity implements View.OnClickListener {
    int successStatus = 0;
    SweetAlertDialog sweetAlertDialog;
    private String customer_card_no;
    private TextView card_num;
    private Button btn_submit, btn_cancel;
    private EditText customer_mob_no;
    private ReIssueCardResponse reIssueCardResponse;
    private ProgressBar progressBar;
    private DatabaseHelper databaseHelper;
    private boolean stopThread;
    private TokenManager tokenManager;
    private SharedPreferences preferences;
    private String printData;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 100:
                    setCardNUm(msg.obj.toString());
                    break;
                case 200:
                    if (msg.obj.toString().equalsIgnoreCase("Success")) {
                        sweetAlertDialog.dismiss();
                        try {
                            BeepLEDTest.beepSuccess();
                            Printer.Print(ReIssueCard.this, printData,handler);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(ReIssueCard.this, "ग्राहक सफलतापूर्वक दर्ता गरियो।", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ReIssueCard.this, TicketAndTracking.class));
                        finish();
                    }
                    break;
                case 505:
                    Toast.makeText(ReIssueCard.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ReIssueCard.this, TicketAndTracking.class));
                    finish();
                    break;
                case 404:
                    recreate();
                    Toast.makeText(ReIssueCard.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reissue_card_layout);
        setUpToolbar("कार्ड पुनः जारी", true);
        btn_cancel = findViewById(R.id.btn_cancel);
        customer_mob_no = findViewById(R.id.customer_mob_no);
        databaseHelper = new DatabaseHelper(this);

        card_num = findViewById(R.id.card_num);
        progressBar = findViewById(R.id.progressBar);
        btn_submit = findViewById(R.id.btn_submit);
        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        stopThread = false;
        int[] value = {};
        M1CardHandlerMosambee.read_miCard(handler, value,"ReIssueCard");

        btn_submit.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    private void setCardNUm(String toString) {
        if (databaseHelper.listBlockList().size() > 0) {
            for (int i = 0; i < databaseHelper.listBlockList().size(); i++) {
                if (databaseHelper.listBlockList().get(i).identificationId.equalsIgnoreCase(toString)) {
                    Toast.makeText(ReIssueCard.this, "यो कार्ड ब्लक गरिएको छ।", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ReIssueCard.this,TicketAndTracking.class));
                    finish();
                } else {
                    try {
                        BeepLEDTest.beepSuccess();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    customer_card_no = toString;
                    card_num.setText(toString);
                }
            }
        } else {
            try {
                BeepLEDTest.beepSuccess();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            customer_card_no = toString;
            card_num.setText(toString);
        }
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
    public void onBackPressed() {
        stopThread = true;
        super.onBackPressed();
        startActivity(new Intent(this,TicketAndTracking.class));
        finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


    private void issueCard(String mobile_customer, String card_helper_id) {
        RetrofitInterface retrofitInterface = ServerConfigNew.createServiceWithAuth(RetrofitInterface.class, tokenManager);
        Call<ReIssueCardResponse> call = retrofitInterface.reissue_card(mobile_customer, card_helper_id);
        call.enqueue(new Callback<ReIssueCardResponse>() {
            @Override
            public void onResponse(Call<ReIssueCardResponse> call, Response<ReIssueCardResponse> response) {
                if (response.isSuccessful()) {
                    reIssueCardResponse = response.body();

                    String id = String.valueOf(reIssueCardResponse.getData().getCard().getId());
                    String referenceHash = reIssueCardResponse.getData().getPreviousHash();
                    String amount = String.valueOf(reIssueCardResponse.getData().getCard().getAmount());

                    printData = "कार्ड पुनः जारी गरियो।" + "\n" + "ग्राहकको नाम :-" + reIssueCardResponse.getData().getCard().getFirstName() + " " + reIssueCardResponse.getData().getCard().getLastName() + "\n " +
                            "वर्तमान रकम:-" + "Rs." + GeneralUtils.getUnicodeNumber(amount) + "\n" + "रेजिष्टर्ड फोन नम्बर:-" + "***" + customer_mob_no.getText().toString().substring(customer_mob_no.getText().toString().length() - 3);
                    showCardReadLayout(id, amount, referenceHash);
                } else if (response.code() == 404) {
                    progressBar.setVisibility(View.GONE);
                    handleError(ReIssueCard.this,response.errorBody());
                } else if (response.code() == 401) {
                    startActivity(new Intent(ReIssueCard.this, HelperLogin.class));
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
        sweetAlertDialog = new SweetAlertDialog(ReIssueCard.this, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText("Card ReIssued Successfully !!!")
                .setContentText("तपाईको कार्ड प्रमाणित गर्नुहोस् ।")
                .setConfirmText("कन्फर्म")
                .setCancelable(false);

        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                String customerId = Base64.encodeToString(id.getBytes(), Base64.DEFAULT);
                String customerAmt = Base64.encodeToString(amount.getBytes(), Base64.DEFAULT);
                String customerHash = Base64.encodeToString(referenceHash.getBytes(), Base64.DEFAULT);
                String customerTranNo = Base64.encodeToString("0".getBytes(), Base64.DEFAULT);
                Log.i("TAG", "onClick: " + id + amount + referenceHash + customerTranNo);

                String[] customerDetails = {customerId, customerAmt, customerHash, customerTranNo};
                int[] customerDetailsBlock = {CUSTOMERID, CUSTOMER_AMT, CUSTOMER_HASH, CUSTOMER_TRANSACTION_NO};
                M1CardHandlerMosambee.write_miCard(handler, customerDetails, customerDetailsBlock,"ReIssueCard-UpdateCard");
            }
        }).show();
    }
    private static void handleError(Context context, ResponseBody errorBody) {
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                if (GeneralUtils.isNetworkAvailable(this)) {
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
                finish();
                break;
        }
    }
}
