package com.technosales.net.buslocationannouncement.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.pojo.ApiError;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.pojo.HelperModel;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SHARED_PREFERENCES;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SHARED_PREFERENCES_HELPER;

public class HelperLogin extends AppCompatActivity {
    String card_helper_id;
    TextView card_num;
//    Button btn_submit, btn_cancel;
    private String deviceId;
    SharedPreferences preferences;
    HelperModel helperDetails;
//    private EPiccType piccType;
    private ImageView helperLogin;
    private int TIME_DELAY = 500;
    private boolean stopThread;
   private TokenManager tokenManager;
    private DatabaseHelper databaseHelper;
    ProgressDialog pClick;
Thread thread=new Thread(new Runnable() {
    @Override
    public void run() {
        while (!Thread.interrupted() && !stopThread)
            try {
                Thread.sleep(TIME_DELAY);
//                PiccTransaction.getInstance(piccType).readId(handler);
            } catch (InterruptedException e) {
            }
    }
});
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_helper_login);
        preferences = getSharedPreferences(SHARED_PREFERENCES, 0);
        deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
        card_num = findViewById(R.id.card_num);
        pClick = new ProgressDialog(this); //Your Activity.this

        databaseHelper = new DatabaseHelper(this);
        helperLogin=findViewById(R.id.helperIcon);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Glide.with(this).asGif().load(R.drawable.helper).into(helperLogin);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
//        piccType = EPiccType.INTERNAL;
        stopThread = false;
        (thread).start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 100:
                    if (msg.obj.toString() != null) {
                        setHelperId(msg.obj.toString());
                        stopThread=true;
                        thread.interrupt();

                    } else {
                        Toast.makeText(HelperLogin.this, "Timeout Please restart", Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private void setHelperId(String toString) {
        if (databaseHelper.listBlockList().size() != 0) {
            for (int i = 0; i < databaseHelper.listBlockList().size(); i++) {
                if (!databaseHelper.listBlockList().get(i).identificationId.equalsIgnoreCase(toString)) {
                    card_helper_id = toString;
                    card_num.setText(toString);
                    if (GeneralUtils.isNetworkAvailable(HelperLogin.this)) {
                        if (deviceId != null && card_helper_id != null) {
                            sendHelperDetail(deviceId, card_helper_id);

                            pClick.setMessage("कृपया पर्खनुहोस्...");
                            pClick.setCancelable(true);
                            pClick.show();
                        } else {
                            Toast.makeText(HelperLogin.this, "कार्ड देखाउनु होस् ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(HelperLogin.this, UtilStrings.network, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HelperLogin.this, "यो कार्ड ब्लक गरिएको छ।", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            card_helper_id =toString;
            card_num.setText(toString);

            if (GeneralUtils.isNetworkAvailable(HelperLogin.this)) {
                if (deviceId != null && card_helper_id != null) {
                    sendHelperDetail(deviceId, card_helper_id);
                    pClick.setMessage("कृपया पर्खनुहोस्...");
                    pClick.setCancelable(true);
                    pClick.show();
                    GeneralUtils.hideKeyboard(HelperLogin.this);
                } else {
                    Toast.makeText(HelperLogin.this, "कार्ड देखाउनु होस् ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(HelperLogin.this, UtilStrings.network, Toast.LENGTH_SHORT).show();
            }
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
    protected void onDestroy() {
        stopThread = true;
        super.onDestroy();
    }

    private void sendHelperDetail(String deviceId, String card_helper_id) {
        RetrofitInterface retrofitInterface = ServerConfigNew.createService(RetrofitInterface.class);
        Call<HelperModel> call = retrofitInterface.helperLogin(card_helper_id, deviceId);
        call.enqueue(new Callback<HelperModel>() {
            @Override
            public void onResponse(Call<HelperModel> call, Response<HelperModel> response) {
                if (response.isSuccessful()) {
                    pClick.dismiss();
                    helperDetails = response.body();
                    tokenManager.saveToken(helperDetails.getData().getToken());

                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_HELPER, MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString(UtilStrings.EMAIL_HELPER, helperDetails.getData().getHelper().getEmailAddress());
                    myEdit.putString(UtilStrings.CONTACT_HELPER, helperDetails.getData().getHelper().getContactNo());
                    myEdit.putString(UtilStrings.ID_HELPER, String.valueOf(helperDetails.getData().getHelper().getId()));
                    myEdit.putString(UtilStrings.AMOUNT_HELPER, String.valueOf(helperDetails.getData().getHelper().getAmount()));
                    if (helperDetails.getData().getHelper().getMiddleName() != null) {
                        myEdit.putString(UtilStrings.NAME_HELPER, helperDetails.getData().getHelper().getFirstName() + " " + helperDetails.getData().getHelper().getMiddleName() + " " + helperDetails.getData().getHelper().getLastName());
                    } else {
                        myEdit.putString(UtilStrings.NAME_HELPER, helperDetails.getData().getHelper().getFirstName() + " " + helperDetails.getData().getHelper().getLastName());
                    }
                    myEdit.apply();
                        Toast.makeText(HelperLogin.this, "Helper Successfully logged in", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HelperLogin.this, TicketAndTracking.class));
                        finish();
                } else if (response.code() == 400) {
                    pClick.dismiss();
                    Toast.makeText(HelperLogin.this, "Helper not Registered", Toast.LENGTH_SHORT).show();
                } else if(response.code()==404) {
                    pClick.dismiss();
                    handleErrors(response.errorBody());

                }
            }

            @Override
            public void onFailure(Call<HelperModel> call, Throwable t) {
                pClick.dismiss();
                if (t.getMessage() != null) {
                    Toast.makeText(HelperLogin.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void handleErrors(ResponseBody responseBody) {
        ApiError apiErrors = GeneralUtils.convertErrors(responseBody);
        if (responseBody != null) {
            for (Map.Entry<String, List<String>> error : apiErrors.getErrors().entrySet()) {
                if (error.getKey().equals("message")) {
                    Toast.makeText(this, error.getValue().get(0), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        stopThread = true;
        super.onBackPressed();
    }
}