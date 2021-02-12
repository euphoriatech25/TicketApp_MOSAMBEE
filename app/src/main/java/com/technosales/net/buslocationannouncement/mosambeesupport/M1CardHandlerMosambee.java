package com.technosales.net.buslocationannouncement.mosambeesupport;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.card.mifare.M1CardHandler;
import com.morefun.yapi.card.mifare.M1KeyTypeConstrants;
import com.morefun.yapi.device.reader.icc.ICCSearchResult;
import com.morefun.yapi.device.reader.icc.IccCardType;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.technosales.net.buslocationannouncement.SDKManager;
import com.technosales.net.buslocationannouncement.additionalfeatures.PayByCardActivity;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;

import java.io.UnsupportedEncodingException;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.KEY_A;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.KEY_B;


public class M1CardHandlerMosambee {
    public static final byte[] KEY_DEFAULT =
            {(byte) 0xFF, (byte) 0xDB, (byte) 0x59, (byte) 0xCF, (byte) 0xF2, (byte) 0xF3,
                    (byte) 0x08, (byte) 0x77, (byte) 0x8F, (byte) 0xFF,
                    (byte) 0x2C, (byte) 0x1F, (byte) 0xEF, (byte) 0x93, (byte) 0xAA, (byte) 0x13};
    public static final byte[] KEY_A = {(byte) 0xFF, (byte) 0xDB, (byte) 0x59, (byte) 0xCF, (byte) 0xF2, (byte) 0xF3};
    public static final byte[] ACCESS_BITS = {(byte) 0x08, (byte) 0x77, (byte) 0x8F, (byte) 0xFF};
    public static final byte[] KEY_B = {(byte) 0x2C, (byte) 0x1F, (byte) 0xEF, (byte) 0x93, (byte) 0xAA, (byte) 0x13};
    private static final String TAG = "M1CardHandlerTest";

