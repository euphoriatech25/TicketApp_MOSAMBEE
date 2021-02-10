package com.technosales.net.buslocationannouncement.NCF;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pax.dal.IPicc;
import com.pax.dal.entity.EM1KeyType;
import com.pax.dal.entity.EPiccType;
import com.pax.dal.entity.PiccCardInfo;
import com.pax.dal.exceptions.PiccDevException;
import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;
import com.technosales.net.buslocationannouncement.additionalfeatures.PayByCardActivity;
import com.technosales.net.buslocationannouncement.paxsupport.picc.PiccTester;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.paxsupport.printer.PrinterTester;
import com.technosales.net.buslocationannouncement.picc.PiccTransaction;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import at.favre.lib.crypto.bcrypt.BCrypt;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMERID;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_AMT;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_HASH;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CUSTOMER_TRANSACTION_NO;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.KEY_A;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRAILER_CUSTOMER_DETAILS;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRAILER_CUSTOMER_FIRST_TRANSACTION;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRAILER_CUSTOMER_SECOND_TRANSACTION;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRANSACTION_NO;

@SuppressLint("HandlerLeak")
public class EncrptDecrypt extends AppCompatActivity {
    public static EPiccType piccType;
    public IPicc picc;
    TextView toNormal;
    Button button1,button;
    String TAG = "aaaaaaaaaaaaaaa";
            byte[] customerId=null; byte[] customerId1=null; byte[] customerId2=null; byte[] customerId3=null;
            ArrayList<String>arrayList=new ArrayList<>();

    private Handler handlerTransaction = new Handler() {
        public void handleMessage(android.os.Message msg) {
            this.obtainMessage();
            Log.i(TAG, "handleMessage: "+msg.what+" "+msg.obj.toString());

            switch (msg.what) {
                case 100:

                   if (msg.obj.toString() != null) {
                        arrayList.add(msg.obj.toString());
                    } else {
                        Toast.makeText(EncrptDecrypt.this, "Timeout Please restart", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        piccType = EPiccType.INTERNAL;
        setContentView(R.layout.activity_test);
        button = findViewById(R.id.writingButton);
        button1 = findViewById(R.id.writutton);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeValue();

            }
        });

          button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValue();
            }
        });
    }

    private void getValue() {
        for (int i = 20; i < 23; i++) {
//            PiccTransaction.getInstance(piccType).readOfflineTran(handlerTransaction,i);
        }
    }
    private void writeValue() {

        int[]valueBlock={SECTOR_TRAILER_CUSTOMER_DETAILS,SECTOR_TRANSACTION_NO,SECTOR_TRAILER_CUSTOMER_FIRST_TRANSACTION,SECTOR_TRAILER_CUSTOMER_SECOND_TRANSACTION};
        for (int i = 0; i < valueBlock.length; i++) {
            PiccTransaction.getInstance(piccType).writeData(handlerTransaction,"",valueBlock[i]);
        }
    }
}