package com.technosales.net.buslocationannouncement.mosambeesupport;

import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.card.mifare.M1CardHandler;
import com.morefun.yapi.card.mifare.M1KeyTypeConstrants;
import com.morefun.yapi.device.reader.icc.ICCSearchResult;
import com.morefun.yapi.device.reader.icc.IccCardType;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.SDKManager;
import com.technosales.net.buslocationannouncement.additionalfeatures.PayByCardActivity;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.KEY_B;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_CUSTOMER;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_FIRST_TRANSATION;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_SECOND_TRANSATION;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRANSATION;

public class TestActivity extends AppCompatActivity {
    Button validate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        validate=findViewById(R.id.validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//           M1CardHandlerMosambee.write_miCard();
            }
        });

    }

}