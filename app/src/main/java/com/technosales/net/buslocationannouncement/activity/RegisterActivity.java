package com.technosales.net.buslocationannouncement.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.network.GetPricesFares;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_reg;
    private TextInputEditText reg_device_number;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewIniialize();


        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        if (databaseHelper.routeStationLists().size() > 1) {
           /* if (GeneralUtils.isNetworkAvailable(this)) {
                reg_device_number.setText(sharedPreferences.getString(UtilStrings.DEVICE_ID, ""));
            } else {*/
            startActivity(new Intent(this, TicketAndTracking.class));
            finish();
//            }
        }


    }

    private void viewIniialize() {
        reg_device_number = findViewById(R.id.reg_device_number);
        btn_reg = findViewById(R.id.btn_reg);
        btn_reg.setOnClickListener(this);

//        reg_device_number.setText("8170613861");
        /*reg_device_number.setText("8170613553");*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reg:
                if (GeneralUtils.isNetworkAvailable(this)) {
                    if (reg_device_number.getText().toString().trim().length() > 0) {
                        if (getMacAddr() != null) {
                        new GetPricesFares(this, null).getFares(reg_device_number.getText().toString().trim(), getMacAddr(), false);
                        sharedPreferences.edit().putString(UtilStrings.DEVICE_ID, reg_device_number.getText().toString().trim()).apply();
                    }
                    } else {
                        reg_device_number.setError("Enter Device Number");
                    }
                } else {

                    Toast.makeText(this, UtilStrings.network, Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

}