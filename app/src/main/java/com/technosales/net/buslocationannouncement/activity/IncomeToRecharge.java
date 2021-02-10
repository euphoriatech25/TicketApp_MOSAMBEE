package com.technosales.net.buslocationannouncement.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pax.dal.entity.EBeepMode;
import com.pax.dal.entity.EPiccType;
import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.paxsupport.picc.PiccTester;
import com.technosales.net.buslocationannouncement.pojo.ApiError;
import com.technosales.net.buslocationannouncement.printlib.SysTester;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;
import com.technosales.net.buslocationannouncement.serverconn.ServiceConfig;
import com.technosales.net.buslocationannouncement.base.BaseActivity;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.pojo.IncomeToRechargeModel;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomeToRecharge extends BaseActivity implements View.OnClickListener {
    String card_helper_id;
    TextView card_num;
    Button btn_submit, btn_cancel;
    private String deviceId;
    SharedPreferences preferences;
    EditText amount;
    private Integer recharge;
    private Integer updatedRecharge;
   private DatabaseHelper databaseHelper;
    private EPiccType piccType;
    public DetectMThread detectMThread;
    private boolean stopThread;
 TokenManager tokenManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_income_to_recharge);
        setUpToolbar(getString(R.string.incomeTorecharge), true);
        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
        btn_cancel = findViewById(R.id.btn_cancel);
        card_num = findViewById(R.id.card_num);
        amount = findViewById(R.id.amount);
        btn_submit = findViewById(R.id.btn_submit);

        databaseHelper = new DatabaseHelper(this);
        piccType = EPiccType.INTERNAL;

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        stopThread = false;
        (new Thread(new Runnable() {
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
        })).start();


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
                        Toast.makeText(IncomeToRecharge.this, "Please show card properly", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(IncomeToRecharge.this, "यो कार्ड ब्लक गरिएको छ।", Toast.LENGTH_SHORT).show();
                } else {
                    SysTester.getInstance().beep(EBeepMode.FREQUENCE_LEVEL_6, 100);
                    card_helper_id = toString;
                    card_num.setText(toString);
                }
            }
        } else {
            SysTester.getInstance().beep(EBeepMode.FREQUENCE_LEVEL_6, 100);
            card_helper_id = toString;
            card_num.setText(toString);
        }

    }

    private class DetectMThread extends Thread {
        @Override
        public void run() {
            super.run();
            PiccTester.getInstance(piccType).getId(handler);
        }
    }
    private void sendHelperDetail(String deviceId, String card_helper_id, ProgressDialog progressDialog) {
//        recharge=preferences.getInt(UtilStrings.RECHARGE_ITR,0);
//        if (recharge != null) {
//            updatedRecharge = recharge + Integer.parseInt(amount.getText().toString());
//        } else {
//            updatedRecharge = Integer.parseInt(amount.getText().toString());
//        }
      int updatedRecharge=  Integer.parseInt(amount.getText().toString());
        RetrofitInterface retrofitInterface = ServiceConfig.createService(RetrofitInterface.class);
        Call<IncomeToRechargeModel> call = retrofitInterface.transferRechargeToBalance(card_helper_id, updatedRecharge, deviceId);
        call.enqueue(new Callback<IncomeToRechargeModel>() {
            @Override
            public void onResponse(Call<IncomeToRechargeModel> call, Response<IncomeToRechargeModel> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    IncomeToRechargeModel body = response.body();
                    if(!body.getError()) {
                        progressDialog.dismiss();
//                        preferences.edit().putInt(UtilStrings.RECHARGE_ITR, body.getData().getRecharge()).apply();
                        Toast.makeText(IncomeToRecharge.this, body.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(IncomeToRecharge.this, TicketAndTracking.class));
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(IncomeToRecharge.this, "This helper is not assigned to the current device", Toast.LENGTH_SHORT).show();
                    }
                }else if(response.code()==401) {
                    startActivity(new Intent(IncomeToRecharge.this,HelperLogin.class));
                    finish();
                }else if(response.code()==404){
                    handleErrors(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<IncomeToRechargeModel> call, Throwable t) {
                if (t.getMessage() != null) {
                    progressDialog.dismiss();
//                    Toast.makeText(IncomeToRecharge.this, "You don't have sufficient Balance", Toast.LENGTH_SHORT).show();
                    Toast.makeText(IncomeToRecharge.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void handleErrors(ResponseBody responseBody) {
        ApiError apiErrors = GeneralUtils.convertErrors(responseBody);

        if (responseBody != null) {
            for (Map.Entry<String, List<String>> error : apiErrors.getErrors().entrySet()) {
                Toast.makeText(this, error.getValue().get(0), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                if(GeneralUtils.isNetworkAvailable(this)) {
                    if (deviceId != null && card_helper_id != null) {
                        ProgressDialog progressDialog = new ProgressDialog(IncomeToRecharge.this);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setTitle("Income to Recharge...");
                        progressDialog.show();
                        sendHelperDetail(deviceId, card_helper_id, progressDialog);
                    }
                }else {
                    Toast.makeText(this, UtilStrings.network, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_cancel:
                startActivity(new Intent(IncomeToRecharge.this, TicketAndTracking.class));
                break;
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
        stopThread=true;
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

}