    public static void test_m1card(Handler handler) {
        DeviceServiceEngine mSDKManager;
        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            Log.e("TAG", "ServiceEngine is Null");
            return;
        }
        new SearchCardOrCardReaderTest(mSDKManager).searchRFCard(new String[]{IccCardType.M1CARD}, new PayByCardActivity.OnSearchListener() {
            @Override
            public void onSearchResult(int retCode, Bundle bundle) {
                if (ServiceResult.Success == retCode) {
                    String cardType = bundle.getString(ICCSearchResult.CARDTYPE);
                    if (IccCardType.M1CARD.equals(cardType)) {
                        try {
                            M1CardHandler m1CardHandler = mSDKManager.getM1CardHandler(mSDKManager.getIccCardReader(IccReaderSlot.RFSlOT));
                            if (m1CardHandler == null) {
//                              alertDialogOnShowListener.showMessage(getString(R.string.msg_readfail_retry));
                                return;
                            }
                            byte[] uid = new byte[4];
                            int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, 0, KEY_A, uid);
                            if (ret != ServiceResult.Success) {
                                Message message = Message.obtain();
                                message.what = 100;
                                message.obj = GeneralUtils.ByteArrayToHexString(
                                        (uid == null) ? "".getBytes() : uid);
                                handler.sendMessage(message);
                                return;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
//                    alertDialogOnShowListener.showMessage(getString(R.string.msg_icorrfid));
                }
            }
        });
    }

    public static void registerDetails(Handler handler, String customerDetails, int customerDetailsBlock) {
        DeviceServiceEngine mSDKManager;
        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            Log.e("TAG", "ServiceEngine is Null");
            return;
        }
        new SearchCardOrCardReaderTest(mSDKManager).searchRFCard(new String[]{IccCardType.M1CARD}, new PayByCardActivity.OnSearchListener() {
            @Override
            public void onSearchResult(int retCode, Bundle bundle) {
                if (ServiceResult.Success == retCode) {
                    String cardType = bundle.getString(ICCSearchResult.CARDTYPE);
                    if (IccCardType.M1CARD.equals(cardType)) {
                        try {
                            M1CardHandler m1CardHandler = mSDKManager.getM1CardHandler(mSDKManager.getIccCardReader(IccReaderSlot.RFSlOT));
                            if (m1CardHandler == null) {
//                              alertDialogOnShowListener.showMessage(getString(R.string.msg_readfail_retry));
                                return;
                            }
                            byte[] uid = new byte[4];
                            int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, customerDetailsBlock, KEY_A, uid);
                            if (ret != ServiceResult.Success) {
                                ret = m1CardHandler.writeBlock(customerDetailsBlock, customerDetails.getBytes());
                                Log.i(TAG, "onSearchResult: " + ret);
                                if (ret == 0) {
                                    Message message = Message.obtain();
                                    message.what = 100;
                                    message.obj = GeneralUtils.ByteArrayToHexString(
                                            (uid == null) ? "".getBytes() : uid);
                                    handler.sendMessage(message);
                                }
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
//                    alertDialogOnShowListener.showMessage(getString(R.string.msg_icorrfid));
                }
            }
        });
    }

    public static void gettingCustomerDetails(Handler handler, int[] customerDetailsBlock) {
        DeviceServiceEngine mSDKManager;
        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            Log.e("TAG", "ServiceEngine is Null");
            return;
        }
        new SearchCardOrCardReaderTest(mSDKManager).searchRFCard(new String[]{IccCardType.M1CARD}, new PayByCardActivity.OnSearchListener() {
            @Override
            public void onSearchResult(int retCode, Bundle bundle) {
                if (ServiceResult.Success == retCode) {
                    String cardType = bundle.getString(ICCSearchResult.CARDTYPE);
                    if (IccCardType.M1CARD.equals(cardType)) {
                        try {
                            M1CardHandler m1CardHandler = mSDKManager.getM1CardHandler(mSDKManager.getIccCardReader(IccReaderSlot.RFSlOT));
                            if (m1CardHandler == null) {
//                              alertDialogOnShowListener.showMessage(getString(R.string.msg_readfail_retry));
                                return;
                            }
                            byte[] uid = new byte[4];
                            int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, 12, KEY_A, uid);
                            Log.i(TAG, "onSearchResult111111: "+ret);
                            if (ret == ServiceResult.Success) {
                                readCustomerDetails(handler, m1CardHandler, customerDetailsBlock);
//                                return;
                            }

//                            readCustomerDetails(handler, m1CardHandler, customerDetailsBlock);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
//                    alertDialogOnShowListener.showMessage(getString(R.string.msg_icorrfid));
                }
            }
        });
    }

    public static void readCustomerDetails(Handler handlerTransaction, M1CardHandler m1CardHandler, int[] blockList) {

        byte[] customerId = null;
        byte[] customerAmt = null;
        byte[] customerHash = null;
        byte[] customerTranNo = null;
        int ret;
        int len = 8;
        byte buf[] = new byte[64];
        byte write[] = new byte[]{(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78
                , (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF
                , (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78
                , (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
        try {
            ret = m1CardHandler.readBlock(12, buf);
                byte[] data = Base64.decode(buf, Base64.DEFAULT);
                String str1 = null;
                try {
                    str1 = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "read1111: " + ret+"     "+str1);

//                Message messageCustomerId = new Message();
//                messageCustomerId.what = 101;
//                messageCustomerId.obj = str1;
//                handlerTransaction.sendMessage(messageCustomerId);



//            ret = m1CardHandler.readBlock((byte) blockList[0], customerId);
//            if (customerAmt != null) {
//                byte[] data = Base64.decode(customerAmt, Base64.DEFAULT);
//                String str1 = null;
//                try {
//                    str1 = new String(data, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                Log.i(TAG, "read: " + str1);
//
//                Message messageCustomerAmt = new Message();
//                messageCustomerAmt.what = 102;
//                messageCustomerAmt.obj = str1;
//                handlerTransaction.sendMessage(messageCustomerAmt);
//            }
//
//
//            ret = m1CardHandler.readBlock((byte) blockList[0], customerId);
//            if (customerHash != null) {
//                byte[] data = Base64.decode(customerHash, Base64.DEFAULT);
//                String str1 = null;
//                try {
//                    str1 = new String(data, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                Log.i(TAG, "read: " + str1);
//
//                Message messageCustomerHash = new Message();
//                messageCustomerHash.what = 103;
//                messageCustomerHash.obj = str1;
//                handlerTransaction.sendMessage(messageCustomerHash);
//            }
//
//
//            ret = m1CardHandler.readBlock((byte) blockList[0], customerId);
//            if (customerTranNo != null) {
//                byte[] data = Base64.decode(customerTranNo, Base64.DEFAULT);
//                String str1 = null;
//                try {
//                    str1 = new String(data, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                Log.i(TAG, "read: " + str1);
//
//                Message messageCustomerTranNo = new Message();
//                messageCustomerTranNo.what = 104;
//                messageCustomerTranNo.obj = str1;
//                handlerTransaction.sendMessage(messageCustomerTranNo);
//            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

