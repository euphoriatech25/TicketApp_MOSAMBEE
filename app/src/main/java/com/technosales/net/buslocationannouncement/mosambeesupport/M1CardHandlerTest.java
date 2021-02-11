package com.technosales.net.buslocationannouncement.mosambeesupport;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.card.mifare.M1CardHandler;
import com.morefun.yapi.card.mifare.M1KeyTypeConstrants;
import com.morefun.yapi.device.reader.icc.ICCSearchResult;
import com.morefun.yapi.device.reader.icc.IccCardType;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.technosales.net.buslocationannouncement.SDKManager;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;


public class M1CardHandlerTest {
    private static final String TAG = "M1CardHandlerTest";
    public static final byte[] KEY_DEFAULT =
            {(byte) 0xFF, (byte) 0xDB, (byte) 0x59, (byte) 0xCF, (byte) 0xF2, (byte) 0xF3,
                    (byte) 0x08, (byte) 0x77, (byte) 0x8F, (byte) 0xFF,
                    (byte) 0x2C, (byte) 0x1F, (byte) 0xEF, (byte) 0x93, (byte) 0xAA, (byte) 0x13};

    public static final byte[] KEY_A = {(byte) 0xFF, (byte) 0xDB, (byte) 0x59, (byte) 0xCF, (byte) 0xF2, (byte) 0xF3};
    public static final byte[] ACCESS_BITS = {(byte) 0x08, (byte) 0x77, (byte) 0x8F, (byte) 0xFF};
    public static final byte[] KEY_B = {(byte) 0x2C, (byte) 0x1F, (byte) 0xEF, (byte) 0x93, (byte) 0xAA, (byte) 0x13};


    public static void test_m1card(Handler handler) {
        DeviceServiceEngine mSDKManager;
        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            Log.e("TAG", "ServiceEngine is Null");
            return;
        }
        new SearchCardOrCardReaderTest(mSDKManager).searchRFCard(new String[]{IccCardType.M1CARD}, new SearchCardOrCardReaderTest.OnSearchListener() {
            @Override
            public void onSearchResult(int retCode, Bundle bundle) {
                if (ServiceResult.Success == retCode) {
                    String cardType = bundle.getString(ICCSearchResult.CARDTYPE);
                    if (IccCardType.M1CARD.equals(cardType)) {
                        m1card(mSDKManager,handler);
                    }
                } else {
//                    alertDialogOnShowListener.showMessage(getString(R.string.msg_icorrfid));
                }
            }
        });
    }

    private static void m1card(DeviceServiceEngine engine, Handler handler) {
        try {
            M1CardHandler m1CardHandler = engine.getM1CardHandler(engine.getIccCardReader(IccReaderSlot.RFSlOT));
            byte key[] = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
            if (m1CardHandler == null) {
//                alertDialogOnShowListener.showMessage(getString(R.string.msg_readfail_retry));
                return;
            }

            byte[] uid = new byte[4];
            int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, 0, KEY_A, uid);
            if (ret != ServiceResult.Success) {
                Log.i(TAG, "m1card: ppppppppppppppppppppppppppppppppppppppppp");
//                alertDialogOnShowListener.showMessage(Utils.hex2asc(uid, 0, 8, 1));
//                  showmsg("ID: " + Utils.hex2asc(uid, 0, 8, 1));

                Message message = Message.obtain();
                message.what = 100;
                message.obj = GeneralUtils.ByteArrayToHexString(
                        (uid == null) ? "".getBytes() :uid);
                handler.sendMessage(message);
                return;
            }

            int len = 16;
            byte buf[] = new byte[len];
            byte value[] = new byte[]{(byte) 0x0A};
            byte write[] = new byte[]{(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78
                    , (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF
                    , (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78
                    , (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
//            Utils.memset(write, 0x0, write.length);
            write[4] = (byte) 0xff;
            write[5] = (byte) 0xff;
            write[6] = (byte) 0xff;
            write[7] = (byte) 0xff;

            write[12] = ~0;
            write[13] = 0;
            write[14] = ~0;
            write[15] = 0;

//            String s = String.format("M1Card Test\n");
//            ret = m1CardHandler.writeBlock(1, write);
//            s += String.format("writeBlock(%d)\n", ret);
//            ret = m1CardHandler.readBlock(0, buf);
//            s += String.format("readBlock0(%d):", ret) + byte2string(buf) + "\n";
//            ret = m1CardHandler.readBlock(1, buf);
//            s += String.format("readBlock1(%d):", ret) + byte2string(buf) + "\n";
//            Log.v("tag", "read block:" + s);
//
//            ret = m1CardHandler.operateBlock(M1CardOperType.INCREMENT, 1, value, 0);
//            Log.v("tag", "INCREMENT block:" + ret);
//            s += "INCREMENT block:" + Integer.toString(ret) + "\n";
//            ret = m1CardHandler.readBlock(1, buf);
//            s += String.format("readBlock1(%d):", ret) + byte2string(buf) + "\n";
//
//            ret = m1CardHandler.operateBlock(M1CardOperType.DECREMENT, 1, value, 0);
//            Log.v("tag", "DECREMENT block:" + ret);
//            s += "DECREMENT block:" + Integer.toString(ret) + "\n";
//            ret = m1CardHandler.readBlock(1, buf);
//            s += String.format("readBlock1(%d):", ret) + byte2string(buf) + "\n";
//
//            ret = m1CardHandler.operateBlock(M1CardOperType.BACKUP, 0, null, 1);
//            Log.v("tag", "BACKUP block:" + ret);
//            s += "BACKUP block:" + Integer.toString(ret) + "\n";
//            ret = m1CardHandler.readBlock(1, buf);
//            s += String.format("readBlock1(%d):", ret) + byte2string(buf) + "\n";

//            alertDialogOnShowListener.showMessage(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
